package sd.tp1;

public class PictureForCacheClass {
	private byte[] picture;
	private long timestamp;
	
	public PictureForCacheClass(byte[] picture) {
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
