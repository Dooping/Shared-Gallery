/**
 * 
 */
package sd.tp1.srv;

/**
 * @author Moncada
 *
 */
public class PictureClass {
	
	public String name;
	public byte [] contents;
	
	public PictureClass(String name, byte [] c){
		this.name = name;
		this.contents = c;
	}
	
	public String toString() {
		return String.format("name: %s, contents: %s", name, contents);
	}

}
