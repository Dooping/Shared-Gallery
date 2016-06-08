package sd.tp1.common;

import java.io.Serializable;


public class AlbumFolderClass implements Serializable{
	private static final long serialVersionUID = 0L;
	public String name;
	public LamportClock lamportClock;
	public boolean erased;
	public String serverUrl;
	
	public AlbumFolderClass(String name, String serverUrl) {
		this.name = name;
		this.serverUrl  = serverUrl;
		this.lamportClock = new LamportClock(serverUrl, 1);
	}

	public AlbumFolderClass() {
		this.lamportClock = new LamportClock();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AlbumFolderClass other = (AlbumFolderClass) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}
	
	public void erase(String serverUrl){
		erased = true;
		lamportClock.setLamportNumber(lamportClock.lamportNumber+1);
		lamportClock.setServerUrl(serverUrl);
	}
	
	public void recreate(String serverUrl){
		erased = false;
		lamportClock.setLamportNumber(lamportClock.lamportNumber+1);
		lamportClock.setServerUrl(serverUrl);
	}
	
	public boolean isErased(){
		return erased;
	}

	@Override
	public String toString() {
		return "AlbumFolderClass [name=" + name + ", erased=" + erased + "]";
	}
	
	
}
