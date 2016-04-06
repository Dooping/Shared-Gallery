package sd.tp1;

import java.net.MalformedURLException;
import java.net.URI;
import java.util.List;

import sd.tp1.clt.ws.AlbumAlreadyExistsException_Exception;
import sd.tp1.clt.ws.AlbumNotFoundException_Exception;
import sd.tp1.clt.ws.GalleryNotFoundException_Exception;
import sd.tp1.clt.ws.GalleryServerImplWSClass;
import sd.tp1.clt.ws.GalleryServerImplWSClassService;
import sd.tp1.clt.ws.IOException_Exception;
import sd.tp1.clt.ws.PictureAlreadyExistsException_Exception;
import sd.tp1.clt.ws.PictureClass;
import sd.tp1.clt.ws.PictureNotfoundException_Exception;


public class SOAPClientClass implements RequestInterface{
	private GalleryServerImplWSClass server;
	
	public SOAPClientClass(URI serverURI) throws MalformedURLException{
		GalleryServerImplWSClassService service = new GalleryServerImplWSClassService(serverURI.toURL());
		this.server = service.getGalleryServerImplWSClassPort();
	}

	@Override
	public List<String> getAlbums() {
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
					return null;
				}
			}
		}
		return albums;
	}

	@Override
	public List<String> getPictures(String album) {
		List<String> pictures = null;
		boolean executed = false;
		for (int i =0; !executed && i<3; i++){
			try {
				pictures = server.listPictures(album);
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
		/*List<Picture> lst = new ArrayList<Picture>();
		for(String p: pictures)
			lst.add( new SharedPicture(p));*/
		return pictures;
	}

	@Override
	public byte[] getPicture(String album, String picture) {
		byte [] pic = null;
		boolean executed = false;
		for (int i =0; !executed && i<3; i++){
			try {
				PictureClass p = server.getPicture(album, picture);
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

	@Override
	public boolean createAlbum(String album) {
		boolean executed = false;
		for (int i =0; !executed && i<3; i++){
			try{
				server.creatAlbum(album);
				executed = true;
			} catch (AlbumAlreadyExistsException_Exception e1){
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

	@Override
	public boolean deleteAlbum(String album) {
		boolean executed = false;
		for (int i =0; !executed && i<3; i++){
			try{
				server.deleteAlbum(album);
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
					return false;
				}
			}
		}
		return true;
	}

	@Override
	public boolean deletePicture(String album, String picture) {
		boolean executed = false;
		for (int i =0; !executed && i<3; i++){
			try{
				server.deletePicture(album, picture);
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

	@Override
	public boolean uploadPicture(String album, String picture, byte[] data) {
		boolean executed = false;
		for (int i =0; !executed && i<3; i++){
			try{
				PictureClass pic = new PictureClass();
				pic.setContents(data);
				pic.setName(picture);
				server.uploadPicture(album,data, picture);
				executed = true;
			} catch (AlbumNotFoundException_Exception e1){
				System.err.println("Erro: " + e1.getMessage());
				return false;
			} catch (IOException_Exception e1){
				System.err.println("Erro: " + e1.getMessage());
				return false;
			} catch (PictureAlreadyExistsException_Exception e1){
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

}
