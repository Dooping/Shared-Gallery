package sd.tp1.srv;

import java.awt.List;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.net.*;

import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.xml.ws.Endpoint;

import sd.tp1.common.MulticastDiscovery;
import sd.tp1.exeptions.AlbumNotFoundException;
import sd.tp1.exeptions.GalleryNotFoundException;
import sd.tp1.gui.GalleryContentProvider;


@WebService
public class GalleryServerImplWS {
	
	private File basePath;
	
	public GalleryServerImplWS() {
		this(".");
	}

	protected GalleryServerImplWS(String pathname) {
		super();
		basePath = new File(pathname);
	}

	@WebMethod
	public int printCenas(){
		System.out.println("teste");
		return 0;
	}
	
	@WebMethod
	public ArrayList<String> ListAlbums() throws GalleryNotFoundException{
		File f = new File(basePath, "");
		ArrayList<String> names;
		if (f.exists())
			return names = new ArrayList<String>(Arrays.asList(f.list()));
		else 
			throw new GalleryNotFoundException("Gallery not found");
	}
	
	@WebMethod
	public ArrayList<String> ListPictures(String album) throws AlbumNotFoundException{
		File f = new File(basePath, album);
		ArrayList<String> names;
		if (f.exists())
			return names = new ArrayList<String>(Arrays.asList(f.list()));
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
