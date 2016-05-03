package sd.tp1.srv.imgur;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
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

import com.github.scribejava.apis.ImgurApi;
import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Verb;
import com.github.scribejava.core.oauth.OAuth20Service;

import sun.misc.BASE64Encoder;

@Path("/albums")
public class ImgurProxy {
	File basePath;
	
	//TODO: nao e possivel passar os nomes das imgem no upload!
	private OAuth2AccessToken accessToken;
	private OAuth20Service service;
	//estrutura para associar o nome de uma image com um id
	Map<String, String> nameToId;
	//Estrutura para associar um nome de um album com o seu id
	Map<String, String> albumToId;
	//estrutura para associar um id de uma imagem com o seu nome
	Map<String, String> idToPicName;
	int newName;
		
	
	public ImgurProxy(){
		super();
		basePath = new File("./gallery");
		
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
		newName = 0;
		nameToId = new HashMap<String, String>();
		albumToId = new HashMap<String, String>();
		idToPicName = new HashMap<String, String>();

	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAlbums() {
		//System.err.printf("getAlbums()\n");
		List<String> al = new LinkedList<String>();
		try{
			OAuthRequest albumsReq = new OAuthRequest(Verb.GET,
					"https://api.imgur.com/3/account/GonaloMoncada/albums/ids", service);
			service.signRequest(accessToken, albumsReq);
			final com.github.scribejava.core.model.Response albumsRes = albumsReq.send();
			if(albumsRes.getCode() != 200)
				return null;
			JSONParser parser = new JSONParser();
			JSONObject res = (JSONObject) parser.parse(albumsRes.getBody());
			JSONArray albums = (JSONArray) res.get("data");
			Iterator albumsIt = albums.iterator();
			while (albumsIt.hasNext()) {
				String s = albumsIt.next().toString();
				al.add(s);
			} 
		} catch (ParseException e) {
			e.printStackTrace();
		}
		List<String> t = this.getAllAlbumInfo(al);
		return Response.ok(t).build();

	}
	
	
	/**
	 * @param l: a list with the albuns id's
	 * @return the list with the names of albuns
	 */
	private List<String> getAllAlbumInfo(List<String> l){
		List<String> al = new LinkedList<String>();
		try{
			Iterator <String> it = l.iterator();
			while(it.hasNext()){
				String s = it.next();
				OAuthRequest albumsReq = new OAuthRequest(Verb.GET,
						"https://api.imgur.com/3/account/GonaloMoncada/album/" + s, service);
				service.signRequest(accessToken, albumsReq);
				final com.github.scribejava.core.model.Response albumsRes = albumsReq.send();
				if(albumsRes.getCode() == 200){
					JSONParser parser = new JSONParser();
					JSONObject res = (JSONObject) parser.parse(albumsRes.getBody());
					JSONObject p = (JSONObject) res.get("data");
					String piI = (String) p.get("id");
					String title = (String) p.get("title");
					albumToId.put(title, piI);
					al.add(title);
				}
			}

		} catch (ParseException e) {
			e.printStackTrace();
		}
		return al;
	}

	@GET
	@Path("/{album}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getPictures(@PathParam("album") String a) {
		return Response.ok(getPicturesLocal(a)).build();
	}
	
	@GET
	@Path("/{album}/{picture}")
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	public Response getPicture(@PathParam("album") String album, @PathParam("picture") String picture) throws IOException {
		try{
			String picName = nameToId.get(picture);
			OAuthRequest albumsReq = new OAuthRequest(Verb.GET,
					"https://api.imgur.com/3/account/GonaloMoncada/image/" + picName, service);
			service.signRequest(accessToken, albumsReq);
			final com.github.scribejava.core.model.Response albumsRes = albumsReq.send();
			if(albumsRes.getCode() != 200)
				return Response.status(Status.NOT_FOUND).build();
			JSONParser parser = new JSONParser();
			JSONObject res = (JSONObject) parser.parse(albumsRes.getBody());
			JSONObject p = (JSONObject) res.get("data");
			String link = (String) p.get("link");
			System.out.println(link);
			URL imageURL = new URL(link);
			BufferedImage originalImage = ImageIO.read(imageURL);
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ImageIO.write(originalImage, "jpg", baos );
			byte[] imageInByte=baos.toByteArray();
			return Response.ok(imageInByte).build();
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return Response.status(Status.NOT_FOUND).build();
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public Response createAlbum(String album) {
		OAuthRequest albumsReq = new OAuthRequest(Verb.POST,
				"https://api.imgur.com/3/album", service);
		albumsReq.addBodyParameter("title", album);
		service.signRequest(accessToken, albumsReq);
		final com.github.scribejava.core.model.Response albumsRes = albumsReq.send();
		if(albumsRes.getCode()==200){
			try {
				JSONParser parser = new JSONParser();
				JSONObject res;
				res = (JSONObject) parser.parse(albumsRes.getBody());
				JSONObject p = (JSONObject) res.get("data");
				String id = (String) p.get("id");
				albumToId.put(album, id);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			return Response.ok().build();
		}	
		return Response.status(422).build();	
	}
	
	@DELETE
	@Path("/{album}")
	public Response deleteAlbum(@PathParam("album") String album) {
		//apagar as fotos primeiro
		this.deleteAlbumPhotos(album);
		String albumName = albumToId.get(album);
		OAuthRequest albumsReq = new OAuthRequest(Verb.DELETE,
				"https://api.imgur.com/3/account/GonaloMoncada/album/"+albumName, service);
		service.signRequest(accessToken, albumsReq);
		final com.github.scribejava.core.model.Response albumsRes = albumsReq.send();
		if(albumsRes.getCode()==200){
			albumToId.remove(album);
			return Response.ok().build();
		}
		return Response.status(Status.NOT_FOUND).build();	
	}
	

	@DELETE
	@Path("/{album}/{picture}")
	public Response deletePicture(@PathParam("album") String album, @PathParam("picture") String picture) {
		if(this.deletePic(album, picture))
			return Response.ok().build();
		return Response.status(Status.NOT_FOUND).build();	
	}
	
	@POST
	@Path("/{album}/{pictureName}")
	@Consumes(MediaType.APPLICATION_OCTET_STREAM)
	public Response uploadPicture(@PathParam("album") String album, @PathParam("pictureName") String picture, byte[] data) throws IOException {
		OAuthRequest albumsReq = new OAuthRequest(Verb.POST,
				"https://api.imgur.com/3/image", service);
		//TODO: erro, nao passa o nome do album nem da imagem
		
//		newPictureReq.addBodyParameter("image", data);//String enconded em base64
//		//O data pode ser, conforme esta na API A binary file, base64 data, or a URL for an image
//		//Nos fizemos com base64 mas talvez funcione com bytes diretamente, mas não sei. Testa.
//		newPictureReq.addBodyParameter("album",(String)albumJson.get("id"));
//		newPictureReq.addBodyParameter("name", picture);
							
		//String image = new String(data);
		
		//albumsReq.addBodyParameter("image", image);
		
		@SuppressWarnings("restriction")
		BASE64Encoder encoder = new BASE64Encoder();
		@SuppressWarnings("restriction")
		String s = encoder.encode(data);
		albumsReq.addBodyParameter("image", s);
		
		albumsReq.addBodyParameter("name", picture);
		String albumName = albumToId.get(album);
		albumsReq.addBodyParameter("album", albumName);
		//albumsReq.addPayload(data);
		service.signRequest(accessToken, albumsReq);
		final com.github.scribejava.core.model.Response albumsRes = albumsReq.send();
		if(albumsRes.getCode()==200){
			System.err.println("sucess");
			try {
				JSONParser parser = new JSONParser();
				JSONObject res;
				res = (JSONObject) parser.parse(albumsRes.getBody());
				JSONObject p = (JSONObject) res.get("data");
				String id = (String) p.get("id");
				String namePic = (String) p.get("name");
				System.out.println("Name online: " + namePic);
				System.out.println("id of new pic: " + id);
				nameToId.put(picture, id);
				
				//TODO temp:
				idToPicName.put(id, picture);
				
//				//colocar a imagem no album correto
//				OAuthRequest picMove = new OAuthRequest(Verb.PUT,
//						"https://api.imgur.com/3/album/"+albumName+"/add", service);
//				picMove.addBodyParameter("ids[]", id);
//				service.signRequest(accessToken, picMove);
//				final com.github.scribejava.core.model.Response picRes = picMove.send();
//				//TODO: verificar codigos
//				if(picRes.getCode() == 200){
//					//System.out.println("Nice");
//					idToPicName.put(id, picture);
//				}
				
				return Response.ok().build();
			} catch (ParseException e) {
				
				e.printStackTrace();
			}
		}
		return Response.status(Status.NOT_FOUND).build();
			
	}

	

	/** To delete all photos before deleting an album
	 * @param album
	 */
	private void deleteAlbumPhotos(String album){
		List<String> pics =  this.getPicturesLocal(album);
		Iterator <String> it = pics.iterator();
		while(it.hasNext())
			this.deletePic(album, it.next());
	}
	
	/**
	 * @param album
	 * @param picture
	 * @return true if the picture was deleted
	 */
	private boolean deletePic(String album, String picture) {
		String picName = nameToId.get(picture);
		OAuthRequest albumsReq = new OAuthRequest(Verb.DELETE,
				"https://api.imgur.com/3/account/GonaloMoncada/image/"+picName, service);
		service.signRequest(accessToken, albumsReq);
		final com.github.scribejava.core.model.Response albumsRes = albumsReq.send();
		if(albumsRes.getCode()==200){
			nameToId.remove(picture);
			return true;
		}	
		return false;
	}
	
	/**
	 * @param album
	 * @return a list with the names of the albuns
	 */
	private List<String> getPicturesLocal(String album) {
		List<String> al = new LinkedList<String>();
		try{
			String albumName = albumToId.get(album);
			OAuthRequest albumsReq = new OAuthRequest(Verb.GET,
					"https://api.imgur.com/3/account/GonaloMoncada/album/"+albumName+"/images", service);
			service.signRequest(accessToken, albumsReq);
			final com.github.scribejava.core.model.Response albumsRes = albumsReq.send();
			if(albumsRes.getCode() != 200)
				return null;
			JSONParser parser = new JSONParser();
			JSONObject res = (JSONObject) parser.parse(albumsRes.getBody());
			JSONArray albums = (JSONArray) res.get("data");
			@SuppressWarnings("rawtypes")
			Iterator albumsIt = albums.iterator();
			while (albumsIt.hasNext()) {
				JSONObject p = (JSONObject) albumsIt.next();
				String piI = (String) p.get("id");
				String name = (String) p.get("name");
				if(!idToPicName.containsKey(piI)){
					//existem imagem no igmur sem nome, temos de lhe atribuir um nome
					if(name == null)
						name = String.valueOf(newName++);
					nameToId.put(name, piI);
					al.add(name);
					idToPicName.put(piI, name);
				}
				else
					al.add(idToPicName.get(piI));
			}
		} catch (ParseException e) {
		}
		return al;
	}

}
