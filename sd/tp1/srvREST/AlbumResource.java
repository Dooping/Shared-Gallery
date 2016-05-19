package sd.tp1.srvREST;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

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

import sd.tp1.common.AlbumFolderClass;
import sd.tp1.common.PictureClass;

@Path("/albums")
//@Path("")
public class AlbumResource {
	File basePath;
	String url;

	public AlbumResource(){
		super();
		basePath = new File("./gallery");
		if (!basePath.exists())
			basePath.mkdir();
	}

	public void setUrl(String url){
		this.url = url;
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAlbums() {
		//System.err.printf("getAlbums()\n");
		if (basePath.exists()){
			ArrayList<File> names = new ArrayList<File>(Arrays.asList(basePath.listFiles()));
			ObjectInputStream input;
			ArrayList<AlbumFolderClass> albums = new ArrayList<>();
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

	@SuppressWarnings("unchecked")
	@GET
	@Path("/{album}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getPictures(@PathParam("album") String a) {
		//System.err.printf("getPictures()\n");
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
		//System.err.printf("getPicture()\n");
		File f = new File(basePath, album);
		if (f.exists()){
			f = new File(basePath, album + "/"+ picture);
			if (f.exists()){
				return Response.ok(Files.readAllBytes(Paths.get(basePath+"/"+album + "/"+ picture))).build();
			}
			else
				return Response.status(Status.NOT_FOUND).build();
		}
		else
			return Response.status(Status.NOT_FOUND).build();

	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public Response createAlbum(String album) {
		//System.err.printf("createAlbum()\n");
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
					return Response.status(422).build();
				albumDat.recreate();
			} catch (IOException e) {
			} catch (ClassNotFoundException e) {
			}
			return Response.ok().build();
		}
		else{
			f.mkdir();
			File albumDat = new File(basePath,album+"/album.dat");
			List<PictureClass> list = new LinkedList<>();
			AlbumFolderClass a = new AlbumFolderClass(album, this.url);
			ObjectOutput out;
			try {
				out = new ObjectOutputStream(new FileOutputStream(file));
				out.writeObject(a);
				out.close();
				out = new ObjectOutputStream(new FileOutputStream(albumDat));
				out.writeObject(list);
				out.close();
			} catch (IOException e) {}
			return Response.ok().build();
		}
	}

	@DELETE
	@Path("/{album}")
	public Response deleteAlbum(@PathParam("album") String album) {
		File f = new File(basePath, album+".dat");
		if (f.exists()){
			ObjectInputStream input;
			try {
				input = new ObjectInputStream(new FileInputStream(f));
				AlbumFolderClass albumDat = (AlbumFolderClass)input.readObject();
				input.close();
				albumDat.erase();
				ObjectOutput out;
				out = new ObjectOutputStream(new FileOutputStream(f));
				out.writeObject(albumDat);
				out.close();
			} catch (IOException e) {
			} catch (ClassNotFoundException e) {
			}
			//deleteDir(f);
			return Response.ok().build();
		}
		return Response.status(Status.NOT_FOUND).build();	
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

	@SuppressWarnings("unchecked")
	@DELETE
	@Path("/{album}/{picture}")
	public Response deletePicture(@PathParam("album") String album, @PathParam("picture") String picture) {
		//System.err.printf("deletePicture()\n");
		File f = new File(basePath, album+"/"+picture);
		if (f.exists()){
			ObjectInputStream input;
			try {
				File dat = new File(basePath + "/" + album + "/album.dat");
				input = new ObjectInputStream(new FileInputStream(dat));
				List<PictureClass> list = (LinkedList<PictureClass>)input.readObject();
				input.close();
				PictureClass p = list.get(list.indexOf(new PictureClass(picture, this.url)));
				p.erase();
				ObjectOutput outt;
				outt = new ObjectOutputStream(new FileOutputStream(dat));
				outt.writeObject(list);
				outt.close();
			} catch (IOException e) {
			} catch (ClassNotFoundException e) {
			}
			//f.delete();
			return Response.ok().build();
		}
		return Response.status(Status.NOT_FOUND).build();	
	}

	@SuppressWarnings("unchecked")
	@POST
	@Path("/{album}/{pictureName}/{isNew}")
	@Consumes(MediaType.APPLICATION_OCTET_STREAM)
	public Response uploadPicture(@PathParam("album") String album, @PathParam("pictureName") String pictureName, @PathParam("isNew") boolean isNew, byte[] picture) throws IOException {
		//System.err.printf("uploadPicture()\n");
		File dir = new File(basePath + "/" + album);
		if (dir.exists()) {
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
					pic.recreate();
				}
				FileOutputStream out = new FileOutputStream(dir);
				out.write(picture);
				out.close();
					
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
			return Response.ok().build();
		}
		else 
			return Response.status(Status.NOT_FOUND).build();

	}


}

