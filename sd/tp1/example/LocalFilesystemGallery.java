package sd.tp1.example;

import javafx.application.Application;
import javafx.stage.Stage;
import sd.tp1.gui.impl.GalleryWindow;

/*
 * Launches the local filesystem gallery application, pointing to the current directory.
 */
public class LocalFilesystemGallery extends Application {

	GalleryWindow window;
	
	public LocalFilesystemGallery() {
		window = new GalleryWindow( new LocalFilesystemGalleryContentProvider("."));
	}	
	
	
    public static void main(String[] args){
        launch(args);
    }
    
	@Override
	public void start(Stage primaryStage) throws Exception {
		window.start(primaryStage);
	}
}
