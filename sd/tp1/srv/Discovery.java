/**
 * 
 */
package sd.tp1.srv;

import java.net.URL;

/**
 * @author Moncada
 *
 */
public interface Discovery {
	
	URL findService( String name);
	
	void registerService (URL url);
	

}
