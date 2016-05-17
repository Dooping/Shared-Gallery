package sd.tp1.srv;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.net.*;

import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import javax.xml.ws.Endpoint;

import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpsConfigurator;
import com.sun.net.httpserver.HttpsServer;

import sd.tp1.common.Discovery;
import sd.tp1.common.MulticastDiscovery;
import sd.tp1.common.PictureClass;
import sd.tp1.common.ServerManager;
import sd.tp1.exeptions.*;


@WebService
public class GalleryServerImplWS{
	static final File KEYSTORE = new File("./server.jks");
	static final char[] JKS_PASSWORD = "moncada".toCharArray();
	static final char[] KEY_PASSWORD = "moncada".toCharArray();
	
	private File basePath;
	private static String url;
	
	public GalleryServerImplWS() {
		this(".");
	}

	/**
	 * @param pathname
	 */
	protected GalleryServerImplWS(String pathname) {
		super();
		basePath = new File(pathname);
		if (!basePath.exists())
			basePath.mkdir();
	}
	
	/**
	 * @return an arrayList with the albuns on source
	 * @throws GalleryNotFoundException
	 */
	@WebMethod
	public ArrayList<String> ListAlbums() throws GalleryNotFoundException{
		File f = new File(basePath, "");
		@SuppressWarnings("unused")
		ArrayList<String> names;
		if (f.exists())
			return names = new ArrayList<String>(Arrays.asList(f.list()));
		else 
			throw new GalleryNotFoundException("Gallery not found");
	}

	/**
	 * @param album
	 * @return an arrayList with the pictures of the album
	 * @throws AlbumNotFoundException
	 */
	@WebMethod
	public ArrayList<String> ListPictures(String album) throws AlbumNotFoundException{
		File f = new File(basePath, album);
		@SuppressWarnings("unused")
		ArrayList<String> names;
		if (f.exists())
			return names = new ArrayList<String>(Arrays.asList(f.list()));
		else
			throw new AlbumNotFoundException("Album not found");
	}
	
	/**
	 * @param album
	 * @param picture
	 * @return the picture asked for
	 * @throws AlbumNotFoundException
	 * @throws IOException
	 * @throws PictureNotfoundException
	 */
	@WebMethod
	public PictureClass getPicture(String album, String picture) throws AlbumNotFoundException, IOException, PictureNotfoundException{
		File f = new File(basePath, album);
		if (f.exists()){
			f = new File(basePath, album + "/"+ picture);
			if (f.exists()){
				Path path = f.toPath();
				BasicFileAttributes attrs = Files.readAttributes(path, BasicFileAttributes.class);
				return new PictureClass(picture, Files.readAllBytes(Paths.get(basePath+"/"+album + "/"+ picture)), attrs.lastModifiedTime().toMillis(),url);
			}
			else
				throw new PictureNotfoundException("Picture not found");
		}
		else
			throw new AlbumNotFoundException("Album not found");
	}
	
	/**
	 * @param name
	 * @return
	 * @throws AlbumNotFoundException
	 * creates an album
	 */
	@WebMethod
	public void creatAlbum (String name)throws AlbumAlreadyExistsException{
		File dir = new File(basePath + "/" + name);
		if (!dir.exists())
			dir.mkdir();
		else 
			throw new AlbumAlreadyExistsException("Album already exists");

	}
	
	/**
	 * @param name
	 * @return
	 * @throws AlbumNotFoundException
	 * deletes an album
	 */
	@WebMethod
	public void deleteAlbum (String name)throws AlbumNotFoundException{
		File dir = new File(basePath + "/" + name);
		if (dir.exists()) {
			deleteDir(dir);
		}
		else 
			throw new AlbumNotFoundException("Album not found");
		
	}
	
	/**
	 * @param file
	 * to delete a directory and all of it's content's
	 */
	private void deleteDir(File file) {
	    File[] contents = file.listFiles();
	    if (contents != null) {
	        for (File f : contents) {
	            deleteDir(f);
	        }
	    }
	    file.delete();
	}
	
	/**
	 * @param picture
	 * @return
	 * to upload a picture to the album
	 * @throws IOException, AlbumNotFoundException, PictureAlreadyExistsException
	 */
	@WebMethod
	public void uploadPicture (String album, byte [] data, String name)throws AlbumNotFoundException, IOException, PictureAlreadyExistsException{
		File dir = new File(basePath + "/" + album);
		if (dir.exists()) {
			dir = new File(basePath, album + "/"+ name);
			
			if (!dir.exists()){
				FileOutputStream out = new FileOutputStream(dir);
				out.write(data);
				out.close();
			}
			else throw new PictureAlreadyExistsException("Picture already exists");

		}
		else 
			throw new AlbumNotFoundException("Album not found");

	}
	
	/**
	 * @param album
	 * @param name
	 * @return void
	 * to delete a picture of the an album
	 */
	@WebMethod
	public void deletePicture (String album, String name)throws AlbumNotFoundException,PictureNotfoundException {
		File dir = new File(basePath, album);
		if(dir.exists()){
			dir = new File(basePath, album + "/"+ name);
			if (dir.exists())
				dir.delete();
			else 
				throw new PictureNotfoundException("Picture not found");
		}
		else
			throw new AlbumNotFoundException("Album not found");
	}

	public static void main(String[] args) throws Exception {
		String path = args.length > 0 ? args[0] : "./gallery";
		final int servicePort = 9090;
		
		KeyManagerFactory keyFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
		KeyStore store = KeyStore.getInstance("JKS");
		try( FileInputStream fis = new FileInputStream( KEYSTORE )){
			store.load(fis, JKS_PASSWORD);			
			keyFactory.init(store, KEY_PASSWORD);
		}

		// Prepare the server's trust manager 
		TrustManagerFactory trustFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
		trustFactory.init(store);

		// Create and initialize the ssl context.
		SSLContext ssl = SSLContext.getInstance("TLS");
		ssl.init(keyFactory.getKeyManagers(), trustFactory.getTrustManagers(), new SecureRandom());

		// Create the HTTPS server using the ssl context.
		HttpsConfigurator configurator = new HttpsConfigurator(ssl);
		HttpsServer httpsServer = HttpsServer.create(new InetSocketAddress("0.0.0.0", servicePort), -1);
		httpsServer.setHttpsConfigurator(configurator);
		HttpContext httpContext = httpsServer.createContext("/GalleryServerSOAP");
		httpsServer.start();

		// Instantiate the soap webservice and publish it on the the https server.
		GalleryServerImplWS impl = new GalleryServerImplWS(path);
		Endpoint ep = Endpoint.create( impl);
		ep.publish(httpContext);
		System.err.println("GalleryServer started");
		String serviceURL = ""+localhostAddress().getCanonicalHostName()+":"+servicePort;
		url = "https://"+serviceURL+ "/GalleryServerSOAP";
		System.out.println(url);
		Discovery discovery = new MulticastDiscovery();
		discovery.registerService(new URL(url));
		ServerManager manager = new ServerManager();
		
		
		/*Endpoint.publish("http://0.0.0.0:" +servicePort+"/GalleryServerSOAP", new GalleryServerImplWS(path));
		System.err.println("GalleryServer started");
		String serviceURL = ""+localhostAddress().getCanonicalHostName()+":"+servicePort;
		String url = "http://"+serviceURL+ "/GalleryServerSOAP";
		System.out.println(url);
		Discovery discovery = new MulticastDiscovery();
		discovery.registerService(new URL(url));*/
	}
	
	/**
	 * Return the IPv4 address of the local machine that is not a loopback address if available.
	 * Otherwise, returns loopback address.
	 * If no address is available returns null.
	 */
	private static InetAddress localhostAddress() {
		try {
			try {
				Enumeration<NetworkInterface> e = NetworkInterface.getNetworkInterfaces();
				while (e.hasMoreElements()) {
					NetworkInterface n = e.nextElement();
					Enumeration<InetAddress> ee = n.getInetAddresses();
					while (ee.hasMoreElements()) {
						InetAddress i = ee.nextElement();
						if (i instanceof Inet4Address && !i.isLoopbackAddress())
							return i;
					}
				}
			} catch (SocketException e) {
				// do nothing
			}
			return InetAddress.getLocalHost();
		} catch (UnknownHostException e) {
			return null;
		}
	}
	
}
