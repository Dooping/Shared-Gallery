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
	

	public static void main(String[] args) throws Exception {
		String path = args.length > 0 ? args[0] : ".";
		Endpoint.publish("http://0.0.0.0:8080/GalleryServer", new GalleryServerImplWS());
		System.err.println("GalleryServer started");

		final String add = "230.0.1.0";
		final int port = 9000;
		final InetAddress adress = InetAddress.getByName(add);
		@SuppressWarnings("resource")
		MulticastSocket socket = new MulticastSocket(port);
		socket.joinGroup(adress);


		while(true){
			byte [] buffer = new byte [65536];
			DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
			socket.receive(packet);
			byte [] send = new byte[128];
			String s = "webService of david and goncalo";
			send = s.getBytes();
			DatagramPacket toSend = new DatagramPacket(send, send.length);
			toSend.setAddress(packet.getAddress());
			toSend.setPort(packet.getPort());
			socket.send(toSend);

		}

	}
	
}
