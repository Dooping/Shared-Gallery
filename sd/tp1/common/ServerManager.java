package sd.tp1.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.net.MulticastSocket;
import java.net.URI;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import sd.tp1.RESTClientClass;
import sd.tp1.RequestInterface;
import sd.tp1.SOAPClientClass;
import sd.tp1.ServerObjectClass;
import sd.tp1.common.UtilsClass;

public class ServerManager {
	protected static final int DISCOVERY_INTERVAL = 1000;
	protected static final int REPLICATION_INTERVAL = 10000;
	protected static final int REPLICATION_DELAY = 21000;
	protected static final int TIMEOUT_CYCLES = 5;
	protected static final int NUMBER_OF_REPLICS = 2;
	protected static final int SYNCHRONIZATION_DELAY = 10000;
	protected static final int SYNCHRONIZATION_CYCLE = 10000;
	protected static final int GARBAGE_INTERVAL = 1000000;

	protected MulticastDiscovery discovery;
	protected MulticastSocket socket;
	protected List<ServerObjectClass> servers;

	public ServerManager() {
		servers = Collections.synchronizedList(new CopyOnWriteArrayList<ServerObjectClass>());
		discovery = new MulticastDiscovery();
		try {
			socket = new MulticastSocket();
		} catch (IOException e) {
			//e.printStackTrace();
		}
		this.sendRequests();
		this.registServer();
		this.albumReplicationThread();
		this.albumSynchronizationThread();
		this.garbageCollector();
	}

	/**
	 * to send the requests to the network
	 */
	protected void sendRequests(){
		new Thread(() -> {
			try {
				while (true){
					Iterator<ServerObjectClass> i = servers.iterator();
					while(i.hasNext()){
						ServerObjectClass s = i.next();

						if(s.getCounter() == TIMEOUT_CYCLES && s.isConnected()){
							System.out.println("Removing server: " + s.getServerName());
							s.setConnected(false);
						}
						else if (s.isConnected())
							s.incrementCounter();
					}
					discovery.findService(socket);
					Thread.sleep(DISCOVERY_INTERVAL);
					//System.out.println(servers.toString());
				}
			}catch(Exception e){
				e.printStackTrace();
			};
		}).start();
	}


	/**
	 * to catch the servers 
	 */
	protected void registServer (){
		String SERVER_SOAP = "GalleryServerSOAP";
		String SERVER_REST = "GalleryServerREST";
		String IMGUR_REST = "GalleryServerImgur";
		new Thread(() -> {
			try {
				while (true){
					URI serviceURI = discovery.getService(socket);
					if(serviceURI!=null){
						String [] compare = serviceURI.toString().split("/");
						RequestInterface sv = null;

						boolean exits = false;
						for (ServerObjectClass s: servers){
							if (s.getServerName().equals(serviceURI.toString())){
								exits = true;
								s.resetCounter();
								if(!s.isConnected()){
									s.setConnected(true);
									System.out.println("Adding server: " + serviceURI.toString() );
								}
								break;
							}
						}
						if (!exits){
							if(compare[3].equalsIgnoreCase(SERVER_SOAP)){
								sv = new SOAPClientClass(serviceURI);

							}
							else if(compare[3].equalsIgnoreCase(SERVER_REST)|| compare[3].equalsIgnoreCase(IMGUR_REST)){
								sv = new RESTClientClass(serviceURI);
							}
							System.out.println("Adding server: " + serviceURI.toString() );
							ServerObjectClass obj = new ServerObjectClass(sv, serviceURI.toString());
							servers.add(obj);
							Collections.sort(servers, new Comparator<ServerObjectClass>(){
								@Override
								public int compare(ServerObjectClass o1, ServerObjectClass o2){
									return o1.getServerName().compareTo(o2.getServerName());
								}
							}); 
						}
					}
				}
			}catch(Exception e){
				e.printStackTrace();
			};
		}).start();
	}

	/**
	 * Thread responsible for checking if any album needs replication
	 */
	protected void albumReplicationThread(){
		new Thread(() -> {
			try {
				Thread.sleep(REPLICATION_DELAY);
				File basePath = new File("./gallery");
				while (true){
					int onlineServers = 0;
					Map<String,AlbumClass> albums = new HashMap<>();
					for(ServerObjectClass s : servers)
						if(s.isConnected())
							try{
								List<AlbumFolderClass> as = s.getServer().getAlbums();
								if(as!=null){
									onlineServers++;
									for(AlbumFolderClass album : as){
										AlbumClass a = albums.get(album.name);
										if(a != null)
											a.addServer(s);
										else
											albums.put(album.name, new AlbumClass(album.name, s));
									}
									s.addListAlbuns(as);
								}
							} catch(Exception e){
								e.printStackTrace();
							}
					ArrayList<File> names = new ArrayList<File>(Arrays.asList(basePath.listFiles()));
					ObjectInputStream input;
					ArrayList<AlbumFolderClass> selfAlbums = new ArrayList<>();
					for(File f : names){
						try {
							input = new ObjectInputStream(new FileInputStream(f));
							selfAlbums.add((AlbumFolderClass)input.readObject());
							input.close();
						} catch (IOException e) {
						} catch (ClassNotFoundException e) {
						}
					}
					for(AlbumFolderClass selfAlbum : selfAlbums){
						AlbumClass album = albums.get(selfAlbum.name);
						if(album.getServers().size()<NUMBER_OF_REPLICS && onlineServers >= NUMBER_OF_REPLICS){
							replicateAlbumToServer(servers.get(UtilsClass.getNextServerIndex(servers, selfAlbum.name)), selfAlbum.name);
						}
					}
					Thread.sleep(REPLICATION_INTERVAL);
				}
			}catch(Exception e){
				e.printStackTrace();
			};
		}).start();
	}

	/**
	 * Uploads an album to the given server
	 * @param s - server
	 * @param album - album to upload
	 */
	protected void replicateAlbumToServer(ServerObjectClass s, String album){
		try{
			RequestInterface server = s.getServer();
			//System.out.println(s);
			boolean response = server.createAlbum(album);
			if (response){
				File albumFolder = new File("./gallery/"+album);
				List<File> files = new ArrayList<File>(Arrays.asList(albumFolder.listFiles()));
				for(File pic : files)
					if(!pic.getName().equals("album.dat")){
						//System.out.println("uploading pic: " + pic.getName());
						server.uploadPicture(album, pic.getName(), Files.readAllBytes(pic.toPath()));
					}
						

			}
		} catch(Exception e){}

	}

	/**
	 * Thread checking if own albums are synchronized with every other server's albums
	 */
	protected void albumSynchronizationThread(){
		new Thread(() -> {
			try {
				Thread.sleep(SYNCHRONIZATION_DELAY);
				int step = SYNCHRONIZATION_CYCLE/servers.size();
				while (true){
					for(ServerObjectClass s : servers)
						if(s.isConnected()){
							synchronizationAlbum(s);
						}
					Thread.sleep(step);
				}
			}catch(Exception e){
				e.printStackTrace();
			};
		}).start();
	}

	/**
	 * Checks if album information from the given server is different from local information and updates it if necessary
	 * @param s - server
	 */
	protected void synchronizationAlbum(ServerObjectClass s){
		List<AlbumFolderClass> otherAlbums = s.getServer().getAlbums();
		if(otherAlbums==null)
			return;
		File basePath = new File("./gallery");
		if (basePath.exists()){
			ArrayList<File> names = new ArrayList<File>(Arrays.asList(basePath.listFiles()));
			ObjectInputStream input;
			ArrayList<AlbumFolderClass> selfAlbums = new ArrayList<>();
			for(File f : names){
				try {
					input = new ObjectInputStream(new FileInputStream(f));
					selfAlbums.add((AlbumFolderClass)input.readObject());
					input.close();
				} catch (IOException e) {
				} catch (ClassNotFoundException e) {
				}
			}
			for(AlbumFolderClass a : selfAlbums)
				if(otherAlbums.contains(a)){
					AlbumFolderClass oa = otherAlbums.get(otherAlbums.indexOf(a));
					if(oa.isErased()!= a.isErased()){
						if(oa.lamportClock.compareTo(a.lamportClock) > 0){
							if(oa.isErased() && !a.isErased()){
								a.erase(oa.serverUrl);
								
							}
								
							else if(!oa.isErased() && a.isErased()){
								a.recreate(oa.serverUrl);
							}
							ObjectOutput out;
							try {
								out = new ObjectOutputStream(new FileOutputStream(new File(basePath,a.name+".dat")));
								out.writeObject(a);
								out.close();
							} catch (IOException e) {}
						}
					}
					synchronizationPictures(s, a.name);
				}
		}
	}

	/**
	 * Checks if information about the album's pictures in the given server is different from local information and updates it if necessary, downloading also the picture
	 * @param s - server
	 * @param album - album to synchronize
	 */
	@SuppressWarnings("unchecked")
	protected void synchronizationPictures(ServerObjectClass s, String album){
		File basePath = new File("./gallery");
		File ownAlbumDat = new File(basePath, album+"/album.dat");
		ObjectInputStream input;
		List<PictureClass> ownPictures = null;
		try {
			input = new ObjectInputStream(new FileInputStream(ownAlbumDat));
			ownPictures = (List<PictureClass>)input.readObject();
			input.close();
		} catch (IOException e) {
		} catch (ClassNotFoundException e) {
		}
		List<PictureClass> otherPictures = s.getServer().getPictures(album);
		for(PictureClass p : otherPictures)
			if(!ownPictures.contains(p)){
				byte[] picture = s.getServer().getPicture(album, p.name);
				FileOutputStream out;
				try {
					out = new FileOutputStream(new File(basePath, album+"/"+p.name));
					out.write(picture);
					out.close();
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
				ownPictures.add(new PictureClass(p.name, s.getServerName()));
			}
			else{
				PictureClass ownPic = ownPictures.get(ownPictures.indexOf(p));
				if(p.isErased()!=ownPic.isErased()  || p.lamportClock.lamportNumber!= ownPic.lamportClock.lamportNumber)
					if(p.lamportClock.compareTo(ownPic.lamportClock) > 0){
						if(!p.isErased()){
							byte[] picture = s.getServer().getPicture(album, p.name);
							FileOutputStream out;
							try {
								out = new FileOutputStream(new File(basePath, album+"/"+p.name));
								out.write(picture);
								out.close();
							} catch (FileNotFoundException e) {
								e.printStackTrace();
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
						ownPic.erased = p.erased;
						ownPic.lamportClock = p.lamportClock;
					}
			}

		ObjectOutput out;
		try {
			out = new ObjectOutputStream(new FileOutputStream(ownAlbumDat));
			out.writeObject(ownPictures);
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}


	@SuppressWarnings("unchecked")
	protected void garbageCollector(){
		new Thread(() -> {
			File basePath = new File("./gallery");

			try {
				while(true){
					Thread.sleep(GARBAGE_INTERVAL);
					ObjectInputStream input;
					ArrayList<File> names = new ArrayList<File>(Arrays.asList(basePath.listFiles()));
					ArrayList<AlbumFolderClass> albums = new ArrayList<>();
					for(File f : names){
						try {
							input = new ObjectInputStream(new FileInputStream(f));
							albums.add((AlbumFolderClass)input.readObject());
							input.close();

							input.close();
						} catch (IOException e) {
						} catch (ClassNotFoundException e) {
						}
					}

					for(AlbumFolderClass al: albums){
						if(al.isErased()){
							this.deleteDir(new File(basePath,al.name));
							this.deleteDir(new File(basePath,al.name+".dat"));
						}

						else{
							List<PictureClass> pictures;
							File f = new File(basePath, al.name+"/album.dat");
							input = new ObjectInputStream(new FileInputStream(f));
							pictures = (LinkedList<PictureClass>)input.readObject();
							input.close();
							List<PictureClass> newSetPictures = new LinkedList<PictureClass>();
							for(PictureClass p: pictures){
								if(p.isErased()){
									File pic = new File(basePath, al.name +"/" + p.name);
									deleteDir(pic);
								}
								else
									newSetPictures.add(p);
							}

							ObjectOutput outt;
							outt = new ObjectOutputStream(new FileOutputStream(f));
							outt.writeObject(newSetPictures);
							outt.close();
							
						}	
					}
				}

			} catch (Exception e) {
				e.printStackTrace();
			}

		}).start();
	}

	/**
	 * @param file
	 * to delete a directory and all of it's content's
	 */
	protected void deleteDir(File file) {
		File[] contents = file.listFiles();
		if (contents != null) {
			for (File f : contents) {
				deleteDir(f);
			}
		}
		file.delete();
	}
}


