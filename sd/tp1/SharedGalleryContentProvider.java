package sd.tp1;


import java.io.IOException;
import java.net.MulticastSocket;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;



import java.util.Random;



import sd.tp1.clt.ws.AlbumAlreadyExistsException_Exception;
import sd.tp1.clt.ws.AlbumNotFoundException_Exception;
import sd.tp1.clt.ws.GalleryServerImplWSClass;
import sd.tp1.clt.ws.IOException_Exception;
import sd.tp1.clt.ws.PictureAlreadyExistsException_Exception;
import sd.tp1.clt.ws.PictureClass;
import sd.tp1.clt.ws.PictureNotfoundException_Exception;
import sd.tp1.common.MulticastDiscovery;
import sd.tp1.gui.GalleryContentProvider;
import sd.tp1.gui.Gui;

/*
 * This class provides the album/picture content to the gui/main application.
 * 
 * Project 1 implementation should complete this class. 
 */
public class SharedGalleryContentProvider implements GalleryContentProvider{

	Gui gui;
	//TODO: tive de mudar para class,
	//mas temos de ver isto melhor,
	//devia dar com a interface...
	private GalleryServerImplWSClass server;
	private MulticastDiscovery discovery;
	public MulticastSocket socket;

	private List<serverObjectClass> servers;

	SharedGalleryContentProvider() {
		servers = Collections.synchronizedList(new LinkedList<serverObjectClass>());
		discovery = new MulticastDiscovery();
		try {
			socket = new MulticastSocket();
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.sendRequests();
		this.registServer();
		
		try {
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	/**
	 *  Downcall from the GUI to register itself, so that it can be updated via upcalls.
	 */
	@Override
	public void register(Gui gui) {
		if( this.gui == null ) {
			this.gui = gui;
		}
	}

	/**
	 * Returns the list of albums in the system.
	 * On error this method should return null.
	 */
	@Override
	public List<Album> getListOfAlbums() {
		List<String> albuns = new ArrayList<String>();
		List<Album> toReturn = new ArrayList<Album>();
		if (servers != null){
			for (serverObjectClass server: servers){
				if (server != null){
					try{
						List <String> al = server.getServer().getAlbums();
						//adicionar ao albuns para devolver
						albuns.addAll(al);
						//adicionar ao serverObjectClass
						server.addListAlbuns(al);
					}catch (Exception e ){
						return null;
					}
				}
			}
			for(String a: albuns)
				toReturn.add( new SharedAlbum(a));
		}
		else return null;
		
		
		return toReturn;
	}

	/**
	 * Returns the list of pictures for the given album. 
	 * On error this method should return null.
	 */
	@Override
	public List<Picture> getListOfPictures(Album album) {
		serverObjectClass s = this.findServer(album.getName());
		if(s!= null){
			RequestInterface i = s.getServer();
			List<String> pictNames = i.getPictures(album.getName());
			List<Picture> lst = new ArrayList<Picture>();
			for(String p: pictNames)
				lst.add( new SharedPicture(p));
			return lst;
		}
		return null;
	}

	/**
	 * Returns the contents of picture in album.
	 * On error this method should return null.
	 */
	@Override
	public byte[] getPictureData(Album album, Picture picture) {	
		serverObjectClass s = this.findServer(album.getName());
		if(s!= null){
			RequestInterface i = s.getServer();
			return i.getPicture(album.getName(), picture.getName());
		}
		return null;
	}

	/**
	 * Create a new album.
	 * On error this method should return null.
	 */
	@Override
	public Album createAlbum(String name) {
		serverObjectClass s = this.findServer(name);
		if(s== null){
			Random r = new Random();
			int i = r.nextInt(servers.size());
			serverObjectClass server = servers.get(i);
			server.getServer().createAlbum(name);
			server.addAlbum(name);
			return new SharedAlbum(name);
		}
		else
			return null;
	}

	/**
	 * Delete an existing album.
	 */
	@Override
	public void deleteAlbum(Album album) {
		serverObjectClass s = this.findServer(album.getName());
		if(s!= null){
			s.getServer().deleteAlbum(album.getName());
			s.deleteAlbum(album.getName());
		}
		
	}
	
	/**
	 * Add a new picture to an album.
	 * On error this method should return null.
	 */
	@Override
	public Picture uploadPicture(Album album, String name, byte[] data) {
		
		
		
		
		
		//TODO: put picture on cache
		boolean executed = false;
		for (int i =0; !executed && i<3; i++){
			try{
				PictureClass pic = new PictureClass();
				pic.setContents(data);
				pic.setName(name);
				server.uploadPicture(album.getName(),data, name);
				executed = true;
			} catch (AlbumNotFoundException_Exception e1){
				System.err.println("Erro: " + e1.getMessage());
				return null;
			} catch (IOException_Exception e1){
				System.err.println("Erro: " + e1.getMessage());
				return null;
			} catch (PictureAlreadyExistsException_Exception e1){
				System.err.println("Erro: " + e1.getMessage());
				return null;
			} catch (Exception e) {
				if(i < 2){
					try {
						Thread.sleep(500); //wait some time
					} catch (InterruptedException e1) {
						//do nothing
					}
				}
				else {
					System.err.println("Erro: " + e.getMessage());
					return null;
				}

			}
		}
		return new SharedPicture(name);
	}

	/**
	 * Delete a picture from an album.
	 * On error this method should return false.
	 */
	@Override
	public boolean deletePicture(Album album, Picture picture) {
		boolean executed = false;
		for (int i =0; !executed && i<3; i++){
			try{
				server.deletePicture(album.getName(), picture.getName());
				executed = true;
			} catch (AlbumNotFoundException_Exception e1){
				System.err.println("Erro: " + e1.getMessage());
				return false;
			} catch (PictureNotfoundException_Exception e1){
				System.err.println("Erro: " + e1.getMessage());
				return false;
			} catch (Exception e) {
				if(i < 2){
					try {
						Thread.sleep(500); //wait some time
					} catch (InterruptedException e1) {
						//do nothing
					}
				}
				else {
					System.err.println("Erro: " + e.getMessage());
					return false;
				}
			}
		}
		return true;
	}

	
	/**
	 * @param album
	 * @return the server of the album, or null
	 */
	private serverObjectClass findServer(String album){
		try{
			for (serverObjectClass server: servers){
				if (server.containsAlbuns(album)){
					return server;
				}
			}
		}catch (Exception e){
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Represents a shared album.
	 */
	static class SharedAlbum implements GalleryContentProvider.Album {
		final String name;

		SharedAlbum(String name) {
			this.name = name;
		}

		@Override
		public String getName() {
			return name;
		}
	}

	/**
	 * Represents a shared picture.
	 */
	static class SharedPicture implements GalleryContentProvider.Picture {
		final String name;

		SharedPicture(String name) {
			this.name = name;
		}

		@Override
		public String getName() {
			return name;
		}
	}



/**
 * to send the requests to the network
 */
private void sendRequests(){
	new Thread(() -> {
		try {
			while (true){
				Iterator<serverObjectClass> i = servers.iterator();
				while(i.hasNext()){
					serverObjectClass s = i.next();
					
					if(s.getCounter() == 5){
						System.out.println("Removing server: " + s.getServerName());
						i.remove();
						//servers.remove(s);
						gui.updateAlbums();
					}
					else
						s.incrementCounter();
				}
				discovery.findService(socket);
				Thread.sleep(1000);
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
	new Thread(() -> {
		try {
			while (true){
				URI serviceURI = discovery.getService(socket);
				if(serviceURI!=null){
					String [] compare = serviceURI.toString().split("/");
					RequestInterface sv = null;
					
					boolean exits = false;
					for (serverObjectClass s: servers){
						if (s.equals(serviceURI.toString())){
							exits = true;
							s.resetCounter();
							break;
						}
					}
					if (!exits){
						if(compare[3].equalsIgnoreCase(SERVER_SOAP)){
							sv = new SOAPClientClass(serviceURI);

						}
						else if(compare[3].equalsIgnoreCase(SERVER_REST)){
							sv = new RESTClientClass(serviceURI);
						}
						System.out.println("Adding server: " + serviceURI.toString() );
						serverObjectClass obj = new serverObjectClass(sv, serviceURI.toString());
						servers.add(obj);
						gui.updateAlbums();
					}
					//the server already exits
//					else{
//						for (serverObjectClass s: servers){
//							if (s.equals(serviceURI.toString())){
//								s.resetCounter();
//							}
//						}
//
//					}
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		};
	}).start();
}




}
