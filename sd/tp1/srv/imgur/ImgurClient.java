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
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;

import sun.misc.BASE64Encoder;

import javax.imageio.ImageIO;
import javax.ws.rs.core.Response.Status;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import sd.tp1.RequestInterface;
import sd.tp1.common.AlbumFolderClass;
import sd.tp1.common.PictureClass;

import com.github.scribejava.apis.ImgurApi;
import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Response;
import com.github.scribejava.core.model.Verb;
import com.github.scribejava.core.oauth.OAuth20Service;




import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;


public class ImgurClient implements RequestInterface{
	public static final int MANAGER_INTERVAL = 30000;
	private String url;
	File basePath;
	private OAuth2AccessToken accessToken;
	private OAuth20Service service;
	//estrutura para associar o nome de uma image com um id
	private Map<String, String> nameToId;
	//Estrutura para associar um nome de um album com o seu id
	private Map<String, String> albumToId;
	//estrutura para associar um id de uma imagem com o seu nome
	private Map<String, String> idToPicName;
	int newName;
	private String authorizationUrl;
	
	private Map<String, byte[]> picsList;
	

	
	
	
	public ImgurClient(OAuth2AccessToken accessToken, OAuth20Service service, String url) {
		this.accessToken =accessToken;
		this.service = service;
		newName = 0;
		nameToId = new HashMap<String, String>();
		albumToId = new HashMap<String, String>();
		idToPicName = new HashMap<String, String>();
		
		basePath = new File("./gallery");
		if (!basePath.exists())
			basePath.mkdir();
		this.url = url;
		picsList = new ConcurrentHashMap<String, byte[]>();
		this.imgurManager();
	}

	@SuppressWarnings("rawtypes")
	@Override
	public List<AlbumFolderClass> getAlbums() {
		List<String> al = new LinkedList<String>();
		
		try{
			OAuthRequest albumsReq = new OAuthRequest(Verb.GET,
					"https://api.imgur.com/3/account/gonalomoncada/albums/ids", service);
			service.signRequest(accessToken, albumsReq);
			final Response albumsRes = albumsReq.send();
			if(albumsRes.getCode() != 200)
				return null;
			JSONParser parser = new JSONParser();
			JSONObject res = (JSONObject) parser.parse(albumsRes.getBody());
			JSONArray albums = (JSONArray) res.get("data");
			Iterator albumsIt = albums.iterator();
			while (albumsIt.hasNext()) {
				String s = albumsIt.next().toString();
				//AlbumFolderClass a = new AlbumFolderClass(s, this.authorizationUrl);
				al.add(s);
			} 
		} catch (ParseException e) {
			e.printStackTrace();
		}
		List<AlbumFolderClass> t = this.getAllAlbumInfo(al);
		return t;
	}
	
	/**
	 * @param l: a list wiht the albuns id's
	 * @return the list with the names of albuns
	 */
	private List<AlbumFolderClass> getAllAlbumInfo(List<String> l){
		List<AlbumFolderClass> al = new LinkedList<AlbumFolderClass>();
		try{
			Iterator <String> it = l.iterator();
			while(it.hasNext()){
				String s = it.next();
				OAuthRequest albumsReq = new OAuthRequest(Verb.GET,
						"https://api.imgur.com/3/account/gonalomoncada/album/" + s, service);
				service.signRequest(accessToken, albumsReq);
				final Response albumsRes = albumsReq.send();
				if(albumsRes.getCode() == 200){
					JSONParser parser = new JSONParser();
					JSONObject res = (JSONObject) parser.parse(albumsRes.getBody());
					JSONObject p = (JSONObject) res.get("data");
					String piI = (String) p.get("id");
					String title = (String) p.get("title");
					albumToId.put(title, piI);
					AlbumFolderClass a = new AlbumFolderClass(title, this.authorizationUrl);
					al.add(a);
				}
			}

		} catch (ParseException e) {
			e.printStackTrace();
		}
		return al;
	}

	@Override
	public List<PictureClass> getPictures(String album) {
		List<PictureClass> al = new LinkedList<PictureClass>();
		try{
			String albumName = albumToId.get(album);
			OAuthRequest albumsReq = new OAuthRequest(Verb.GET,
					"https://api.imgur.com/3/account/gonalomoncada/album/"+albumName+"/images", service);
			service.signRequest(accessToken, albumsReq);
			final Response albumsRes = albumsReq.send();
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
				long datetime = (long) p.get("datetime");
				long size = (long) p.get("size");
				if(!idToPicName.containsKey(piI)){
					//existem imagem no igmur sem nome, temos de lhe atribuir um nome
					if(name == null)
						name = String.valueOf(newName++);
					nameToId.put(name, piI);
					PictureClass pic = new PictureClass(name, this.authorizationUrl);
					pic.datetime =datetime;
					pic.picSize = size;
					al.add(pic);
					idToPicName.put(piI, name);
				}
				else{
					PictureClass pic = new PictureClass(idToPicName.get(piI), this.authorizationUrl);
					al.add(pic);
				}
			}
		} catch (ParseException e) {
		}
		return al;
	}
	
	@Override
	public byte[] getPicture(String album, String picture) {
		if(picsList.containsKey(album+picture))
			return picsList.get(album+picture);
		return null;
	}
	
	private byte[] requestPic(String album, String picture){
		try{
			String picName = nameToId.get(picture);
			OAuthRequest albumsReq = new OAuthRequest(Verb.GET,
					"https://api.imgur.com/3/account/gonalomoncada/image/" + picName, service);
			service.signRequest(accessToken, albumsReq);
			final Response albumsRes = albumsReq.send();
			if(albumsRes.getCode() != 200)
				return null;
			JSONParser parser = new JSONParser();
			JSONObject res = (JSONObject) parser.parse(albumsRes.getBody());
			JSONObject p = (JSONObject) res.get("data");
			String link = (String) p.get("link");
			String mime = (String) p.get("type");
			System.out.println(mime);
			URL imageURL = new URL(link);
			BufferedImage originalImage = ImageIO.read(imageURL);
			ByteArrayOutputStream baos = new ByteArrayOutputStream();

			ImageIO.write(originalImage, mime, baos );
			byte[] imageInByte=baos.toByteArray();
			//System.out.println("here is " + picture);
			return imageInByte;
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public boolean createAlbum(String album) {
		OAuthRequest albumsReq = new OAuthRequest(Verb.POST,
				"https://api.imgur.com/3/album", service);
		albumsReq.addBodyParameter("title", album);
		service.signRequest(accessToken, albumsReq);
		final Response albumsRes = albumsReq.send();
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
			this.crealAlbumResDat(album);
			return true;
		}	
		return false;
	}
	
	@Override
	public boolean deleteAlbum(String album) {
		File f = new File(basePath, album+".dat");
		if(f.exists() && requestAlbumDeletion(album)){
			ObjectInputStream input;
			try {
				input = new ObjectInputStream(new FileInputStream(f));
				AlbumFolderClass albumDat = (AlbumFolderClass)input.readObject();
				input.close();
				albumDat.erase(this.url);
				ObjectOutput out;
				out = new ObjectOutputStream(new FileOutputStream(f));
				out.writeObject(albumDat);
				out.close();
				File dat = new File(basePath + "/" + album + "/album.dat");
				input = new ObjectInputStream(new FileInputStream(dat));
				List<PictureClass> list = (List<PictureClass>)input.readObject();
				input.close();
				for(PictureClass p : list)
					p.erase(this.url);
				out = new ObjectOutputStream(new FileOutputStream(dat));
				out.writeObject(list);
				out.close();
			} catch (IOException e) {
			} catch (ClassNotFoundException e) {
			}
			return true;
		}
		else 
			return false;
	}
	
	private boolean requestAlbumDeletion(String album){


		String albumName = albumToId.get(album);
		OAuthRequest albumsReq = new OAuthRequest(Verb.DELETE,
				"https://api.imgur.com/3/account/doping/album/"+albumName, service);
		service.signRequest(accessToken, albumsReq);
		final Response albumsRes = albumsReq.send();
		if(albumsRes.getCode()==200){
			albumToId.remove(album);
			//apagar as fotos primeiro
			this.deleteAlbumPhotos(album);
			return true;
		}
		return false;
	}
	
	
	
	/** To delete all photos before deleting an album
	 * @param album
	 */
	private void deleteAlbumPhotos(String album){
		List<PictureClass> pics =  this.getPictures(album);
		Iterator <PictureClass> it = pics.iterator();
		while(it.hasNext()){
			this.deletePicture(album, it.next().name);
		}
			
	}
	
	

	@Override
	public boolean deletePicture(String album, String picture) {
		File f = new File(basePath + "/" + album + "/album.dat");
		if (f.exists() && picsList.containsKey(album+picture) && requestPhotoDeletion(album, picture)){
			ObjectInputStream input;
			try {
				File dat = new File(basePath + "/" + album + "/album.dat");
				input = new ObjectInputStream(new FileInputStream(dat));
				List<PictureClass> list = (LinkedList<PictureClass>)input.readObject();
				input.close();
				PictureClass p = list.get(list.indexOf(new PictureClass(picture, this.url)));
				p.erase(this.url);
				ObjectOutput outt;
				outt = new ObjectOutputStream(new FileOutputStream(dat));
				outt.writeObject(list);
				outt.close();
			} catch (IOException e) {
			} catch (ClassNotFoundException e) {
			}
			picsList.remove(album+picture);
			return true;
		}
		return false;
	}
	
	private boolean requestPhotoDeletion(String album, String picture){
		String picName = nameToId.get(picture);
		OAuthRequest albumsReq = new OAuthRequest(Verb.DELETE,
				"https://api.imgur.com/3/account/doping/image/"+picName, service);
		service.signRequest(accessToken, albumsReq);
		final Response albumsRes = albumsReq.send();
		if(albumsRes.getCode()==200){
			nameToId.remove(picture);
			return true;
		}	
		return false;
	}

	
	@Override
	public boolean uploadPicture(String album, String pictureName, byte[] data) {
		File dir = new File(basePath + "/" + album);
		if (dir.exists() && !picsList.containsKey(album+pictureName) && requestPhotoUpdate(album, pictureName, data)) {
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
						return false;
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
			return true;
		}
		else 
			return false;	
	}
	
	
	
	private boolean requestPhotoUpdate(String album, String picture, byte [] data){
		OAuthRequest albumsReq = new OAuthRequest(Verb.POST,
				"https://api.imgur.com/3/image", service);
		BASE64Encoder encoder = new BASE64Encoder();
		String s = encoder.encode(data);
		albumsReq.addBodyParameter("image", s);
		System.out.println("Pic name: " + picture);
		
		if (picture == null) return false;
        // Get position of last '.'.
        int pos = picture.lastIndexOf(".");
        // If there wasn't any '.' just return the string as is.
        if (pos == -1)
        	picture = picture;
        else
        	picture = picture.substring(0, pos);
        System.out.println("No ext: " + picture);

		//String ext = FilenameUtils.getExtension("/path/to/file/foo.txt");
		
		
		albumsReq.addBodyParameter("name", picture);
		String albumName = albumToId.get(album);
		albumsReq.addBodyParameter("album", albumName);
		service.signRequest(accessToken, albumsReq);
		final com.github.scribejava.core.model.Response albumsRes = albumsReq.send();
		if(albumsRes.getCode()==200){
			//System.err.println("sucess");
			try {
				JSONParser parser = new JSONParser();
				JSONObject res;
				res = (JSONObject) parser.parse(albumsRes.getBody());
				JSONObject p = (JSONObject) res.get("data");
				String id = (String) p.get("id");
				//String namePic = (String) p.get("name");
				//System.out.println("Name online: " + namePic);
				//System.out.println("id of new pic: " + id);
				//System.out.println(picture + " uploaded suc");
				nameToId.put(picture, id);
				idToPicName.put(id, picture);
				return true;
			} catch (ParseException e) {

				e.printStackTrace();
			}
		}
		return false;
	}
	
	/**
	 * this method makes the request's to imgur and updates the files in the system
	 */
	private void imgurManager(){
		new Thread(() -> {
			try {
				long t = System.currentTimeMillis();
				List<AlbumFolderClass> l =  this.getAlbums();
//				if(!l.isEmpty()){
					this.updateAlbuns(l);
//				}
//				else
//					deleteDir(new File(basePath,"/"));
//				

				System.err.println("Imgur proxy ready");
				System.out.println("Time to prepare: " + (System.currentTimeMillis() - t));
				while(true){
					//ler os albuns e escrever
					l =  this.getAlbums();
					this.updateAlbuns(l);
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
		File albuns = new File("gallery/");
		List<File> files = new ArrayList<File>(Arrays.asList(albuns.listFiles()));
		List <String> allAbuns = new ArrayList<String>();

		
		String albName;
//		for(File fls : files){
//			int pos = fls.getName().lastIndexOf(".");
//			System.out.println(fls.getName());
//			if (pos != -1){
//				albName = fls.getName().substring(0, pos);
//				System.err.println("Adding: " + albName);
//				allAbuns.add(albName);
//			}
//
//		}
		List <String> found = new LinkedList<String>();
		
		for(AlbumFolderClass al: list){
			String album = al.name;
			this.crealAlbumResDat(album);
			this.createAlbumDat(album);
			found.add(album);
		}
		
//		for (String s: allAbuns){
//			int index = found.indexOf(s);
//			if(index == -1){
//				//dontExists.add(album);
//				System.out.println("Album not found on imgur: DELETING");
//				this.deleteAlbumPhotos(s);
//				found.remove(index);
//			}
//		}

		
		
	}
	
	private void crealAlbumResDat(String album){
		File f = new File(basePath, album);
		File file = new File(basePath,album+".dat");
		if (file.exists()){
			//System.out.println("Found album: " + album);
			ObjectInputStream input;
			AlbumFolderClass albumDat;
			try {
				input = new ObjectInputStream(new FileInputStream(file));
				albumDat = (AlbumFolderClass)input.readObject();
				input.close();
				if(albumDat.isErased()){
					albumDat.recreate(this.url);
					ObjectOutput out;
					out = new ObjectOutputStream(new FileOutputStream(file));
					out.writeObject(albumDat);
					out.close();
				}
			} catch (IOException e) {
			} catch (ClassNotFoundException e) {
			}
		}
		else{
			
			//System.out.println("creating new");
			f.mkdir();
			
			//createAlbumDat(album);
			File albumDat = new File(basePath,album+"/album.dat");
			//System.out.println("Writing on: " + album+"/album.dat");
			List<PictureClass> l = this.getPictures(album);
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

	}
	
	/**
	 * @param album
	 */
	private void createAlbumDat(String album){
		List<PictureClass> l = this.getPictures(album);
		File dir = new File(basePath + "/" + album);
		if (dir.exists()) {
			File dat = new File(basePath, album + "/album.dat");
			//System.out.println("acessing: " + (album + "/album.dat"));
			ObjectInputStream input;
			try {
				input = new ObjectInputStream(new FileInputStream(dat));
				List<PictureClass> listOld = (LinkedList<PictureClass>)input.readObject();
				input.close();
				for(PictureClass pic: l){
					int index = listOld.indexOf(pic);
					if(index < 0){
						//System.out.println("Adding pic: " + pic.getName());
						listOld.add(pic);
						ObjectOutput outt;
						outt = new ObjectOutputStream(new FileOutputStream(dat));
						outt.writeObject(listOld);
						outt.close();
					}
					else{
						//vamos comparar as datas de origem
						PictureClass picOld = listOld.get(index);
						String name = picOld.name;
						//System.out.println("found pic : " + name);
						//quando são a escrita é mais antigo, muda-se a data de origem para a nova
						//como os nomes são iguais, depois recria-se a pic
						if(this.equalsPic(pic, picOld)){
							picOld.datetime = pic.datetime;
							picOld.recreate(this.url);

							if(picsList.containsKey(album+name))
								picsList.replace(album+name, this.requestPic(album, name));
							else
								picsList.put(album+name, this.requestPic(album, name));
						}
						//são iguais ou não é preciso atualizar
						else 
							if(picsList.containsKey(album+name))
								picsList.replace(album+name, this.requestPic(album, name));
							else
								picsList.put(album+name, this.requestPic(album, name));
					}
				}

				ObjectOutput outt;
				outt = new ObjectOutputStream(new FileOutputStream(dat));
				outt.writeObject(listOld);
				outt.close();

			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}

		}	
	}
	
	/**
	 * @param pic
	 * @param picOld
	 * @return true if the newPic is more recent
	 */
	private boolean equalsPic(PictureClass newPic, PictureClass oldPic){
		//só nos interessa saber quando a pic nova é mais recente (podem ser fotos diferentes,
		//mas com o mesmo nome
		if(oldPic.datetime < newPic.datetime)
			return true;
		else if (oldPic.datetime > newPic.datetime)
			return false;
		else if (oldPic.datetime == newPic.datetime)
			//quando tem a mesma data de origem, mas tamanhos diferentes, assumimos que a pic
			//é mais recente, isto é, sofreu alterações
			if(oldPic.datetime != newPic.datetime){
				return true;
		}
		return false;
	}
	
	/**
	 * @param file
	 * to delete a directory and all of it's content's
	 */
	protected void deleteDir(File file) {
		File[] contents = file.listFiles();
		if (contents != null) {
			for (File f : contents) {
				deleteDir(f);
			}
		}
		file.delete();
	}
	
}
