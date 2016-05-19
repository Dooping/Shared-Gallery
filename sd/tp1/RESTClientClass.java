package sd.tp1;

import java.net.URI;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.List;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
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
		
        try {
        	SSLContext sc = SSLContext.getInstance("TLSv1");
    		
    		TrustManager[] trustAllCerts = { new InsecureTrustManager() };
			sc.init(null, trustAllCerts, new java.security.SecureRandom());

			ClientConfig config = new ClientConfig();
		    Client client = ClientBuilder.newBuilder()
					.hostnameVerifier(new InsecureHostnameVerifier())
					.sslContext(sc)
					.withConfig(config)
					.build();

		    this.target = client.target(serverURI);
		} catch (KeyManagementException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
	
	static private class InsecureHostnameVerifier implements HostnameVerifier {
		@Override
		public boolean verify(String hostname, SSLSession session) {
			return true;
		}
	}

	static private class InsecureTrustManager implements X509TrustManager {
	    @Override
	    public void checkClientTrusted(final X509Certificate[] chain, final String authType) throws CertificateException {
	    }

	    @Override
	    public void checkServerTrusted(final X509Certificate[] chain, final String authType) throws CertificateException {
	    	Arrays.asList( chain ).forEach( i -> {
	    		System.err.println( "type: " + i.getType() + "from: " + i.getNotBefore() + " to: " + i.getNotAfter() );
	    	});
	    }

	    @Override
	    public X509Certificate[] getAcceptedIssuers() {
	        return new X509Certificate[0];
	    }
	}

}
