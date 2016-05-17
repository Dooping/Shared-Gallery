/**
 * 
 */
package sd.tp1.common;


/**
 * @author Moncada
 *
 */
public class PictureClass {
	
	public String name;
	public long timestamp;
	public String server;
	public byte [] contents;
	
	public PictureClass(String name, byte [] c, long timestamp, String server){
		this.name = name;
		this.contents = c;
		this.timestamp = timestamp;
		this.server = server;
	}

}
