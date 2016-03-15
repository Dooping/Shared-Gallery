package sd.tp1.gui.impl;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseButton;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.TilePane;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;
import javafx.stage.Screen;
import javafx.stage.Stage;
import sd.tp1.gui.GalleryContentProvider;
import sd.tp1.gui.GalleryContentProvider.Album;
import sd.tp1.gui.GalleryContentProvider.Picture;
import sd.tp1.gui.Gui;

/*
 * Implements the Gallery.
 * 
 * This is a JavaFX GUI.
 * 
 * Note: The GUI uses drag and drop from the OS window system into the gui to add new pictures to the current album.
 * 
 * Drag and drop, within the gui, into the trash bin icon performs a delete of the dragged album/picture. 
 * It does not request confirmation as a proper gui would do.
 * 
 */
public class GalleryWindow implements Gui {
	static final int PADDING = 10;
	static final int WORKER_THREADS = 4;
	static final int THUMBNAIL_WIDTH = 80;

	Stage primaryStage;
	Scene albums;

	AlbumView currentAlbum;

	GalleryContentProvider contentProvider;
	ExecutorService executor = Executors.newFixedThreadPool(WORKER_THREADS);

	public GalleryWindow(GalleryContentProvider contentProvider) {
		this.contentProvider = contentProvider;
	}

	public void start(Stage stage) throws Exception {
		primaryStage = stage;
		primaryStage.setWidth(Screen.getPrimary().getVisualBounds().getWidth() / 2);
		primaryStage.setHeight(2 * Screen.getPrimary().getVisualBounds().getHeight() / 3);

		this.contentProvider.register(this);
		createAlbums(stage);
		showAlbums();
	}

	@Override
	public void updateAlbums() {
		Platform.runLater(() -> {
			createAlbums(primaryStage);
			showAlbums();
		});
	}

	@Override
	public void updateAlbum(Album album) {
		if (currentAlbum != null && album.getName().equals(currentAlbum.album.getName()))
			Platform.runLater(() -> {
				currentAlbum.showAlbum(primaryStage);
			});
	}

	void createAlbums(Stage stage) {
		ScrollPane root = new ScrollPane();
		TilePane tile = new TilePane();

		tile.getStyleClass().add("albums");

		tile.setPadding(new Insets(PADDING));
		tile.setHgap(PADDING);
		tile.setVgap(PADDING);

		root.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
		root.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

		root.setFitToWidth(true);
		root.setFitToHeight(true);
		root.setContent(tile);

		albums = new Scene(root);
		albums.getStylesheets().add(getClass().getResource("gallery.css").toExternalForm());

		contentProvider.getListOfAlbums().forEach(album -> {
			final AlbumView av = new AlbumView(album);
			tile.getChildren().add(av);
		});

		tile.getChildren().add(new AddAlbumIcon(tile));
		tile.getChildren().add(new TrashIcon());

		stage.setTitle("Albums");
		stage.setScene(albums);
		stage.show();

		stage.setOnCloseRequest(event -> System.exit(0));
	}

	void showAlbums() {
		primaryStage.setTitle("Albums");
		primaryStage.setScene(albums);
		primaryStage.show();
		primaryStage.setOnCloseRequest(event -> System.exit(0));
		currentAlbum = null;
	}

	class TrashIcon extends javafx.scene.control.Label {
		ImageView view;

		TrashIcon() {
			super.getStyleClass().add("trash");

			view = new ImageView(trashIcon);
			view.setFitWidth(THUMBNAIL_WIDTH / 2);
			view.setFitHeight(THUMBNAIL_WIDTH / 2);
			super.setGraphic(view);

			super.setContentDisplay(ContentDisplay.TOP);
			super.setTextAlignment(TextAlignment.CENTER);

			this.setOnDragOver((ev) -> {
				ev.acceptTransferModes(TransferMode.LINK);
				ev.consume();
			});

			this.setOnDragDropped((ev) -> {
				ev.setDropCompleted(true);
				ev.consume();
			});
		}
	}

	class AddAlbumIcon extends javafx.scene.control.Label {
		ImageView view;
		TilePane pane;

		AddAlbumIcon(TilePane pane) {
			this.pane = pane;
			super.getStyleClass().add("add_album");

			view = new ImageView(newIcon);
			view.setFitWidth(THUMBNAIL_WIDTH / 2);
			view.setFitHeight(THUMBNAIL_WIDTH / 2);
			view.setPreserveRatio(true);
			super.setGraphic(view);

			super.setContentDisplay(ContentDisplay.TOP);
			super.setTextAlignment(TextAlignment.CENTER);

			setOnMouseClicked((ev) -> {
				TextInputDialog dialog = new TextInputDialog();
				dialog.getDialogPane().getStyleClass().add("dialog");
				dialog.getDialogPane().getStylesheets().add(getClass().getResource("gallery.css").toExternalForm());
				;
				dialog.getEditor().getStylesheets().add(getClass().getResource("gallery.css").toExternalForm());

				dialog.setTitle("");
				dialog.setGraphic(new ImageView(albumIcon));
				dialog.setHeaderText("Create new album");
				dialog.setContentText("Album name...");
				Optional<String> result = dialog.showAndWait();
				if (result.isPresent()) {
					Album a = contentProvider.createAlbum(result.get());
					if (a != null) {
						ObservableList<Node> c = pane.getChildren();
						c.add(c.size() - 2, new AlbumView(a));
					}
				}
			});
		}
	}

	static class GalleryIcon extends javafx.scene.control.Label {
		ImageView view;

		GalleryIcon(Image img) {
			super.getStyleClass().add("icon");
			view = new ImageView(img);
			view.setFitWidth(THUMBNAIL_WIDTH);
			view.setFitHeight(THUMBNAIL_WIDTH);
			view.setPreserveRatio(true);
			super.setGraphic(view);

			super.setContentDisplay(ContentDisplay.TOP);
			super.setTextAlignment(TextAlignment.CENTER);
		}
	}

	class AlbumView extends GalleryIcon {

		final Album album;

		AlbumView(Album album) {
			super(albumIcon);
			this.album = album;
			super.setText(album.getName());
			super.getStyleClass().add("album_icon");

			this.setOnMouseClicked(ev -> {
				if (ev.getClickCount() == 2 && ev.getButton() == MouseButton.PRIMARY)
					showAlbum(primaryStage);
			});

			this.setOnDragDetected((ev) -> {
				Dragboard db = this.startDragAndDrop(TransferMode.ANY);
				ClipboardContent content = new ClipboardContent();
				content.putImage(super.view.getImage());
				db.setContent(content);
				ev.consume();
			});

			this.setOnDragDone((ev) -> {
				System.err.println(ev.getTransferMode() + "/" + ev.getAcceptingObject());
				if (ev.getAcceptedTransferMode() == TransferMode.LINK) {
					contentProvider.deleteAlbum(album);
					createAlbums(primaryStage);
					showAlbums();
				}
			});
		}

		void showAlbum(Stage stage) {
			ScrollPane root = new ScrollPane();
			Scene scene = new Scene(root);
			scene.getStylesheets().add(getClass().getResource("gallery.css").toExternalForm());

			TilePane tile = new TilePane();
			tile.getStyleClass().add("album_pictures");

			tile.setPadding(new Insets(PADDING));
			tile.setHgap(PADDING);
			tile.setVgap(PADDING);

			root.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
			root.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

			root.setFitToWidth(true);
			root.setFitToHeight(true);
			root.setContent(tile);

			stage.setTitle(album.getName());

			tile.getChildren().add(new TrashIcon());
			contentProvider.getListOfPictures(album).forEach(picture -> {
				final PictureView pv = new PictureView(album, picture);
				tile.getChildren().add(pv);
			});

			tile.setOnDragOver((e) -> {
				e.acceptTransferModes(TransferMode.MOVE);
				e.consume();
			});

			tile.setOnDragDropped((e) -> {
				Dragboard db = e.getDragboard();
				if (db.hasFiles()) {
					db.getFiles().forEach(f -> {
						if (isPicture(f)) {
							try {
								byte[] data = Files.readAllBytes(f.toPath());
								Picture pic = contentProvider.uploadPicture(album, f.getName(), data);
								if (pic != null) {
									tile.getChildren().add(new PictureView(album, pic));
								}

							} catch (Exception x) {
							}
						}
					});
				}
				e.setDropCompleted(true);
				e.consume();
			});

			stage.setScene(scene);
			stage.show();
			stage.setOnCloseRequest(event -> {
				event.consume();
				showAlbums();
			});
			currentAlbum = this;
		}
	}

	class PictureView extends GalleryIcon {
		final Album album;
		final Picture picture;

		PictureView(Album album, Picture picture) {
			super(loadingIcon);
			super.getStyleClass().add("picture_icon");

			this.album = album;
			this.picture = picture;
			super.setText(picture.getName());
			executor.execute(() -> load());
		}

		void load() {
			Platform.runLater(() -> {
				super.view.setImage(getImage(THUMBNAIL_WIDTH));
				setOnMouseClicked(ev -> {
					if (ev.getClickCount() == 2)
						showFullView();
				});

				this.setOnDragDetected((ev) -> {
					Dragboard db = this.startDragAndDrop(TransferMode.ANY);
					ClipboardContent content = new ClipboardContent();
					content.putImage(super.view.getImage());
					db.setContent(content);
					ev.consume();
				});

				this.setOnDragDone((ev) -> {
					if (ev.getAcceptedTransferMode() == TransferMode.LINK) {
						if (contentProvider.deletePicture(album, picture))
							((TilePane) getParent()).getChildren().remove(PictureView.this);
					}
				});
			});
		}

		void showFullView() {
			BorderPane borderPane = new BorderPane();
			ImageView imageView = new ImageView();
			imageView.setImage(getImage(primaryStage.getWidth() - PADDING));
			imageView.setStyle("-fx-background-color: BLACK");
			imageView.setFitHeight(primaryStage.getHeight() - 10);
			imageView.setPreserveRatio(true);
			imageView.setSmooth(true);
			imageView.setCache(true);
			borderPane.setCenter(imageView);
			borderPane.setStyle("-fx-background-color: BLACK");

			Stage newStage = new Stage();
			newStage.setWidth(primaryStage.getWidth());
			newStage.setHeight(primaryStage.getHeight());
			newStage.setTitle(picture.getName());
			Scene scene = new Scene(borderPane, Color.BLACK);
			newStage.setScene(scene);
			newStage.show();
			newStage.setOnCloseRequest(event -> {
				event.consume();
				newStage.close();
			});
		}

		private Image getImage(double width) {
			byte[] data = contentProvider.getPictureData(album, picture);
			if (data != null) {
				ByteArrayInputStream bais = new ByteArrayInputStream(data);
				Image img = new Image(bais, width, 0, true, true);
				return img;
			}
			return null;
		}
	}

	static boolean isPicture(File f) {
		String filename = f.getName();
		int i = filename.lastIndexOf('.');
		String ext = i < 0 ? "" : filename.substring(i + 1).toLowerCase();
		return f.isFile() && !filename.startsWith(".") && EXTENSIONS.contains(ext);
	}

	static final List<String> EXTENSIONS = Arrays.asList(new String[] { "tiff", "gif", "jpg", "jpeg", "png" });

	static Image newIcon = new Image(GalleryWindow.class.getResourceAsStream("new.png"));
	static Image loadingIcon = new Image(GalleryWindow.class.getResourceAsStream("loading.gif"));
	static Image albumIcon = new Image(GalleryWindow.class.getResourceAsStream("album.png"));
	static Image trashIcon = new Image(GalleryWindow.class.getResourceAsStream("trash.png"));

}