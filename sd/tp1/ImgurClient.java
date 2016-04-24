package sd.tp1;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.github.scribejava.apis.ImgurApi;
import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Response;
import com.github.scribejava.core.model.Verb;
import com.github.scribejava.core.oauth.OAuth20Service;

public class ImgurClient implements RequestInterface{
	private OAuth2AccessToken accessToken;
	private OAuth20Service service;
	
	public ImgurClient() {
		try {
			// Substituir pela API key atribuida
			final String apiKey = "87d56e838ce5413"; 
			// Substituir pelo API secret atribuido
			final String apiSecret = "b5ed4dadbc629cfd1058c678d10a795f9dbcb5a9"; 
			service = new ServiceBuilder().apiKey(apiKey).apiSecret(apiSecret)
					.build(ImgurApi.instance());
			final Scanner in = new Scanner(System.in);

			// Obtain the Authorization URL
			System.out.println("A obter o Authorization URL...");
			final String authorizationUrl = service.getAuthorizationUrl();
			System.out.println("Necessario dar permissao neste URL:");
			System.out.println(authorizationUrl);
			System.out.println("e copiar o codigo obtido para aqui:");
			System.out.print(">>");
			final String code = in.nextLine();

			// Trade the Request Token and Verifier for the Access Token
			System.out.println("A obter o Access Token!");
			accessToken = service.getAccessToken(code);

			
			in.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public List<String> getAlbums() {
		List<String> al = new LinkedList<String>();
		try{
			OAuthRequest albumsReq = new OAuthRequest(Verb.GET,
					"https://api.imgur.com/3/account/GonaloMoncada/albums/ids", service);
			service.signRequest(accessToken, albumsReq);
			final Response albumsRes = albumsReq.send();
			System.out.println(albumsRes.getCode());

			JSONParser parser = new JSONParser();
			JSONObject res = (JSONObject) parser.parse(albumsRes.getBody());

			JSONArray albums = (JSONArray) res.get("data");
			Iterator albumsIt = albums.iterator();
			while (albumsIt.hasNext()) {
				//System.out.println( "id : " + albumsIt.next()); 
				al.add(albumsIt.next().toString());
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		return null;
	}

	@Override
	public List<String> getPictures(String album) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public byte[] getPicture(String album, String picture) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean createAlbum(String album) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean deleteAlbum(String album) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean deletePicture(String album, String picture) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean uploadPicture(String album, String picture, byte[] data) {
		// TODO Auto-generated method stub
		return false;
	}

}
