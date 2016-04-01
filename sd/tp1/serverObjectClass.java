/**
 * 
 */
package sd.tp1;

import java.util.List;
import java.util.Map;

import sd.tp1.gui.GalleryContentProvider.Album;

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
	
	}
	
	/**
	 * @return
	 */
	public String getServerName() {
		return serverName;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof String)
			return serverName.equals(obj);
		return false;
	}

	public void addListAlbuns(List <Album> listAlbuns){
		for(Album a: listAlbuns){
			this.listAlbuns.put(a.getName(), a.getName());
		}
	}
	

	/**
	 * @param the album name
	 * @return the server in case the album belongs to this server, null if not
	 */
	public RequestInterface serverOfAlbun(String album) {
		return (listAlbuns.containsKey(album)) ? server : null;
	}
	
	/**
	 * @param album
	 * add an album
	 */
	public void addAlbum(Album album){
		if(listAlbuns.containsKey(album.getName())){
			listAlbuns.put(album.getName(), album.getName());
		}
		
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
