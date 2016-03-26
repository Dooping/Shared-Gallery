/**
 * 
 */
package sd.tp1.srv;

import java.io.IOException;
import java.util.ArrayList;

import javax.jws.WebMethod;

import sd.tp1.exeptions.AlbumAlreadyExistsException;
import sd.tp1.exeptions.AlbumNotFoundException;
import sd.tp1.exeptions.GalleryNotFoundException;
import sd.tp1.exeptions.PictureAlreadyExistsException;
import sd.tp1.exeptions.PictureNotfoundException;

/**
 * @author Moncada
 *
 */
public interface GalleryServerImplWS {
	
	/**
	 * @return an arrayList with the albuns on source
	 * @throws GalleryNotFoundException
	 */
	@WebMethod
	public ArrayList<String> ListAlbums() throws GalleryNotFoundException;
	
	/**
	 * @param album
	 * @return an arrayList with the pictures of the album
	 * @throws AlbumNotFoundException
	 */
	@WebMethod
	public ArrayList<String> ListPictures(String album) throws AlbumNotFoundException;
	
	/**
	 * @param album
	 * @param picture
	 * @return the picture asked for
	 * @throws AlbumNotFoundException
	 * @throws IOException
	 * @throws PictureNotfoundException
	 */
	@WebMethod
	public PictureClass getPicture(String album, String picture) throws AlbumNotFoundException, IOException, PictureNotfoundException;
	
	/**
	 * @param name
	 * @return
	 * @throws AlbumNotFoundException
	 * creates an album
	 */
	@WebMethod
	public void creatAlbum (String name)throws AlbumAlreadyExistsException;
	
	/**
	 * @param name
	 * @return
	 * @throws AlbumNotFoundException
	 * deletes an album
	 */
	@WebMethod
	public void deleteAlbum (String name)throws AlbumNotFoundException;

	/**
	 * @param picture
	 * @return
	 * to upload a picture to the album
	 * @throws IOException, AlbumNotFoundException, PictureAlreadyExistsException
	 */
	@WebMethod
	public void uploadPicture (String album, byte [] data, String name)throws AlbumNotFoundException, IOException, PictureAlreadyExistsException;

	/**
	 * @param album
	 * @param name
	 * @return void
	 * to delete a picture of the an album
	 */
	@WebMethod
	public void deletePicture (String album, String name)throws AlbumNotFoundException,PictureNotfoundException;
	
	
}
