package sd.tp1.common;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Enumeration;

public class MulticastDiscovery implements Discovery {
	private static final String MULTICAST_IP = "224.0.0.0";
	private static int port = 9005;
	
	
	

	@Override
	public void findService(MulticastSocket socket) {
		try{
			final InetAddress address = InetAddress.getByName(MULTICAST_IP);
			if( ! address.isMulticastAddress()) { 
				 System.out.println( "Use range : 224.0.0.0 -- 239.255.255.255"); 
			} 
			byte [] send = new byte [128];
			DatagramPacket request = new DatagramPacket(send, send.length);
			request.setAddress(address);
			request.setPort(port);
			socket.send(request);

		}
		catch (Exception e){
			e.printStackTrace();
		}
		
	}
	
	public URI getService(MulticastSocket socket){
		byte [] buffer = new byte [65536];
		DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
		try {
			socket.receive(packet);
			String serviceURL = new String(packet.getData(),0,packet.getLength());
			return new URI(serviceURL);
		} catch (IOException | URISyntaxException e) {
			return null;
			//e.printStackTrace();
		}
	}

	@Override
	public void registerService(URL url) {
		InetAddress address;
		MulticastSocket socket;
		try {
			address = InetAddress.getByName(MULTICAST_IP);
			if( ! address.isMulticastAddress()) { 
				 System.out.println( "Use range : 224.0.0.0 -- 239.255.255.255"); 
			} 
			socket = new MulticastSocket(port);
			socket.joinGroup(address);

			while(true){
				byte [] buffer = new byte [65536];
				DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
				socket.receive(packet);
				//TODO comparar o serviço pedido
				byte [] send = new byte[128];
				String s = url.toString();
				send = s.getBytes();
				DatagramPacket toSend = new DatagramPacket(send, s.length());
				toSend.setAddress(packet.getAddress());
				toSend.setPort(packet.getPort());
				socket.send(toSend);
			}

		} catch (UnknownHostException e) {
			//e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	
	
	
	


}
