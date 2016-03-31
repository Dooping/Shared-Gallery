package sd.tp1.srvREST;

import java.net.URI;

import javax.ws.rs.core.UriBuilder;

import org.glassfish.jersey.jdkhttp.JdkHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import com.sun.net.httpserver.HttpServer;

public class BasicRestServer {

	public static void main(String[] args) throws Exception {

		URI baseUri = UriBuilder.fromUri("http://0.0.0.0/").port(8080).path("GalleryServerREST").build();

		ResourceConfig config = new ResourceConfig();

		config.register(AlbumResource.class);
		
		HttpServer server = JdkHttpServerFactory.createHttpServer(baseUri, config);

		System.err.println("REST Server ready... ");
	}
}