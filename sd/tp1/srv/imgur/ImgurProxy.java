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
		
		imgur = new ImgurClient(accessToken, service, url);
		//this.imgurManager();
	}
	
	public void setUrl (String url){
		this.url = url;
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
			//this.crealAlbumResDat(album);
			return Response.ok().build();
		}
		return Response.status(422).build();
	}
	
	@DELETE
	@Path("/{album}")
	public Response deleteAlbum(@PathParam("album") String album) {
		 if(imgur.deleteAlbum(album))
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
					if (!pic.isErased())
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
		
		
		//return imgur.uploadPicture(album, picture, data) ? Response.ok().build() : Response.status(Status.NOT_FOUND).build();
	}

//	/**
//	 * this method makes the request's to imgur and updates the files in the system
//	 */
//	private void imgurManager(){
//		new Thread(() -> {
//			try {
//				long t = System.currentTimeMillis();
//				List<AlbumFolderClass> l =  imgur.getAlbums();
//				this.updateAlbuns(l);
//				System.err.println("Imgur proxy ready");
//				System.out.println("Time to prepare: " + (System.currentTimeMillis() - t));
//				while(true){
//					//ler os albuns e escrever
//					l =  imgur.getAlbums();
//					this.updateAlbuns(l);
//					Thread.sleep(MANAGER_INTERVAL);
//				}
//
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//		}).start();
//	}
//
//	/**
//	 * @param list
//	 */
//	private  void updateAlbuns(List<AlbumFolderClass> list){
//		for(AlbumFolderClass al: list){
//			String album = al.name;
//			this.crealAlbumResDat(album);
//			this.createAlbumDat(album);
//		}
//	}
//	
//	private void crealAlbumResDat(String album){
//		File f = new File(basePath, album);
//		File file = new File(basePath,album+".dat");
//		if (file.exists()){
//			//System.out.println("Found album");
//			ObjectInputStream input;
//			AlbumFolderClass albumDat;
//			try {
//				input = new ObjectInputStream(new FileInputStream(file));
//				albumDat = (AlbumFolderClass)input.readObject();
//				input.close();
//				if(albumDat.isErased()){
//					albumDat.recreate(this.url);
//					ObjectOutput out;
//					out = new ObjectOutputStream(new FileOutputStream(file));
//					out.writeObject(albumDat);
//					out.close();
//				}
//			} catch (IOException e) {
//			} catch (ClassNotFoundException e) {
//			}
//		}
//		else{
//			//System.out.println("creating new");
//			f.mkdir();
//			File albumDat = new File(basePath,album+"/album.dat");
//			List<PictureClass> l = new LinkedList<>();
//			AlbumFolderClass a = new AlbumFolderClass(album, this.url);
//			ObjectOutput out;
//			try {
//				out = new ObjectOutputStream(new FileOutputStream(file));
//				out.writeObject(a);
//				out.close();
//				out = new ObjectOutputStream(new FileOutputStream(albumDat));
//				out.writeObject(l);
//				out.close();
//			} catch (IOException e) {}
//		}
//
//	}
//	
//	/**
//	 * @param album
//	 */
//	private void createAlbumDat(String album){
//		List<PictureClass> l = imgur.getPictures(album);
//		File dir = new File(basePath + "/" + album);
//		if (dir.exists()) {
//			File dat = new File(basePath + "/" + album + "/album.dat");
//			ObjectInputStream input;
//			try {
//				input = new ObjectInputStream(new FileInputStream(dat));
//				List<PictureClass> listOld = (LinkedList<PictureClass>)input.readObject();
//				input.close();
//				for(PictureClass pic: l){
//					int index = listOld.indexOf(pic);
//					if(index < 0){
//						//System.out.println("Adding pic: " + pic.getName());
//						listOld.add(pic);
//						ObjectOutput outt;
//						outt = new ObjectOutputStream(new FileOutputStream(dat));
//						outt.writeObject(listOld);
//						outt.close();
//					}
//					else{
//						//vamos comparar as datas de origem
//						PictureClass picOld = listOld.get(index);
//						String name = picOld.name;
//						//System.out.println("found pic : " + name);
//						//quando são a escrita é mais antigo, muda-se a data de origem para a nova
//						//como os nomes são iguais, depois recria-se a pic
//						if(this.equalsPic(pic, picOld)){
//							picOld.datetime = pic.datetime;
//							picOld.recreate(this.url);
//							
//							if(picsList.containsKey(album+name))
//								picsList.replace(album+name, imgur.getPicture(album, name));
//							else
//								picsList.put(album+name, imgur.getPicture(album, name));
//						}
//						//são iguais ou não é preciso atualizar
//						else 
//							if(picsList.containsKey(album+name))
//								picsList.replace(album+name, imgur.getPicture(album, name));
//							else
//								picsList.put(album+name, imgur.getPicture(album, name));
//					}
//				}
//
//				ObjectOutput outt;
//				outt = new ObjectOutputStream(new FileOutputStream(dat));
//				outt.writeObject(listOld);
//				outt.close();
//
//			} catch (IOException e) {
//				e.printStackTrace();
//			} catch (ClassNotFoundException e) {
//				e.printStackTrace();
//			}
//		}	
//	}
//	
//	/**
//	 * @param pic
//	 * @param picOld
//	 * @return true if the newPic is more recent
//	 */
//	private boolean equalsPic(PictureClass newPic, PictureClass oldPic){
//		//só nos interessa saber quando a pic nova é mais recente (podem ser fotos diferentes,
//		//mas com o mesmo nome
//		if(oldPic.datetime < newPic.datetime)
//			return true;
//		else if (oldPic.datetime > newPic.datetime)
//			return false;
//		else if (oldPic.datetime == newPic.datetime)
//			//quando tem a mesma data de origem, mas tamanhos diferentes, assumimos que a pic
//			//é mais recente, isto é, sofreu alterações
//			if(oldPic.datetime != newPic.datetime){
//				return true;
//		}
//		return false;
//	}
}
