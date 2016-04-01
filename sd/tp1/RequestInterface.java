package sd.tp1;

import java.util.List;

public interface RequestInterface {

	List<String> getAlbums();
	
	List<String> getPictures(String album);
	
	byte[] getPicture(String album, String picture);
	
	boolean createAlbum(String album);
	
	boolean deleteAlbum(String album);
	
	boolean deletePicture(String album, String picture);
	
	boolean uploadPicture(String album, String picture, byte[] data);

}
