package sd.tp1;

import java.util.HashMap;

public class PictureCacheClass {
	public static final int DEFAULT_SIZE = 100;
	private HashMap<String, PictureForCacheClass> cache;
	private int size;
	private long delta;
	
	/**
	 * @param size
	 */
	public PictureCacheClass(int size, long delta){
		cache = new HashMap<String, PictureForCacheClass>(size);
		this.size = size;
		this.delta = delta;
	}
	
	/**
	 * 
	 */
	public PictureCacheClass(){
		this(DEFAULT_SIZE, 30000);
	}
	
	/**
	 * @param path
	 * @param picture
	 */
	public void put(String path, byte[] picture){
		if(size <= cache.size() )
			this.clear();
		if(cache.containsKey(path))
			cache.remove(path);
		cache.put(path, new PictureForCacheClass(picture));
	}
	
	/**
	 * @param path
	 * @return the data in the cache (null if doesn't exist)
	 */
	public byte[] get(String path){
		PictureForCacheClass pic = cache.get(path);
		if(pic != null && (pic.getTimestamp()+this.delta)>System.currentTimeMillis())
			return pic.getPicture();
		else return null;
	}
	
	/**
	 * clear the cache
	 */
	public void clear(){
		cache.clear();
	}
}
