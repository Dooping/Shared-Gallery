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
	
	public PictureClass(String name, String server){
		this.name = name;
		this.server = server;
		this.lamportClock = new LamportClock(server, 1);
	}
	
	public PictureClass(){}

//	public LamportClock getLamportClock() {
//		return lamportClock;
//	}
	
	public String getServer(){
		return server;
	}

	public void setLamportClock(LamportClock lamportClock) {
		this.lamportClock = lamportClock;
	}

	public String getName() {
		return name;
	}
	
	public boolean isErased() {
		return erased;
	}
	
	public void erase(){
		erased = true;
		lamportClock.setLamportNumber(lamportClock.lamportNumber+1);
	}
	
	public void recreate(){
		erased = false;
		lamportClock.setLamportNumber(lamportClock.lamportNumber+1);
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
	
	
}
