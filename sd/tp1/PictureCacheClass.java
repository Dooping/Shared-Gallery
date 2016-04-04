package sd.tp1;

import java.util.HashMap;

public class PictureCacheClass {
	public static final int DEFAULT_SIZE = 100;
	private HashMap<String, byte[]> cache;
	
	public PictureCacheClass(int size){
		cache = new HashMap<String, byte[]>(size);
	}
	
	public PictureCacheClass(){
		this(DEFAULT_SIZE);
	}
	
	public void put(String path, byte[] picture){
		if(cache.containsKey(path))
			cache.remove(path);
		cache.put(path, picture);
	}
	
	public byte[] get(String path){
		return cache.get(path);
	}
	
	public void clear(){
		cache.clear();
	}
}
