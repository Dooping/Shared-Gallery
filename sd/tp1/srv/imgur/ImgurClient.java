package sd.tp1.srv.imgur;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

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

public class ImgurClient implements RequestInterface{
	
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
	
	public ImgurClient(OAuth2AccessToken accessToken, OAuth20Service service) {
		this.accessToken =accessToken;
		this.service = service;
		newName = 0;
		nameToId = new HashMap<String, String>();
		albumToId = new HashMap<String, String>();
		idToPicName = new HashMap<String, String>();
	}

	@SuppressWarnings("rawtypes")
	@Override
	public List<AlbumFolderClass> getAlbums() {
		List<String> al = new LinkedList<String>();
		
		try{
			OAuthRequest albumsReq = new OAuthRequest(Verb.GET,
					"https://api.imgur.com/3/account/doping/albums/ids", service);
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
						"https://api.imgur.com/3/account/doping/album/" + s, service);
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
					"https://api.imgur.com/3/account/doping/album/"+albumName+"/images", service);
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
				//System.out.println(datetime);
				//System.out.println("Name: " + name);
				//System.out.println("width: " + p.get("width"));
				//System.out.println("height: " + p.get("height"));
				//System.out.println("size: " + p.get("size"));

				if(!idToPicName.containsKey(piI)){
					//existem imagem no igmur sem nome, temos de lhe atribuir um nome
					if(name == null)
						name = String.valueOf(newName++);
					nameToId.put(name, piI);
					PictureClass pic = new PictureClass(name, this.authorizationUrl);
					pic.setDatetime(datetime);
					pic.setPicSize(size);
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
		try{
			String picName = nameToId.get(picture);
			OAuthRequest albumsReq = new OAuthRequest(Verb.GET,
					"https://api.imgur.com/3/account/doping/image/" + picName, service);
			service.signRequest(accessToken, albumsReq);
			final Response albumsRes = albumsReq.send();
			if(albumsRes.getCode() != 200)
				return null;
			JSONParser parser = new JSONParser();
			JSONObject res = (JSONObject) parser.parse(albumsRes.getBody());
			JSONObject p = (JSONObject) res.get("data");
			String link = (String) p.get("link");
			URL imageURL = new URL(link);
			BufferedImage originalImage = ImageIO.read(imageURL);
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ImageIO.write(originalImage, "jpg", baos );
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
			return true;
		}	
		return false;
	}

	@Override
	public boolean deleteAlbum(String album) {
		//apagar as fotos primeiro
		this.deleteAlbumPhotos(album);
		String albumName = albumToId.get(album);
		OAuthRequest albumsReq = new OAuthRequest(Verb.DELETE,
				"https://api.imgur.com/3/account/doping/album/"+albumName, service);
		service.signRequest(accessToken, albumsReq);
		final Response albumsRes = albumsReq.send();
		if(albumsRes.getCode()==200){
			albumToId.remove(album);
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
	public boolean uploadPicture(String album, String picture, byte[] data) {
		OAuthRequest albumsReq = new OAuthRequest(Verb.POST,
				"https://api.imgur.com/3/image", service);
		BASE64Encoder encoder = new BASE64Encoder();
		String s = encoder.encode(data);
		albumsReq.addBodyParameter("image", s);

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
				String namePic = (String) p.get("name");
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
	

	
	
}
