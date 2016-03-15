package sd.tp1.gui;

import sd.tp1.gui.GalleryContentProvider.Album;

/*
 * The GUI provides this interface to the main application.
 * The interface features two up calls that the main application can invoke to update
 * the state of the GUI automatically/programmatically
 */
public interface Gui {
	
	/*
	 * Requests the GUI to update the view of available albums.
	 * 
	 * Upon this call, the GUI will switch to the album view.
	 * 
	 * The GUI expects this call to be called only when there are actual changes
	 * to the album list.
	 */
	void updateAlbums();

	/*
	 * Requests the GUI to update the view of the given album.

	 * Upon this call, if the album being displayed matches the given album, the list of pictures will be refreshed.
	 * 
	 * The GUI expects this call to be called only when there are actual changes
	 * to the given album contents.
	 */
	void updateAlbum(Album album);
}
