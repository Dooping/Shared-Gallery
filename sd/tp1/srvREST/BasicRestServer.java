package sd.tp1.srvREST;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URI;
import java.net.URL;
import java.net.UnknownHostException;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.util.Enumeration;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import javax.ws.rs.core.UriBuilder;

import org.glassfish.jersey.jdkhttp.JdkHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import sd.tp1.common.Discovery;
import sd.tp1.common.MulticastDiscovery;

import com.sun.net.httpserver.HttpServer;

public class BasicRestServer {
	static final File KEYSTORE = new File("./server.jks");
	static final char[] JKS_PASSWORD = "moncada".toCharArray();
	static final char[] KEY_PASSWORD = "moncada".toCharArray();

	@SuppressWarnings("unused")
	public static void main(String[] args) throws Exception {

		URI baseUri = UriBuilder.fromUri("https://0.0.0.0/").port(9090).path("GalleryServerREST").build();

		ResourceConfig config = new ResourceConfig();

		config.register(AlbumResource.class);
		
		SSLContext sslContext = SSLContext.getInstance("TLSv1");

		KeyStore keyStore = KeyStore.getInstance("JKS");

		try (InputStream is = new FileInputStream(KEYSTORE)) {
			keyStore.load(is, JKS_PASSWORD);
		}

		KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
		kmf.init(keyStore, KEY_PASSWORD);
		
		TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
		tmf.init(keyStore);

		sslContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), new SecureRandom());
		
		HttpServer server = JdkHttpServerFactory.createHttpServer(baseUri, config, sslContext);
		
		System.err.println("GalleryServer started");
		String serviceURL = ""+localhostAddress().getCanonicalHostName()+":"+baseUri.getPort();
		String url = "https://"+serviceURL+ "/GalleryServerREST";
		System.out.println(url);
		Discovery discovery = new MulticastDiscovery();
		discovery.registerService(new URL(url));
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