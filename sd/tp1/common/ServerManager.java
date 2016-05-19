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
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.Response;

import sd.tp1.RESTClientClass;
import sd.tp1.RequestInterface;
import sd.tp1.SOAPClientClass;
import sd.tp1.ServerObjectClass;
import sd.tp1.common.UtilsClass;

public class ServerManager {
	public static final int DISCOVERY_INTERVAL = 1000;
	public static final int REPLICATION_INTERVAL = 10000;
	public static final int REPLICATION_DELAY = 30000;
	public static final int TIMEOUT_CYCLES = 5;
	public static final int NUMBER_OF_REPLICS = 2;
	public static final int SYNCHRONIZATION_DELAY = 10000;
	public static final int SYNCHRONIZATION_CYCLE = 10000;
	public static final int GARBAGE_INTERVAL = 100000;
	
	private MulticastDiscovery discovery;
	public MulticastSocket socket;
	private List<ServerObjectClass> servers;
	
	public ServerManager() {
		servers = Collections.synchronizedList(new LinkedList<ServerObjectClass>());
		
		discovery = new MulticastDiscovery();
		try {
			socket = new MulticastSocket();
		} catch (IOException e) {
			//e.printStackTrace();
		}
		this.sendRequests();
		this.registServer();
		//this.albumReplicationThread();
		this.albumSynchronizationThread();
	}
	
	/**
	 * to send the requests to the network
	 */
	private void sendRequests(){
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
			};
		}).start();
	}


	/**
	 * to catch the servers 
	 */
	private void registServer (){
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
							if (s.equals(serviceURI.toString())){
								exits = true;
								s.resetCounter();
								s.setConnected(true);
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
				//e.printStackTrace();
			};
		}).start();
	}
	
//	private void albumReplicationThread(){
//		new Thread(() -> {
//			try {
//				Thread.sleep(REPLICATION_DELAY);
//				while (true){
//					Map<String,AlbumClass> albums = new HashMap<>();
//					for(ServerObjectClass s : servers)
//						if(s.isConnected())
//							try{
//								List<String> as = s.getServer().getAlbums();
//								s.addListAlbuns(as);
//								for(String albumName : as){
//									AlbumClass a = albums.get(albumName);
//									if(a != null)
//										a.addServer(s);
//									else
//										albums.put(albumName, new AlbumClass(albumName, s));
//								}
//							} catch(Exception e){}
//					
//					ArrayList<String> names = new ArrayList<String>(Arrays.asList(new File("./gallery").list()));
//					for(String name : names){
//						AlbumClass album = albums.get(name);
//						if(album.getServers().size()<NUMBER_OF_REPLICS && servers.size() >= NUMBER_OF_REPLICS){
//							replicateAlbumToServer(servers.get(UtilsClass.getNextServerIndex(servers, name)), name);
//						}
//					}
//					Thread.sleep(REPLICATION_INTERVAL);
//				}
//			}catch(Exception e){
//			};
//		}).start();
//	}
	
	private void replicateAlbumToServer(ServerObjectClass s, String album){
		try{
			RequestInterface server = s.getServer();
			//System.out.println(s);
			boolean response = server.createAlbum(album);
			if (response){
				File albumFolder = new File("./gallery/"+album);
				ArrayList<String> names = new ArrayList<String>(Arrays.asList(albumFolder.list()));
				for(String pic : names){
					server.uploadPicture(album, pic, Files.readAllBytes(Paths.get("./gallery"+"/"+album + "/"+ pic)), false);
				}
			}
		} catch(Exception e){}
		
	}
	
	private void albumSynchronizationThread(){
		new Thread(() -> {
			try {
				Thread.sleep(SYNCHRONIZATION_DELAY);
				int step = SYNCHRONIZATION_CYCLE/servers.size();
				while (true){
					for(ServerObjectClass s : servers)
						if(s.isConnected()){
							System.out.println("akjhsflkajsf");
							synchronizationAlbum(s);
						}
					Thread.sleep(step);
				}
			}catch(Exception e){
				e.printStackTrace();
			};
		}).start();
	}
	
	private void synchronizationAlbum(ServerObjectClass s){
		List<AlbumFolderClass> otherAlbums = s.getServer().getAlbums();
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
					if(oa.isErased()!=a.isErased()){
						if(oa.getLamportClock().compareTo(a.getLamportClock()) < 0){
							if(oa.isErased())
								a.erase();
							else
								a.recreate();
							ObjectOutput out;
							try {
								out = new ObjectOutputStream(new FileOutputStream(new File(basePath,a.getName()+".dat")));
								out.writeObject(a);
								out.close();
							} catch (IOException e) {}
						}
						else{
							if(a.isErased())
								s.getServer().deleteAlbum(oa.getName());
							else
								s.getServer().createAlbum(oa.getName());
						}
					}
					synchronizationPictures(s, a.getName());
				}
		}
	}
	
	@SuppressWarnings("unchecked")
	private void synchronizationPictures(ServerObjectClass s, String album){
		File basePath = new File("./gallery");
		File ownAlbumDat = new File(basePath, album+".dat");
		ObjectInputStream input;
		List<PictureClass> ownPictures = null;
		try {
			input = new ObjectInputStream(new FileInputStream(ownAlbumDat));
			ownPictures = (LinkedList<PictureClass>)input.readObject();
			input.close();
		} catch (IOException e) {
		} catch (ClassNotFoundException e) {
		}
		List<PictureClass> otherPictures = s.getServer().getPictures(album);
		for(PictureClass p : ownPictures){
			if(otherPictures.contains(p)){
				//TODO comparar lamportClock
			} else
				try {
					s.getServer().uploadPicture(album, p.getName(), Files.readAllBytes(Paths.get(basePath+"/"+album + "/"+ p.getName())), true);
				} catch (IOException e) {
					e.printStackTrace();
				}
			
		}
		for(PictureClass p : otherPictures)
			if(!ownPictures.contains(p)){
				byte[] picture = s.getServer().getPicture(album, p.getName());
				FileOutputStream out;
				try {
					out = new FileOutputStream(new File(basePath, p.getName()));
					out.write(picture);
					out.close();
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
				ownPictures.add(new PictureClass(p.getName(), s.getServerName()));
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
	
	
	public void garbageCollector(){
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
					
					//TODO: iterar enquanto se apaga?!
					List <Integer> picToDelete = new LinkedList<Integer>();
					List <Integer> albumsToDelete = new LinkedList<Integer>();
					for(AlbumFolderClass al: albums){
						if(al.isErased()){
							this.deleteDir(new File(basePath,al.getName()));
							//TODO: apagar o .dat!
							this.deleteDir(new File(basePath,al.getName()+"/album.dat"));
						}
							
						else{
							List<PictureClass> pictures;
							File f = new File(basePath, al.getName()+"/album.dat");
							input = new ObjectInputStream(new FileInputStream(f));
							pictures = (LinkedList<PictureClass>)input.readObject();
							input.close();
							
							for(PictureClass p: pictures){
								if(p.isErased()){
									File pic = new File(basePath, al.getName() + p.getName());
									deleteDir(pic);
									//TODO:apagar a ref do server
									picToDelete.add(pictures.indexOf(p));
								}
							}
							//TODO: altamente ineficiente...
							input.close();
							//write do .dat with new list of pic
							if(!picToDelete.isEmpty()){
								List<PictureClass> list = new LinkedList<PictureClass>();
								for(int i = 0; i< pictures.size(); i++){
									if(!picToDelete.contains(i))
										list.add(pictures.get(i));
									
								}
								//apagar antigo .dat
								this.deleteDir(new File(basePath,al.getName()+"/album.dat"));
								//escrever o novo com a nova lista
								ObjectOutput outt;
								outt = new ObjectOutputStream(new FileOutputStream(f));
								outt.writeObject(list);
								outt.close();
							}
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
	private void deleteDir(File file) {
	    File[] contents = file.listFiles();
	    if (contents != null) {
	        for (File f : contents) {
	            deleteDir(f);
	        }
	    }
	    file.delete();
	}
}


