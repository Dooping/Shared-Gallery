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
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
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
	public static final int MANAGER_INTERVAL = 10000;
	private String url;
	private Map<String, byte[]> picsList;
	
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
		picsList = new ConcurrentHashMap<String, byte[]>();
		imgur = new ImgurClient(accessToken, service);
		//this.imgurManager();
	}
	
	public void setUrl (String url){
		this.url = url;
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

	
	/**
	 * this method makes the request's to imgur and updates the files in the system
	 */
	private void imgurManager(){
		new Thread(() -> {
			try {

				//ler os albuns e escrever
				List<AlbumFolderClass> l =  imgur.getAlbums();
				this.updateAlbuns(l);

				while(true){
					l =  imgur.getAlbums();




					Thread.sleep(MANAGER_INTERVAL);
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}).start();
	}


	
	/**
	 * @param list
	 */
	private  void updateAlbuns(List<AlbumFolderClass> list){
		for(AlbumFolderClass al: list){
			String album = al.name;
			File f = new File(basePath, album);
			File file = new File(basePath,album+".dat");
			if (file.exists()){
				ObjectInputStream input;
				AlbumFolderClass albumDat;
				try {
					input = new ObjectInputStream(new FileInputStream(file));
					albumDat = (AlbumFolderClass)input.readObject();
					input.close();
					if(!albumDat.isErased())
						albumDat.recreate(this.url);
					ObjectOutput out;
					out = new ObjectOutputStream(new FileOutputStream(file));
					out.writeObject(albumDat);
					out.close();
				} catch (IOException e) {
				} catch (ClassNotFoundException e) {
				}
			}
			else{
				f.mkdir();
				File albumDat = new File(basePath,album+"/album.dat");
				List<PictureClass> l = new LinkedList<>();
				AlbumFolderClass a = new AlbumFolderClass(album, this.url);
				ObjectOutput out;
				try {
					out = new ObjectOutputStream(new FileOutputStream(file));
					out.writeObject(a);
					out.close();
					out = new ObjectOutputStream(new FileOutputStream(albumDat));
					out.writeObject(l);
					out.close();
				} catch (IOException e) {}
			}
		this.createAlbumDat(album);
		}
	}
	
	/**
	 * @param album
	 */
	private void createAlbumDat(String album){
		List<PictureClass> l = imgur.getPictures(album);
		File dir = new File(basePath + "/" + album);
			if (dir.exists()) {
				File dat = new File(basePath + "/" + album + "/album.dat");
				ObjectInputStream input;
				try {
					input = new ObjectInputStream(new FileInputStream(dat));
					List<PictureClass> listOld = (LinkedList<PictureClass>)input.readObject();
					input.close();
					for(PictureClass pic: l){
						this.comparePic(pic, dat, listOld);
						
						//colocar na cache cache
						String name = pic.getName();
						picsList.put(name, imgur.getPicture(album,name ));
					}
	
				} catch (IOException e) {
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
			}	
	}
	
	private void comparePic(PictureClass pic, File dat, List<PictureClass> listOld){
		try {
			int index = listOld.indexOf(pic);
			if(index < 0){
				listOld.add(pic);
				ObjectOutput outt;
				outt = new ObjectOutputStream(new FileOutputStream(dat));
				outt.writeObject(listOld);
				outt.close();
			}
			else{
				//vamos comparar as datas de origem
				PictureClass picOld = listOld.get(index);
				//quando s�o a escrita � mais antigo, muda-se a data de origem para a nova
				//como os nomes s�o iguais, depois recria-se a pic
				if(picOld.getDatetime() < pic.getDatetime()){
					picOld.setDatetime(pic.getDatetime());
					picOld.recreate(this.url);
				}
				//a foto � a mesma, mas editada
				else if(picOld.getPicSize() != pic.getPicSize()){
					picOld.recreate(this.url);
				}

				ObjectOutput outt;
				outt = new ObjectOutputStream(new FileOutputStream(dat));
				outt.writeObject(listOld);
				outt.close();
			}
		}catch (IOException e) {
			e.printStackTrace();
		} 
		
	}
	
	private void deleteDir(File file) {
		File[] contents = file.listFiles();
		if (contents != null) {
			for (File f : contents) {
				deleteDir(f);
			}
		}
		file.delete();
	}

}
