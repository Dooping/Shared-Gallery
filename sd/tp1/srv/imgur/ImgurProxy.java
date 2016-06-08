package sd.tp1.srv.imgur;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

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

import sd.tp1.ServerObjectClass;
import sd.tp1.common.AlbumFolderClass;
import sd.tp1.common.PictureClass;
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
	public static final int MANAGER_INTERVAL = 100000;
	private String url;
	//nota: a chave é o nome do album + o nome da picture
	private Map<String, byte[]> picsList;
	
	public ImgurProxy(){
		super();		
		basePath = new File("./gallery");
		if (!basePath.exists())
			basePath.mkdir();
		
		OAuth2AccessToken accessToken = null;
		OAuth20Service service = null;
		
		try {
			final String apiKey = "160aaaca82d1aeb"; 
			final String apiSecret = "ee723f09e99233816e40288beadb33507d269ee4"; 
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
		picsList = new ConcurrentHashMap<String, byte[]>();
		//System.out.println(url);
		imgur = new ImgurClient(accessToken, service, url);

	}
	
	public void setUrl (String url){
		this.url = url;
		imgur.url = url;
	}
	
	public ImgurClient getImgurClient(){
		return this.imgur;
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAlbums() {
		if (basePath.exists()){
			List<File> names = new ArrayList<File>(Arrays.asList(basePath.listFiles()));
			ObjectInputStream input;
			List<AlbumFolderClass> albums = new ArrayList<>();
			for(File f : names){
				try {
					input = new ObjectInputStream(new FileInputStream(f));
					albums.add((AlbumFolderClass)input.readObject());
					input.close();
				} catch (IOException e) {
				} catch (ClassNotFoundException e) {
				}
			}
			return Response.ok(albums).build();
		}
		else
			return Response.status(Status.NOT_FOUND).build();

	}

	@GET
	@Path("/{album}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getPictures(@PathParam("album") String a) {
		File f = new File(basePath, a+"/album.dat");
		if (f.exists()){
			ObjectInputStream input;
			List<PictureClass> pictures;
			try {
				input = new ObjectInputStream(new FileInputStream(f));
				pictures = (LinkedList<PictureClass>)input.readObject();
				input.close();
				return Response.ok(pictures).build();
			} catch (IOException e) {
			} catch (ClassNotFoundException e) {
			}
			return Response.status(Status.BAD_REQUEST).build();
		}
		else
			return Response.status(Status.NOT_FOUND).build();
	}
	
	@GET
	@Path("/{album}/{picture}")
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	public Response getPicture(@PathParam("album") String album, @PathParam("picture") String picture) throws IOException {
		byte[] p = imgur.getPicture(album, picture);
		if(p!= null)
			return Response.ok(p).build();
		return Response.status(Status.NOT_FOUND).build();
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public Response createAlbum(String album) {
		if(imgur.createAlbum(album)){
			return Response.ok().build();
		}
		return Response.status(422).build();
	}
	
	@DELETE
	@Path("/{album}")
	public Response deleteAlbum(@PathParam("album") String album) {
		 if(imgur.deleteAlbum(album))
		//imgur.deleteAlbum(album);
			return Response.ok().build();	
		return Response.status(Status.NOT_FOUND).build();	
	}
	
	@DELETE
	@Path("/{album}/{picture}")
	public Response deletePicture(@PathParam("album") String album, @PathParam("picture") String picture) {
		if ( imgur.deletePicture(album, picture))
			return Response.ok().build();
		return Response.status(Status.NOT_FOUND).build();	
	}
	
	@POST
	@Path("/{album}/{pictureName}")
	@Consumes(MediaType.APPLICATION_OCTET_STREAM)
	public Response uploadPicture(@PathParam("album") String album, @PathParam("pictureName") String pictureName, byte[] data) throws IOException {
		File dir = new File(basePath + "/" + album);
		if (dir.exists() && !picsList.containsKey(album+pictureName) && imgur.uploadPicture(album, pictureName, data)) {
			dir = new File(basePath, album + "/"+ pictureName);
			File dat = new File(basePath + "/" + album + "/album.dat");
			ObjectInputStream input;
			try {
				input = new ObjectInputStream(new FileInputStream(dat));
				List<PictureClass> list = (LinkedList<PictureClass>)input.readObject();
				input.close();
				System.out.println(this.url);
				PictureClass pic = new PictureClass(pictureName, this.url);
				int index = list.indexOf(pic);
				if(index < 0){
					list.add(new PictureClass(pictureName, this.url));
					ObjectOutput outt;
					outt = new ObjectOutputStream(new FileOutputStream(dat));
					outt.writeObject(list);
					outt.close();
				}
				else{
					if (!list.get(index).isErased())
						return Response.status(422).build();
					pic = list.get(index);
					pic.recreate(this.url);
					ObjectOutput outt;
					outt = new ObjectOutputStream(new FileOutputStream(dat));
					outt.writeObject(list);
					outt.close();
				}
					
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
			//System.out.println("good pic upload!");
			picsList.put(album+pictureName, data);
			return Response.ok().build();
		}
		else 
			return Response.status(Status.NOT_FOUND).build();
	}


}
