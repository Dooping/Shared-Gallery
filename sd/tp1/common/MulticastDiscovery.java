package sd.tp1.common;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Enumeration;

public class MulticastDiscovery implements Discovery {
	private static final String MULTICAST_IP = "224.0.0.0";
	private static int port = 9005;
	

	@Override
	public URL findService(String name) {
		//TODO: para quando houver dois svç, temos de tentar e se falhar
		// tentar no outro!
		try{
			final InetAddress adress = InetAddress.getByName(MULTICAST_IP);
			//int port = 9005;
			@SuppressWarnings("resource")
			MulticastSocket socket = new MulticastSocket();

			byte [] send = new byte [128];
			send = name.getBytes();
			DatagramPacket request = new DatagramPacket(send, send.length);
			request.setAddress(adress);
			request.setPort(port);
			socket.send(request);
			byte [] buffer = new byte [65536];
			DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
			socket.receive(packet);
			String serviceURL = new String(packet.getData(),0,packet.getLength());
			return new URL(serviceURL);
		}
		catch (Exception e){
			e.printStackTrace();
		}
		return null;
	}
	

	@Override
	public void registerService(URL url) {
		InetAddress adress;
		MulticastSocket socket;
		try {
			adress = InetAddress.getByName(MULTICAST_IP);
			socket = new MulticastSocket(port);
			socket.joinGroup(adress);

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
