package sd.tp1.common;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

public class AlbumFolderClass implements Serializable{
	private static final long serialVersionUID = 0L;
	public String name;
	public LamportClock lamportClock;
	public boolean erased;
	
	public AlbumFolderClass(String name, String serverUrl) {
		this.name = name;
		this.lamportClock = new LamportClock(serverUrl, 1);
	}
	public String getName() {
		return name;
	}
	
	public void erase(){
		erased = true;
	}
	
	public boolean isErased(){
		return erased;
	}

}
