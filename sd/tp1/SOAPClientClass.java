package sd.tp1;

import java.net.MalformedURLException;
import java.net.URI;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.List;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import sd.tp1.clt.ws.AlbumAlreadyExistsException_Exception;
import sd.tp1.clt.ws.AlbumNotFoundException_Exception;
import sd.tp1.clt.ws.GalleryNotFoundException_Exception;
import sd.tp1.clt.ws.GalleryServerImplWS;
import sd.tp1.clt.ws.GalleryServerImplWSService;
import sd.tp1.clt.ws.IOException_Exception;
import sd.tp1.clt.ws.PictureAlreadyExistsException_Exception;
import sd.tp1.clt.ws.PictureClass;
import sd.tp1.clt.ws.PictureNotfoundException_Exception;
import sd.tp1.common.AlbumFolderClass;


public class SOAPClientClass implements RequestInterface{
	private GalleryServerImplWS server;
	
	public SOAPClientClass(URI serverURI) throws MalformedURLException{
		//GalleryServerImplWSService service = new GalleryServerImplWSService(serverURI.toURL());
		//this.server = service.getGalleryServerImplWSPort();
		
		try{
			SSLContext sc = SSLContext.getInstance("TLSv1");			
			TrustManager[] trustAllCerts = { new InsecureTrustManager() };
	        sc.init(null, trustAllCerts, new java.security.SecureRandom());
			HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
			HttpsURLConnection.setDefaultHostnameVerifier( new InsecureHostnameVerifier());
	
			
			GalleryServerImplWSService service = new GalleryServerImplWSService(serverURI.toURL());
			
	        
			this.server = service.getGalleryServerImplWSPort();
		} catch (Exception e) {
			System.err.println("Erro: " + e.getMessage());
			e.printStackTrace();
		}
	}

	@Override
	public List<AlbumFolderClass> getAlbums() {
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
		//return albums;
		return null;
	}

	@Override
	public List<sd.tp1.common.PictureClass> getPictures(String album) {
		List<String> pictures = null;
		boolean executed = false;
		//System.out.println("getting: " + album);
		for (int i =0; !executed && i<3; i++){
			try {
				pictures = server.listPictures(album);
				//for(String s: pictures)
					//System.out.println(s);
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
		//return pictures;
		return null;
	}

	@Override
	public byte[] getPicture(String album, String picture) {
		byte [] pic = null;
		boolean executed = false;
		//System.out.println("Getting picture: " + picture + " of album: " + album);
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
	public boolean uploadPicture(String album, String picture, byte[] data, boolean isNew) {
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
	
	static public class InsecureHostnameVerifier implements HostnameVerifier {
		@Override
		public boolean verify(String hostname, SSLSession session) {
			//System.err.println(hostname);
			return true;
		}
	}

	static public class InsecureTrustManager implements X509TrustManager {
	    @Override
	    public void checkClientTrusted(final X509Certificate[] chain, final String authType) throws CertificateException {
	    }

	    @Override
	    public void checkServerTrusted(final X509Certificate[] chain, final String authType) throws CertificateException {
	    	Arrays.asList( chain ).forEach( i -> {
	    		//System.err.println( "type: " + i.getType() + "from: " + i.getNotBefore() + " to: " + i.getNotAfter() );
	    	});
	    }

	    @Override
	    public X509Certificate[] getAcceptedIssuers() {
	        return new X509Certificate[0];
	    }
	}

}
