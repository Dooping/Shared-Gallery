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
	
	public PictureClass(String name, String server){
		this.name = name;
		this.lamportClock = new LamportClock(server, 1);
	}

	public LamportClock getLamportClock() {
		return lamportClock;
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
	}	
}
