package sd.tp1;

public class PictureClass {
	private byte[] picture;
	private long timestamp;
	
	public PictureClass(byte[] picture) {
		this.picture = picture;
		this.timestamp = System.currentTimeMillis();
	}
	public byte[] getPicture() {
		return picture;
	}
	public long getTimestamp() {
		return timestamp;
	}
}
