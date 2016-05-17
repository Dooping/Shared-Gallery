package sd.tp1.srvREST;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Arrays;

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
			ArrayList<String> names = new ArrayList<String>(Arrays.asList(basePath.list()));
			return Response.ok(names).build();
		}
		else
			return Response.status(Status.NOT_FOUND).build();
	}

	@GET
	@Path("/{album}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getPictures(@PathParam("album") String a) {
		//System.err.printf("getPictures()\n");
		File f = new File(basePath, a);
		ArrayList<String> names;
		if (f.exists()){
			names = new ArrayList<String>(Arrays.asList(f.list()));
			return Response.ok(names).build();
		}
		else
			return Response.status(Status.NOT_FOUND).build();
		
	}
	
	@GET
	@Path("/{album}/{picture}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getPicture(@PathParam("album") String album, @PathParam("picture") String picture) throws IOException {
		//System.err.printf("getPicture()\n");
		File f = new File(basePath, album);
		if (f.exists()){
			f = new File(basePath, album + "/"+ picture);
			if (f.exists()){
				BasicFileAttributes attrs = Files.readAttributes(f.toPath(), BasicFileAttributes.class);
				PictureClass pic = new PictureClass(picture, Files.readAllBytes(Paths.get(basePath+"/"+album + "/"+ picture)), attrs.lastModifiedTime().toMillis(),url);
				return Response.ok(pic).build();
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
		if (f.exists())
			return Response.status(422).build();
		else{
			f.mkdir();
			return Response.ok().build();
		}
			
	}
	
	@DELETE
	@Path("/{album}")
	public Response deleteAlbum(@PathParam("album") String album) {
		//System.err.printf("deleteAlbum()\n");
		File f = new File(basePath, album);
		if (f.exists()){
			deleteDir(f);
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
	
	@DELETE
	@Path("/{album}/{picture}")
	public Response deletePicture(@PathParam("album") String album, @PathParam("picture") String picture) {
		//System.err.printf("deletePicture()\n");
		File f = new File(basePath, album+"/"+picture);
		if (f.exists()){
			f.delete();
			return Response.ok().build();
		}
		return Response.status(Status.NOT_FOUND).build();	
	}
	
	@POST
	@Path("/{album}/{pictureName}")
	@Consumes(MediaType.APPLICATION_OCTET_STREAM)
	public Response uploadPicture(@PathParam("album") String album, @PathParam("pictureName") String pictureName, byte[] picture) throws IOException {
		//System.err.printf("uploadPicture()\n");
		File dir = new File(basePath + "/" + album);
		if (dir.exists()) {
			dir = new File(basePath, album + "/"+ pictureName);
			
			if (!dir.exists()){
				FileOutputStream out = new FileOutputStream(dir);
				out.write(picture);
				out.close();
				return Response.ok().build();
			}
			else return Response.status(422).build();

		}
		else 
			return Response.status(Status.NOT_FOUND).build();
			
	}
	
//	@GET
//	@Path("/get/{filename}")
//	@Produces("image/jpg")
//	public Response getPicture(@PathParam("filename") String filename) {
//		File file = new File(basePath+ "/" + filename);
//		System.out.println("request " + filename);
//		try {
//			if (file.exists()) {
//				System.out.println("Found");
//				
//				Response rep = Response.ok(file).build();
//				rep.getHeaders().add("Access-Control-Allow-Origin",  "*");
//				System.out.println(rep);
//				return rep;
//			}else
//				return Response.status(Status.NOT_FOUND).build();
//		} catch( Exception x ) {
//			return Response.status(Status.BAD_REQUEST).build();
//		} 
//	}
	

}

