package sd.tp1;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import sd.tp1.clt.ws.GalleryServerImplWS;
import sd.tp1.clt.ws.GalleryServerImplWSService;
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
	private GalleryServerImplWS server;

	SharedGalleryContentProvider() {
		MulticastDiscovery discovery = new MulticastDiscovery();
		URL serviceURL = discovery.findService("GalleryServer");
		System.out.println(serviceURL);

		GalleryServerImplWSService service = new GalleryServerImplWSService(serviceURL);

		this.server = service.getGalleryServerImplWSPort();
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
		List<String> albums;
		try {
			albums = server.listAlbums();
		} catch (Exception e) {
			System.err.println("Erro: " + e.getMessage());
			albums = new ArrayList<String>();
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
		List<String> pictures;
		try {
			pictures = server.listPictures(album.getName());
		} catch (Exception e) {
			System.err.println("Erro: " + e.getMessage());
			pictures = new ArrayList<String>();
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
		// TODO: obtain remote information 
		return null;
	}

	/**
	 * Create a new album.
	 * On error this method should return null.
	 */
	@Override
	public Album createAlbum(String name) {
		// TODO: contact servers to create album 
		return new SharedAlbum(name);
	}

	/**
	 * Delete an existing album.
	 */
	@Override
	public void deleteAlbum(Album album) {
		// TODO: contact servers to delete album 
	}
	
	/**
	 * Add a new picture to an album.
	 * On error this method should return null.
	 */
	@Override
	public Picture uploadPicture(Album album, String name, byte[] data) {
		// TODO: contact servers to add picture name with contents data 
		return new SharedPicture(name);
	}

	/**
	 * Delete a picture from an album.
	 * On error this method should return false.
	 */
	@Override
	public boolean deletePicture(Album album, Picture picture) {
		// TODO: contact servers to delete picture from album 
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
