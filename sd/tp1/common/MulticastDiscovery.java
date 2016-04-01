package sd.tp1.common;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.URL;

public class MulticastDiscovery implements Discovery {
	private static final String MULTICAST_IP = "224.0.0.0";
	

	@Override
	public URL findService(String name) {
		//TODO: para quando houver dois svç, temos de tentar e se falhar
		// tentar no outro!
		try{
			final InetAddress adress = InetAddress.getByName(MULTICAST_IP);
			int port = 9005;
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
			return new URL("http://"+serviceURL+ "/"+name);
		}
		catch (Exception e){
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public void registerService(URL url, int servicePort) {
		//TODO perguntar ao prof
	}

}
