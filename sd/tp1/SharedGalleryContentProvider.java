package sd.tp1;


import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import sd.tp1.clt.ws.AlbumAlreadyExistsException_Exception;
import sd.tp1.clt.ws.AlbumNotFoundException_Exception;
import sd.tp1.clt.ws.GalleryNotFoundException_Exception;
import sd.tp1.clt.ws.GalleryServerImplWS;
import sd.tp1.clt.ws.GalleryServerImplWSClass;
import sd.tp1.clt.ws.GalleryServerImplWSClassService;
import sd.tp1.clt.ws.GalleryServerImplWSService;
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

	/*TODO:
	 * Em relação a cache, podemos tambem gravar diretamente no pc do utilizador 
	 * (usando o serializable do java) e depois era so uma questão de saber se houve
	 * alterações
	 * 
	 * Para o error handling neste momento fazemos 3 tentativas para ir buscar o recurso que 
	 * queremos, no futuro, com dois servidores, podemos fazer duas tentativas para um
	 * e depois mudar para o outro e assim sucessivamente. 
	 * Embora isto dependa bastante de como vamos fazer as coisas, se por replicação, ou divisão
	 * (o ideal era os dois!!!)
	 * nota: exstem diferenças nos erros, entre, por exemplo, não encontrar uma galeria
	 * e não conseguir fazer a conexão
	 */
	Gui gui;
	//TODO: tive de mudar para class,
	//mas temos de ver isto melhor,
	//devia dar com a interface...
	private GalleryServerImplWSClass server;

	SharedGalleryContentProvider() {
		MulticastDiscovery discovery = new MulticastDiscovery();
		URL serviceURL = discovery.findService("GalleryServerSOAP");
		System.out.println(serviceURL);
		GalleryServerImplWSClassService service = new GalleryServerImplWSClassService(serviceURL);
		this.server = service.getGalleryServerImplWSClassPort();
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
		//TODO: cache?
		boolean executed = false;
		List<String> albums = null;
		for (int i =0; !executed && i<3; i++){
			try {
				albums = server.listAlbums();
				executed = true;
			} catch (GalleryNotFoundException_Exception e1){
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
					//albums = new ArrayList<String>();
					return null;
				}
			}
		}
		List<Album> lst = new ArrayList<Album>();
		for(String a: albums)
			lst.add( new SharedAlbum(a));
		return lst;
	}

	/**
	 * Returns the list of pictures for the given album. 
	 * On error this method should return null.
	 */
	@Override
	public List<Picture> getListOfPictures(Album album) {
		//TODO: cache?
		List<String> pictures = null;
		boolean executed = false;
		for (int i =0; !executed && i<3; i++){
			try {
				pictures = server.listPictures(album.getName());
				executed = true;
			} catch (AlbumNotFoundException_Exception e1){
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
					//pictures = new ArrayList<String>();
					return null;
				}

			} 
		}
		List<Picture> lst = new ArrayList<Picture>();
		for(String p: pictures)
			lst.add( new SharedPicture(p));
		return lst;
	}

	/**
	 * Returns the contents of picture in album.
	 * On error this method should return null.
	 */
	@Override
	public byte[] getPictureData(Album album, Picture picture) {
		//TODO: put picture on cache
		byte [] pic = null;
		boolean executed = false;
		for (int i =0; !executed && i<3; i++){
			try {
				PictureClass p = server.getPicture(album.getName(), picture.getName());
				pic = p.getContents();
				executed = true;
				//tratamento dos vários erros possiveis
			} catch (AlbumNotFoundException_Exception e1){
				System.err.println("Erro: " + e1.getMessage());
				return null;
			} catch (IOException_Exception e1){
				System.err.println("Erro: " + e1.getMessage());
				return null;
			} catch (PictureNotfoundException_Exception e1){
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
		return pic;
	}

	/**
	 * Create a new album.
	 * On error this method should return null.
	 */
	@Override
	public Album createAlbum(String name) {
		// TODO: put this album on cache
		boolean executed = false;
		for (int i =0; !executed && i<3; i++){
			try{
				server.creatAlbum(name);
				executed = true;
			} catch (AlbumAlreadyExistsException_Exception e1){
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
		return new SharedAlbum(name);
	}

	/**
	 * Delete an existing album.
	 */
	@Override
	public void deleteAlbum(Album album) {
		//TODO: retirar o album da cache?!
		boolean executed = false;
		for (int i =0; !executed && i<3; i++){
			try{
				server.deleteAlbum(album.getName());
				executed = true;
			} catch (AlbumNotFoundException_Exception e1){
				System.err.println("Erro: " + e1.getMessage());
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
				}
			}
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
}
