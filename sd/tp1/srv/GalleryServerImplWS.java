package sd.tp1.srv;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.net.*;

import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.xml.ws.Endpoint;

import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpsConfigurator;
import com.sun.net.httpserver.HttpsServer;

import sd.tp1.common.AlbumFolderClass;
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
	public ArrayList<AlbumFolderClass> ListAlbums() throws GalleryNotFoundException{

		if (basePath.exists()){
			ArrayList<File> names = new ArrayList<File>(Arrays.asList(basePath.listFiles()));
			ObjectInputStream input;
			ArrayList<AlbumFolderClass> albums = new ArrayList<>();
			for(File f : names){
				try {
					input = new ObjectInputStream(new FileInputStream(f));
					albums.add((AlbumFolderClass)input.readObject());
					input.close();
				} catch (IOException e) {
				} catch (ClassNotFoundException e) {
				}
			}
			return albums;
		}
		return null;
	}

	/**
	 * @param album
	 * @return an arrayList with the pictures of the album
	 * @throws AlbumNotFoundException
	 */
	@SuppressWarnings("unchecked")
	@WebMethod
	public List<PictureClass> ListPictures(String album) throws AlbumNotFoundException{
		
		File f = new File(basePath, album+"/album.dat");
		if (f.exists()){
			ObjectInputStream input;
			List<PictureClass> pictures;
			try {
				input = new ObjectInputStream(new FileInputStream(f));
				pictures = (LinkedList<PictureClass>)input.readObject();
				input.close();
				return pictures;
			} catch (IOException e) {
			} catch (ClassNotFoundException e) {
			}
		}
		return null;
		

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
	public byte[] getPicture(String album, String picture) throws AlbumNotFoundException, IOException, PictureNotfoundException{
		File f = new File(basePath, album);
		if (f.exists()){
			f = new File(basePath, album + "/"+ picture);
			if (f.exists()){
				return Files.readAllBytes(Paths.get(basePath+"/"+album + "/"+ picture));
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
		
		File f = new File(basePath, name);
		File file = new File(basePath,name+".dat");
		if (file.exists()){
			ObjectInputStream input;
			AlbumFolderClass albumDat;
			try {
				input = new ObjectInputStream(new FileInputStream(file));
				albumDat = (AlbumFolderClass)input.readObject();
				input.close();
				if(!albumDat.isErased())
					throw new AlbumAlreadyExistsException("Album already exists");
				albumDat.recreate(this.url);
				ObjectOutput out;
				out = new ObjectOutputStream(new FileOutputStream(file));
				out.writeObject(albumDat);
				out.close();
			} catch (IOException e) {
			} catch (ClassNotFoundException e) {
			}
			
		}
		else{
			f.mkdir();
			File albumDat = new File(basePath,name+"/album.dat");
			List<PictureClass> list = new LinkedList<>();
			AlbumFolderClass a = new AlbumFolderClass(name, this.url);
			ObjectOutput out;
			try {
				out = new ObjectOutputStream(new FileOutputStream(file));
				out.writeObject(a);
				out.close();
				out = new ObjectOutputStream(new FileOutputStream(albumDat));
				out.writeObject(list);
				out.close();
			} catch (IOException e) {}
		}
	}

	
	/**
	 * @param name
	 * @return
	 * @throws AlbumNotFoundException
	 * deletes an album
	 */
	@SuppressWarnings("unchecked")
	@WebMethod
	public void deleteAlbum (String name)throws AlbumNotFoundException{
		
		File f = new File(basePath, name+".dat");
		if (f.exists()){
			ObjectInputStream input;
			try {
				input = new ObjectInputStream(new FileInputStream(f));
				AlbumFolderClass albumDat = (AlbumFolderClass)input.readObject();
				input.close();
				albumDat.erase(this.url);
				ObjectOutput out;
				out = new ObjectOutputStream(new FileOutputStream(f));
				out.writeObject(albumDat);
				out.close();
				File dat = new File(basePath + "/" + name + "/album.dat");
				input = new ObjectInputStream(new FileInputStream(dat));
				List<PictureClass> list = (List<PictureClass>)input.readObject();
				input.close();
				for(PictureClass p : list)
					p.erase(this.url);
				out = new ObjectOutputStream(new FileOutputStream(dat));
				out.writeObject(list);
				out.close();
			} catch (IOException e) {
			} catch (ClassNotFoundException e) {
			}
		}
		else
			throw new AlbumNotFoundException("album not found");	
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
	public void uploadPicture (String album, byte [] data, String pictureName)throws AlbumNotFoundException, IOException, PictureAlreadyExistsException{
		
		File dir = new File(basePath + "/" + album);
		if (dir.exists()) {
			dir = new File(basePath, album + "/"+ pictureName);
			File dat = new File(basePath + "/" + album + "/album.dat");
			ObjectInputStream input;
			try {
				input = new ObjectInputStream(new FileInputStream(dat));
				List<PictureClass> list = (LinkedList<PictureClass>)input.readObject();
				input.close();
				PictureClass pic = new PictureClass(pictureName, this.url);
				int index = list.indexOf(pic);
				if(index < 0){
					list.add(new PictureClass(pictureName, this.url));
					ObjectOutput outt;
					outt = new ObjectOutputStream(new FileOutputStream(dat));
					outt.writeObject(list);
					outt.close();
				}
				else{
					if (!pic.isErased())
						throw new PictureAlreadyExistsException("picture already exists");
					pic = list.get(index);
					pic.recreate(this.url);
					ObjectOutput outt;
					outt = new ObjectOutputStream(new FileOutputStream(dat));
					outt.writeObject(list);
					outt.close();
				}
				FileOutputStream out = new FileOutputStream(dir);
				out.write(data);
				out.close();
					
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
		else 
			throw new AlbumNotFoundException("album not found");
	}
	
	/**
	 * @param album
	 * @param name
	 * @return void
	 * to delete a picture of the an album
	 */
	@WebMethod
	public void deletePicture (String album, String picture)throws AlbumNotFoundException,PictureNotfoundException {
		File dir = new File(basePath + "/" + album);
		if (!dir.exists())
			throw new AlbumNotFoundException("album not found");

		File f = new File(basePath, album+"/"+picture);
		if (f.exists()){
			ObjectInputStream input;
			try {
				File dat = new File(basePath + "/" + album + "/album.dat");
				input = new ObjectInputStream(new FileInputStream(dat));
				List<PictureClass> list = (LinkedList<PictureClass>)input.readObject();
				input.close();
				for(PictureClass p: list){
					if(p.getName().equals(picture))
						p.erase(this.url);
				}
				//System.out.println("deleting: " + picture);
				//int l = list.indexOf(new PictureClass(album, this.url));
				//System.out.println("Index: " +l);
				//PictureClass p = list.get(l);
				//p.erase();
				ObjectOutput outt;
				outt = new ObjectOutputStream(new FileOutputStream(dat));
				outt.writeObject(list);
				outt.close();
			} catch (IOException e) {
			} catch (ClassNotFoundException e) {
			}
		}
		else
			throw new PictureNotfoundException("picture not found");
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
		
//		Endpoint.publish("http://0.0.0.0:" +servicePort+"/GalleryServerSOAP", new GalleryServerImplWS(path));
		Endpoint ep = Endpoint.create(impl);
		ep.publish(httpContext);
		System.err.println("GalleryServer started");
		String serviceURL = ""+localhostAddress().getCanonicalHostName()+":"+servicePort;
		url = "https://"+serviceURL+ "/GalleryServerSOAP";
		
//		String serviceURL = ""+localhostAddress().getCanonicalHostName()+":"+servicePort;
//		String url = "http://"+serviceURL+ "/GalleryServerSOAP";
		
		
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
