package sd.tp1;

import javafx.application.Application;
import javafx.stage.Stage;
import sd.tp1.gui.impl.GalleryWindow;

/*
 * Launches the local shared gallery application.
 */
public class SharedGallery extends Application {

	GalleryWindow window;
	static String messageServerHost;
	
	public SharedGallery() {
		window = new GalleryWindow( new SharedGalleryContentProvider(messageServerHost));
	}	
	
	
    public static void main(String[] args){
    	if ((args.length != 1))
			throw new IllegalArgumentException("Syntax: SharedGallery <messageServerHost>");
    	messageServerHost = args[0];
        launch(args);
    }
    
	@Override
	public void start(Stage primaryStage) throws Exception {
		window.start(primaryStage);
	}
}
