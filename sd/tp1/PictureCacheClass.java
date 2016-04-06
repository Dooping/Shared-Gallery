package sd.tp1;

import java.util.HashMap;

public class PictureCacheClass {
	public static final int DEFAULT_SIZE = 100;
	private HashMap<String, byte[]> cache;
	
	/**
	 * @param size
	 */
	public PictureCacheClass(int size){
		cache = new HashMap<String, byte[]>(size);
	}
	
	/**
	 * 
	 */
	public PictureCacheClass(){
		this(DEFAULT_SIZE);
	}
	
	/**
	 * @param path
	 * @param picture
	 */
	public void put(String path, byte[] picture){
		if(cache.containsKey(path))
			cache.remove(path);
		cache.put(path, picture);
	}
	
	/**
	 * @param path
	 * @return the data in the cache (null if doesn't exist)
	 */
	public byte[] get(String path){
		return cache.get(path);
	}
	
	/**
	 * clear the cache
	 */
	public void clear(){
		cache.clear();
	}
}
