package sd.tp1.common;

import java.util.List;
import java.util.Random;

import sd.tp1.ServerObjectClass;

public final class UtilsClass {
	private UtilsClass(){}
	
	public static int getNextServerIndex(List<ServerObjectClass> servers, String albumName){
		Random generator = new Random(albumName.hashCode());
		int n = generator.nextInt(servers.size());
		int antiLoop = n;
		while(!servers.get(n).isConnected() || servers.get(n).containsAlbuns(albumName)){
			n = (n+1)%servers.size();
			if(n == antiLoop)
				break;
		}
		return n;
	}
}
