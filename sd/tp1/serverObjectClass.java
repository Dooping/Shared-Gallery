/**
 * 
 */
package sd.tp1;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import sd.tp1.common.AlbumFolderClass;


/**
 * @author Moncada
 *
 */
public class ServerObjectClass {
	
	private RequestInterface server;
	private Map <String, AlbumFolderClass> listAlbuns;
	private int counter;
	private String serverName;
	private boolean connected;

	
	public ServerObjectClass (RequestInterface sv, String serverName){
		this.server = sv;
		this.counter = 0;
		this.serverName = serverName;
		listAlbuns = new HashMap<String, AlbumFolderClass>();
		this.connected = true;
	}

	
	public boolean isConnected() {
		return connected;
	}


	public void setConnected(boolean connected) {
		this.connected = connected;
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
	public void addListAlbuns(List<AlbumFolderClass> listAlbuns){
		this.listAlbuns = new HashMap<String, AlbumFolderClass>();
		for(AlbumFolderClass a: listAlbuns){
			this.listAlbuns.put(a.getName(), a);
		}
	}
	
	/**
	 * @param album
	 * add an album
	 */
	public void addAlbum(AlbumFolderClass album){
		if(listAlbuns.containsKey(album)){
			listAlbuns.put(album.getName(), album);
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
	public Map<String, AlbumFolderClass> getListAlbuns() {
		return listAlbuns;
	}

	/**
	 * @return the counter
	 */
	public int getCounter() {
		return counter;
	}


	@Override
	public String toString() {
		return "ServerObjectClass [serverName=" + serverName + "]";
	}
	
	
}
