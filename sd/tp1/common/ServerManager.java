package sd.tp1.common;

import java.io.File;
import java.io.IOException;
import java.net.MulticastSocket;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import sd.tp1.RESTClientClass;
import sd.tp1.RequestInterface;
import sd.tp1.SOAPClientClass;
import sd.tp1.ServerObjectClass;
import sd.tp1.common.UtilsClass;

public class ServerManager {
	public static final int DISCOVERY_INTERVAL = 1000;
	public static final int REPLICATION_INTERVAL = 10000;
	public static final int REPLICATION_DELAY = 30000;
	public static final int TIMEOUT_CYCLES = 5;
	public static final int NUMBER_OF_REPLICS = 2;
	
	private MulticastDiscovery discovery;
	public MulticastSocket socket;
	private List<ServerObjectClass> servers;
	
	public ServerManager() {
		servers = Collections.synchronizedList(new LinkedList<ServerObjectClass>());
		
		discovery = new MulticastDiscovery();
		try {
			socket = new MulticastSocket();
		} catch (IOException e) {
			//e.printStackTrace();
		}
		this.sendRequests();
		this.registServer();
		this.albumReplicationThread();
		
		try {
		} catch (Exception e) {
			//e.printStackTrace();
		}
		
		
	}
	
	/**
	 * to send the requests to the network
	 */
	private void sendRequests(){
		new Thread(() -> {
			try {
				while (true){
					Iterator<ServerObjectClass> i = servers.iterator();
					while(i.hasNext()){
						ServerObjectClass s = i.next();
						
						if(s.getCounter() == TIMEOUT_CYCLES && s.isConnected()){
							System.out.println("Removing server: " + s.getServerName());
							s.setConnected(false);
						}
						else if (s.isConnected())
							s.incrementCounter();
					}
					discovery.findService(socket);
					Thread.sleep(DISCOVERY_INTERVAL);
					//System.out.println(servers.toString());
				}
			}catch(Exception e){
			};
		}).start();
	}


	/**
	 * to catch the servers 
	 */
	private void registServer (){
		String SERVER_SOAP = "GalleryServerSOAP";
		String SERVER_REST = "GalleryServerREST";
		String IMGUR_REST = "GalleryServerImgur";
		new Thread(() -> {
			try {
				while (true){
					URI serviceURI = discovery.getService(socket);
					if(serviceURI!=null){
						String [] compare = serviceURI.toString().split("/");
						RequestInterface sv = null;
						
						boolean exits = false;
						for (ServerObjectClass s: servers){
							if (s.equals(serviceURI.toString())){
								exits = true;
								s.resetCounter();
								s.setConnected(true);
								break;
							}
						}
						if (!exits){
							if(compare[3].equalsIgnoreCase(SERVER_SOAP)){
								sv = new SOAPClientClass(serviceURI);

							}
							else if(compare[3].equalsIgnoreCase(SERVER_REST)|| compare[3].equalsIgnoreCase(IMGUR_REST)){
								sv = new RESTClientClass(serviceURI);
							}
							System.out.println("Adding server: " + serviceURI.toString() );
							ServerObjectClass obj = new ServerObjectClass(sv, serviceURI.toString());
							servers.add(obj);
							Collections.sort(servers, new Comparator<ServerObjectClass>(){
								   @Override
								   public int compare(ServerObjectClass o1, ServerObjectClass o2){
									   return o1.getServerName().compareTo(o2.getServerName());
								   }
								}); 
						}
					}
				}
			}catch(Exception e){
				//e.printStackTrace();
			};
		}).start();
	}
	
	private void albumReplicationThread(){
		new Thread(() -> {
			try {
				Thread.sleep(REPLICATION_DELAY);
				while (true){
					Map<String,AlbumClass> albums = new HashMap<>();
					for(ServerObjectClass s : servers){
						try{
							List<String> as = s.getServer().getAlbums();
							s.addListAlbuns(as);
							for(String albumName : as){
								AlbumClass a = albums.get(albumName);
								if(a != null)
									a.addServer(s);
								else
									albums.put(albumName, new AlbumClass(albumName, s));
							}
						} catch(Exception e){}
					}
					ArrayList<String> names = new ArrayList<String>(Arrays.asList(new File("./gallery").list()));
					for(String name : names){
						AlbumClass album = albums.get(name);
						if(album.getServers().size()<NUMBER_OF_REPLICS && servers.size() >= NUMBER_OF_REPLICS){
							replicateAlbumToServer(servers.get(UtilsClass.getNextServerIndex(servers, name)), name);
						}
					}
					Thread.sleep(REPLICATION_INTERVAL);
				}
			}catch(Exception e){
			};
		}).start();
	}
	
	private void replicateAlbumToServer(ServerObjectClass s, String album){
		try{
			RequestInterface server = s.getServer();
			//System.out.println(s);
			boolean response = server.createAlbum(album);
			if (response){
				File albumFolder = new File("./gallery/"+album);
				ArrayList<String> names = new ArrayList<String>(Arrays.asList(albumFolder.list()));
				for(String pic : names){
					server.uploadPicture(album, pic, Files.readAllBytes(Paths.get("./gallery"+"/"+album + "/"+ pic)));
				}
				//System.out.println(s);
			}
			//else
				//System.out.println("Not Replicated");
		} catch(Exception e){}
		
	}
}


