package sd.tp1.exeptions;

public class AlbumNotFoundException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public AlbumNotFoundException(String message) {
		super(message);
	}
}
