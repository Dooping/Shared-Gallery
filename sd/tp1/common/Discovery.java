/**
 * 
 */
package sd.tp1.common;

import java.net.URL;

/**
 * @author Moncada
 *
 */
public interface Discovery {
	
	URL findService( String name);
	
	void registerService (URL url, int servicePort);
	

}
