package sd.tp1.gui;

import java.util.List;

/*
 * The GUI will request content to display from an object
 * implementing this interface.
 * 
 * Down calls allow the GUI to request data and delete albums/picture or
 * create new ones.
 * 
 * The GUI provides up calls (Gui interface) to allow the content provider
 * to update the GUI programmatically without user intervention.
 * 
 */
public interface GalleryContentProvider {

	/*
	 * Registers the GUI in the content provider to allow
	 * the provider to update the GUI programmatically (cf. Gui interface)
	 */
	public void register( Gui gui );
	
	/*
	 * Returns the list of available albums. The provider must filter any duplicates.
	 */
	public List<Album> getListOfAlbums();
	
	/*
	 * Returns the list of pictures belonging to the given album. The provider must filter any duplicates.
	 */
	public List<Picture> getListOfPictures( Album album );
	
	/*
	 * Returns the bitmap data of a given picture, in a given album.
	 */
	public byte[] getPictureData( Album album, Picture picture);
	
	/*
	 * Creates an album with the given name. 
	 * If null is returned the GUI ignores the result.
	 */
	public Album createAlbum( String name ) ;
	
	/*
	 * Requests the content provider to create a new picture, with a given name and data, to be stored in the given album.
	 * If null is return the GUI ignores the result.
	 */
	public Picture uploadPicture( Album album, String name, byte[] data ) ;
	
	/*
	 * Requests the content provider to delete an album.
	 */
	public void deleteAlbum( Album album );

	/*
	 * Requests the content provider to delete the given picture, in the given album.
	 * If true is returned, the GUI is updated and the picture is removed from the album view.
	 */
	public boolean deletePicture( Album album, Picture picture );
	
	/*
	 * The GUI expects objects representing albums to implement this interface.
	 * 
	 * The implementing class can include any data the content provider might
	 * find useful to associate with an album, such as the list of pictures that
	 * it contains. 
	 */
	interface Album {
		String getName();
	}
	
	/*
	 * The GUI expects objects representing pictures to implement this interface.
	 * 
	 * The implementing class can include any data the content provider might
	 * find useful to associate with a picture, for instance its bitmap data or
	 * where to get it from. 
	 */
	interface Picture {
		String getName();		
	}
}
