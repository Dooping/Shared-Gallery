/**
 * 
 */
package sd.tp1;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @author Moncada
 *
 */
public class serverObjectClass {
	
	private RequestInterface server;
	private Map <String, String> listAlbuns;
	private int counter;
	private String serverName;

	
	public serverObjectClass (RequestInterface sv, String serverName){
		this.server = sv;
		this.counter = 0;
		this.serverName = serverName;
		listAlbuns = new HashMap<String, String>();

	}

	
	/**
	 * @return
	 */
	public String getServerName() {
		return serverName;
	}
	
	@Override
	public boolean equals(Object obj) {
		//System.out.println(serverName.equals(obj));
		if (obj instanceof String)
			return serverName.equals(obj);
		return false;
	}

	/**
	 * @param listAlbuns
	 * to add list of albuns
	 */
	public void addListAlbuns(List <String> listAlbuns){
		for(String a: listAlbuns){
			this.listAlbuns.put(a, a);
		}
	}
	
	/**
	 * @param album
	 * add an album
	 */
	public void addAlbum(String album){
		if(listAlbuns.containsKey(album)){
			listAlbuns.put(album, album);
		}
	}
	
	public void deleteAlbum(String album){
		if(listAlbuns.containsKey(album)){
			listAlbuns.remove(album);
		}
	}
	
	/**
	 * @param name
	 * @return true if this server contais the album
	 */
	public boolean containsAlbuns(String name){
		return listAlbuns.containsKey(name);
	}
	
	/**
	 * reset the counter to zero
	 */
	public void resetCounter(){
		this.counter=0;
	}
	
	/**
	 * increment counter
	 */
	public void incrementCounter(){
		this.counter++;
	}
	
	/**
	 * @return the server
	 */
	public RequestInterface getServer() {
		return server;
	}

	/**
	 * @return the list of albuns
	 */
	public Map<String, String> getListAlbuns() {
		return listAlbuns;
	}

	/**
	 * @return the counter
	 */
	public int getCounter() {
		return counter;
	}
	
	
}
