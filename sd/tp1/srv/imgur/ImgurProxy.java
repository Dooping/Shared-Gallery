package sd.tp1.srv.imgur;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import javax.imageio.ImageIO;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import sd.tp1.common.AlbumFolderClass;
import sun.misc.BASE64Encoder;

import com.github.scribejava.apis.ImgurApi;
import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Verb;
import com.github.scribejava.core.oauth.OAuth20Service;

@SuppressWarnings("restriction")
@Path("/albums")
public class ImgurProxy {
	File basePath;
	private ImgurClient imgur;
	
	public ImgurProxy(){
		super();		
		basePath = new File("./gallery");
		if (!basePath.exists())
			basePath.mkdir();
		
		OAuth2AccessToken accessToken = null;
		OAuth20Service service = null;
		
		try {
			final String apiKey = "87d56e838ce5413"; 
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
		imgur = new ImgurClient(accessToken, service);
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAlbums() {
		return Response.ok(imgur.getAlbums()).build();
	}

	@GET
	@Path("/{album}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getPictures(@PathParam("album") String a) {
		return Response.ok(imgur.getPictures(a)).build();
	}
	
	@GET
	@Path("/{album}/{picture}")
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	public Response getPicture(@PathParam("album") String album, @PathParam("picture") String picture) throws IOException {
		return Response.ok(imgur.getPicture(album, picture)).build();
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public Response createAlbum(String album) {
		return imgur.createAlbum(album) ? Response.ok().build() : Response.status(422).build();
	}
	
	@DELETE
	@Path("/{album}")
	public Response deleteAlbum(@PathParam("album") String album) {
		return imgur.deleteAlbum(album) ? Response.ok().build() : Response.status(Status.NOT_FOUND).build();	
	}
	

	@DELETE
	@Path("/{album}/{picture}")
	public Response deletePicture(@PathParam("album") String album, @PathParam("picture") String picture) {
		return imgur.deletePicture(album, picture) ? Response.ok().build() : Response.status(422).build();
	}
	
	@POST
	@Path("/{album}/{pictureName}")
	@Consumes(MediaType.APPLICATION_OCTET_STREAM)
	public Response uploadPicture(@PathParam("album") String album, @PathParam("pictureName") String picture, byte[] data) throws IOException {
		return imgur.uploadPicture(album, picture, data) ? Response.ok().build() : Response.status(Status.NOT_FOUND).build();
	}




}
