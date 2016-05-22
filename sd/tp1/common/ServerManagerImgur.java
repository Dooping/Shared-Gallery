package sd.tp1.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.net.MulticastSocket;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import sd.tp1.RequestInterface;
import sd.tp1.ServerObjectClass;
import sd.tp1.srv.imgur.ImgurClient;

public class ServerManagerImgur extends ServerManager {
	
	private ImgurClient imgur;
	
	public ServerManagerImgur(ImgurClient i) {
		this.imgur = i;
		servers = Collections.synchronizedList(new LinkedList<ServerObjectClass>());
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
	
	
	protected void replicateAlbumToServer(ServerObjectClass s, String album){
		try{
			RequestInterface server = s.getServer();
			//System.out.println(s);
			boolean response = server.createAlbum(album);
			if (response){
				//File albumFolder = new File("./gallery/"+album);
				//List<File> files = new ArrayList<File>(Arrays.asList(albumFolder.listFiles()));
				
				List <PictureClass> l = imgur.getPictures(album);
				
				for(PictureClass pic:l){
					System.out.println("Sending picure: " + pic.name);
					server.uploadPicture(album, pic.name, imgur.getPicture(album, pic.name));
				}
						
			}
		} catch(Exception e){}

	}
	
	/* (non-Javadoc)
	 * @see sd.tp1.common.ServerManager#synchronizationAlbum(sd.tp1.ServerObjectClass)
	 */
	@Override
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
					if(oa.isErased()!=a.isErased()){
						if(oa.lamportClock.compareTo(a.lamportClock) > 0){
							if(oa.isErased() && !a.isErased()){
								imgur.deleteAlbum(a.name);
								a.erase(oa.serverUrl);
							}
								
							else if(!oa.isErased() && a.isErased()){
								imgur.createAlbum(a.name);
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
	
	/* (non-Javadoc)
	 * @see sd.tp1.common.ServerManager#synchronizationPictures(sd.tp1.ServerObjectClass, java.lang.String)
	 */
	@Override
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
			//a foto ainda nunca esteve neste servidor 
			if(!ownPictures.contains(p)){
				byte[] picture = s.getServer().getPicture(album, p.name);
				System.out.println("Replicate the pic: " + p.name);
				imgur.uploadPicture(album, p.name, picture);
				ownPictures.add(new PictureClass(p.name, s.getServerName()));
			}
			//a foto já existe, verificar as versões
			else{
				PictureClass ownPic = ownPictures.get(ownPictures.indexOf(p));
				if(p.isErased()!=ownPic.isErased() || p.lamportClock.lamportNumber!= ownPic.lamportClock.lamportNumber)
					if(p.lamportClock.compareTo(ownPic.lamportClock) > 0){
						if(!p.isErased()){
							//se é a nossa que está apagada, não existe no server do imgur, pode-se 
							//enviar a nova sem problema
							byte[] picture = s.getServer().getPicture(album, p.name);
							imgur.uploadPicture(album, p.name, picture);
						}
						//a nossa deixa de estar apagada
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
	
	//No imgur, já não existe nada no servidor, é só apagar os .dat
	@SuppressWarnings("unchecked")
	@Override
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
							List<PictureClass> newSetPictures = new LinkedList<PictureClass>();
							System.out.println("Reading for album: " + al.name);
							File f = new File(basePath, al.name+"/album.dat");
							input = new ObjectInputStream(new FileInputStream(f));
							pictures = (LinkedList<PictureClass>)input.readObject();
							input.close();

							for(PictureClass p: pictures)
								if(!p.isErased())
									newSetPictures.add(p);

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

}
