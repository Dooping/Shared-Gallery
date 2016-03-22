package sd.tp1.srv;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.net.*;

import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.xml.ws.Endpoint;

import sd.tp1.common.MulticastDiscovery;
import sd.tp1.exeptions.*;


@WebService
public class GalleryServerImplWS {
	
	private File basePath;
	
	public GalleryServerImplWS() {
		this(".");
	}

	/**
	 * @param pathname
	 */
	protected GalleryServerImplWS(String pathname) {
		super();
		basePath = new File(pathname);
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
			if (f.exists())
				return new PictureClass(picture, Files.readAllBytes(Paths.get(basePath+"/"+album + "/"+ picture)));
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
	//public void uploadPicture (String album, PictureClass picture)throws AlbumNotFoundException, IOException, PictureAlreadyExistsException{
	public void uploadPicture (String album, byte [] data, String name)throws AlbumNotFoundException, IOException, PictureAlreadyExistsException{
		//System.out.println("cheguei aqui. Criar picture: " + picture.name);
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
	 * @return
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
		final int servicePort = 8080;
		Endpoint.publish("http://0.0.0.0:"+servicePort+"/GalleryServer", new GalleryServerImplWS(path));
		System.err.println("GalleryServer started");

		final String add = "230.0.1.0";
		final int port = 9000;
		final InetAddress adress = InetAddress.getByName(add);
		@SuppressWarnings("resource")
		MulticastSocket socket = new MulticastSocket(port);
		socket.joinGroup(adress);
		@SuppressWarnings("unused")
		MulticastDiscovery discovery = new MulticastDiscovery();


		while(true){
			byte [] buffer = new byte [65536];
			DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
			socket.receive(packet);
			//TODO comparar o serviço pedido
			byte [] send = new byte[128];
			String s = ""+servicePort;
			send = s.getBytes();
			DatagramPacket toSend = new DatagramPacket(send, s.length());
			toSend.setAddress(packet.getAddress());
			toSend.setPort(packet.getPort());
			socket.send(toSend);
		}

	}
	
}
