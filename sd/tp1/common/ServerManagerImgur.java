package sd.tp1.common;

import sd.tp1.ServerObjectClass;

public class ServerManagerImgur extends ServerManager {
	
	public ServerManagerImgur() {
		super();
	}
	
	public void sendRequests(){
		super.sendRequests();
	}
	
	protected void registServer (){
		super.registServer();
	}
	
	protected void albumReplicationThread(){
		super.albumReplicationThread();
	}
	
	protected void replicateAlbumToServer(ServerObjectClass s, String album){
		super.replicateAlbumToServer(s,album);
	}
	
	protected void albumSynchronizationThread(){
		super.albumSynchronizationThread();
	}
	
	protected void synchronizationAlbum(ServerObjectClass s){
		
	}
	
	protected void synchronizationPictures(ServerObjectClass s, String album){
		
	}
	
	protected void garbageCollector(){
		
	}

}
