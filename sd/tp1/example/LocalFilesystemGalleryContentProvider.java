package sd.tp1.example;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import sd.tp1.gui.GalleryContentProvider;
import sd.tp1.gui.Gui;

/*
 * This class provides the album/picture content to the gui/main application.
 * 
 * The content is stored in a folder of the local filesystem.
 * 
 * An album maps to a folder
 * 
 * A picture to a file of the proper extension/format.
 * 
 * Deletes are simulated by renaming the objects. 
 * 
 */
public class LocalFilesystemGalleryContentProvider implements GalleryContentProvider{

	Gui gui;	
	final File root;

	LocalFilesystemGalleryContentProvider(String rootDir) {
		root = new File(rootDir);
		if (!root.isDirectory())
			throw new RuntimeException(rootDir + " is not a directory...");
	}

	
	@Override
	// Downcall from the GUI to register itself, so that it can be updated via upcalls.
	// As an example, asks the GUI to update the fist album in the list every 5 seconds.
	public void register(Gui gui) {
		if( this.gui == null ) {
			this.gui = gui;
			new Thread(()->{
				for(;;) {
					List<Album> l = getListOfAlbums();
					if( ! l.isEmpty() )
						gui.updateAlbum( l.iterator().next() );
					try {
						Thread.sleep(5000);
					} catch (Exception e) {}
				}
			}).start();
		}
	}
	
	

	//
	// Returns a list of albums, consisting in all the folders under root, whose name
	// does not start with . or end with .deleted.
	@Override
	public List<Album> getListOfAlbums() {
		return Arrays.asList(root.listFiles())
				.stream()
				.filter(f -> f.isDirectory() && ! f.getName().endsWith(".deleted") && ! f.getName().startsWith("."))
				.map(f -> new FileAlbum(f))
				.collect(Collectors.toList());
	}

	//
	// Returns a list of pictures of an album. The album is a folder under root, the pictures are
	// image files of the proper extension name does not start with . or end with .deleted.
	@Override
	public List<Picture> getListOfPictures(Album album) {
		FileAlbum fa = (FileAlbum) album;
		return Arrays.asList(fa.dir.listFiles())
				.stream()
				.filter(f -> isPicture(f))
				.map(f -> new FilePicture(f))
				.collect(Collectors.toList());
	}

	// Returns the data associated with a picture...
	@Override
	public byte[] getPictureData(Album album, Picture picture) {
		return ((FilePicture) picture).getData();
	}

	@Override
	public Album createAlbum(String name) {
		File dir = new File(root.getAbsolutePath() + "/" + name);
		if (!dir.exists()) {
			dir.mkdir();
			return new FileAlbum(dir);
		}
		return null;
	}

	@Override
	public void deleteAlbum(Album album) {
		FileAlbum fa = (FileAlbum)album;
		fa.dir.renameTo( new File( fa.dir.getAbsolutePath() + ".deleted"));
	}
	
	@Override
	public Picture uploadPicture(Album album, String name, byte[] data) {
		File file = new File(root.getAbsolutePath() + "/" + album.getName() + "/" + name);
		if (!file.exists())
			try {
				Files.write(file.toPath(), data, StandardOpenOption.CREATE_NEW);
				return new FilePicture(file);
			} catch (IOException e) {
				e.printStackTrace();
			}
		return null;
	}

	@Override
	public boolean deletePicture(Album album, Picture picture) {
		FilePicture fp = (FilePicture)picture;
		fp.file.renameTo( new File( fp.file.getAbsolutePath() + ".deleted"));
		return true;
	}

	
	
	static class FileAlbum implements GalleryContentProvider.Album {
		final File dir;

		FileAlbum(File dir) {
			this.dir = dir;
		}

		@Override
		public String getName() {
			return dir.getName();
		}
	}

	static class FilePicture implements GalleryContentProvider.Picture {
		final File file;

		FilePicture(File file) {
			this.file = file;
		}

		@Override
		public String getName() {
			return file.getName();
		}

		byte[] getData() {
			try {
				return Files.readAllBytes(file.toPath());
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}
	}

	static boolean isPicture(File f) {
		String filename = f.getName();
		int i = filename.lastIndexOf('.');
		String ext = i < 0 ? "" : filename.substring(i + 1).toLowerCase();
		return f.isFile() && EXTENSIONS.contains(ext) && !filename.startsWith(".") && !filename.endsWith(".deleted");
	}

	static final List<String> EXTENSIONS = Arrays.asList(new String[] { "jpg", "jpeg", "png" });


	
}
