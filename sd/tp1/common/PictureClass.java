/**
 * 
 */
package sd.tp1.common;

import java.io.Serializable;


/**
 * @author Moncada
 *
 */
public class PictureClass implements Serializable{

	private static final long serialVersionUID = 0L;
	public String name;
	public boolean erased;
	public LamportClock lamportClock;
	public String server;
	public long datetime;
	public long picSize;

	public PictureClass(String name, String server){
		this.name = name;
		this.server = server;
		this.lamportClock = new LamportClock(server, 1);
	}
	
	public PictureClass(){}
	
	public boolean isErased() {
		return erased;
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

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PictureClass other = (PictureClass) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}
	
	@Override
	public String toString() {
		return "PictureClass [name=" + name + "]";
	}
	
}
