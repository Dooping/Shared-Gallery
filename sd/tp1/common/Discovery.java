/**
 * 
 */
package sd.tp1.common;

import java.net.MulticastSocket;
import java.net.URL;

/**
 * @author Moncada
 *
 */
public interface Discovery {
	
	void findService(MulticastSocket socket);
	
	URL getService(MulticastSocket socket);
	
	void registerService (URL url);
	

}
