package sd.tp1;

import java.net.URI;
import java.util.Arrays;
import java.util.List;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.client.ClientConfig;

public class RESTClientClass implements RequestInterface {
	
	WebTarget target;
	
	public RESTClientClass(URI serverURI){
		super();
		ClientConfig config = new ClientConfig();
	    Client client = ClientBuilder.newClient(config);

	    this.target = client.target(serverURI);
	}

	@Override
	public List<String> getAlbums() {
		String[] albums = target.path("/albums")
	    		.request()
	    		.accept(MediaType.APPLICATION_JSON)
	    		.get(String[].class);
		
		return Arrays.asList(albums);
	}

	@Override
	public List<String> getPictures(String album) {
		String[] pictures = target.path("/albums/"+album)
	    		.request()
	    		.accept(MediaType.APPLICATION_JSON)
	    		.get(String[].class);
		
		return Arrays.asList(pictures);
	}

	@Override
	public byte[] getPicture(String album, String picture) {
		return target.path("/albums/"+album+"/"+picture)
	    		.request()
	    		.accept(MediaType.APPLICATION_OCTET_STREAM)
	    		.get(byte[].class);
	}

	@Override
	public boolean createAlbum(String album) {
		Response response = target.path("/albums")
					.request()
					.post(Entity.entity(album, MediaType.APPLICATION_JSON));
		return response.getStatus()==200;
	}

	@Override
	public boolean deleteAlbum(String album) {
		Response response = target.path("/albums/"+album)
				.request()
				.delete();
		return response.getStatus()==200;
	}

	@Override
	public boolean deletePicture(String album, String picture) {
		Response response = target.path("/albums/"+album+"/"+picture)
				.request()
				.delete();
		return response.getStatus()==200;
	}

	@Override
	public boolean uploadPicture(String album, String picture, byte[] data) {
		Response response = target.path("/albums/"+album+"/"+picture)
				.request()
				.post(Entity.entity(data, MediaType.APPLICATION_OCTET_STREAM));
		return response.getStatus()==200;
	}

}
