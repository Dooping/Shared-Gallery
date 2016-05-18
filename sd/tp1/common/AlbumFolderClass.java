package sd.tp1.common;

import java.io.Serializable;

public class AlbumFolderClass implements Serializable{
	private static final long serialVersionUID = 0L;
	public String name;
	public LamportClock lamportClock;
	public boolean erased;
	
	public AlbumFolderClass(String name, String serverUrl) {
		this.name = name;
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

	public String getName() {
		return name;
	}
	
	public void erase(){
		erased = true;
		lamportClock.setLamportNumber(lamportClock.getLamportNumber()+1);
	}
	
	public void recreate(){
		erased = false;
		lamportClock.setLamportNumber(lamportClock.getLamportNumber()+1);
	}
	
	public boolean isErased(){
		return erased;
	}
	public LamportClock getLamportClock() {
		return lamportClock;
	}
}
