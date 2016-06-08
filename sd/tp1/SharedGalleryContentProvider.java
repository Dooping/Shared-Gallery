package sd.tp1;

import java.io.IOException;
import java.net.MulticastSocket;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;

import sd.tp1.common.AlbumFolderClass;
import sd.tp1.common.MulticastDiscovery;
import sd.tp1.common.PictureClass;
import sd.tp1.common.UtilsClass;
import sd.tp1.gui.GalleryContentProvider;
import sd.tp1.gui.Gui;

/*
 * This class provides the album/picture content to the gui/main application.
 * 
 * Project 1 implementation should complete this class. 
 */
public class SharedGalleryContentProvider implements GalleryContentProvider{

	public static final int DISCOVERY_INTERVAL = 1000;
	public static final int TIMEOUT_CYCLES = 5;

	Gui gui;
	private MulticastDiscovery discovery;
	public MulticastSocket socket;
	private List<ServerObjectClass> servers;
	private PictureCacheClass cache;
	private Random random;
	private KafkaProducer<String, String> producer;


	SharedGalleryContentProvider(String messageServerHost) {
		servers = Collections.synchronizedList(new CopyOnWriteArrayList<ServerObjectClass>());
		random = new Random(System.currentTimeMillis());

		cache = new PictureCacheClass();
		discovery = new MulticastDiscovery();
		try {
			socket = new MulticastSocket();
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.sendRequests();
		this.registServer();
		this.kafkaSubscriber(messageServerHost);
		this.kafKaPublisher(messageServerHost);


	}


	/**
	 *  Downcall from the GUI to register itself, so that it can be updated via upcalls.
	 */
	@Override
	public void register(Gui gui) {
		if( this.gui == null ) {
			this.gui = gui;
		}
	}

	/**
	 * Returns the list of albums in the system.
	 * On error this method should return null.
	 */
	@Override
	public List<Album> getListOfAlbums() {
		Map<String,AlbumFolderClass> albums = new HashMap<>();
		List<Album> toReturn = new ArrayList<Album>();
		if (servers != null){
			Iterator<ServerObjectClass> serverIt = servers.iterator();
			while (serverIt.hasNext()){
				ServerObjectClass server = serverIt.next();
				if (server != null){
					try{
						List<AlbumFolderClass> al = server.getServer().getAlbums();
						List<AlbumFolderClass> toAdd = new LinkedList<>();
						if(al != null){
							for(AlbumFolderClass album : al){
								toAdd.add(album);
								AlbumFolderClass a = albums.get(album.name);
								if((a != null && album.lamportClock.compareTo(a.lamportClock)>0) || a == null)
									albums.put(album.name, album);
							}
							server.addListAlbuns(toAdd);
						}
					}catch (Exception e ){
						return null;
					}
				}
			}
			Iterator<String> it = albums.keySet().iterator();
			while(it.hasNext()){
				AlbumFolderClass a = albums.get(it.next());
				if(!a.isErased())
					toReturn.add(new SharedAlbum(a.name));
			}
		}
		else return null;


		return toReturn;
	}

	/**
	 * Returns the list of pictures for the given album. 
	 * On error this method should return null.
	 */
	@Override
	public List<Picture> getListOfPictures(Album album) {
		List<Picture> toReturn = new ArrayList<Picture>();
		List<ServerObjectClass> servers = this.findServer(album.getName());
		Map<String,PictureClass> pictures = new HashMap<>();
		if(!servers.isEmpty()){
			for(ServerObjectClass s : servers){
				List<PictureClass> serverPictures = s.getServer().getPictures(album.getName());
				if(serverPictures != null){
					for(PictureClass picture : serverPictures){
						//if(!picture.isErased()){
							PictureClass p = pictures.get(picture.name);
							if((p != null && picture.lamportClock.compareTo(p.lamportClock)>0) || p == null)
								pictures.put(picture.name, picture);
						}
				}
			}
			Iterator<String> it = pictures.keySet().iterator();
			while(it.hasNext()){
				PictureClass p = pictures.get(it.next());
				if(!p.isErased())
					toReturn.add(new SharedPicture(p.name));
			}
			return toReturn;
		}
		else
			return null;
	}

	/**
	 * Returns the contents of picture in album.
	 * On error this method should return null.
	 */
	@Override
	public byte[] getPictureData(Album album, Picture picture) {	
		List<ServerObjectClass> servers = this.findServer(album.getName());
		ServerObjectClass s = servers.get(random.nextInt(servers.size()));
		byte[] pic = cache.get(album.getName()+"/"+picture.getName());
		if(s!= null && pic == null){
			RequestInterface i = s.getServer();
			pic = i.getPicture(album.getName(), picture.getName());
			if(pic != null)
				cache.put(album.getName()+"/"+picture.getName(), pic);
		}
		return pic;
	}

	/**
	 * Create a new album.
	 * On error this method should return null.
	 */
	@Override
	public Album createAlbum(String name) {
		List<ServerObjectClass> servers = this.findServer(name);
		if(servers.isEmpty()){
			ServerObjectClass server = this.servers.get(UtilsClass.getNextServerIndex(this.servers, name));
			boolean c = server.getServer().createAlbum(name);
			if(c){
				//System.out.println("New album");
				server.addAlbum(new AlbumFolderClass(name, server.getServerName()));
				ProducerRecord<String, String> data = new ProducerRecord<>("albumCreated" , name);
				producer.send(data);
				return new SharedAlbum(name);
			}
			else return null;
		}
		else
			return null;
	}

	/**
	 * Delete an existing album.
	 */
	@Override
	public void deleteAlbum(Album album) {
		List<ServerObjectClass> servers = this.findServer(album.getName());
		ServerObjectClass s = servers.get(random.nextInt(servers.size()));
		if(s!= null){
			if(s.getServer().deleteAlbum(album.getName())){
				//System.out.println("Deleting");
				s.deleteAlbum(album.getName());
				ProducerRecord<String, String> data = new ProducerRecord<>("albumDeleted" , album.getName());
				producer.send(data);
			}

		}

	}

	/**
	 * Add a new picture to an album.
	 * On error this method should return null.
	 */
	@Override
	public Picture uploadPicture(Album album, String name, byte[] data) {
		List<ServerObjectClass> servers = this.findServer(album.getName());
		ServerObjectClass s = servers.get(random.nextInt(servers.size()));
		if(s!= null){
			//System.out.println("Sending new pic");
			s.getServer().uploadPicture(album.getName(), name, data);
			ProducerRecord<String, String> kafka = new ProducerRecord<>("pictureCreated" , album.getName());
			producer.send(kafka);
			return new SharedPicture(name);
		}
		else
			return null;
	}

	/**
	 * Delete a picture from an album.
	 * On error this method should return false.
	 */
	@Override
	public boolean deletePicture(Album album, Picture picture) {
		List<ServerObjectClass> servers = this.findServer(album.getName());
		ServerObjectClass s = servers.get(random.nextInt(servers.size()));
		if(s!= null){
			s.getServer().deletePicture(album.getName(), picture.getName());
			ProducerRecord<String, String> data = new ProducerRecord<>("pictureDeleted" , album.getName());
			producer.send(data);
			return true;
		}
		else
			return false;
	}

	/**
	 * @param album
	 * @return the server of the album, or null
	 */
	private List<ServerObjectClass> findServer(String album){
		List<ServerObjectClass> list = new LinkedList<>();
		try{
			for (ServerObjectClass server: servers){
				if (server.containsAlbuns(album)){
					list.add(server);
				}
			}
		}catch (Exception e){
			e.printStackTrace();
		}
		return list;
	}

	/**
	 * Represents a shared album.
	 */
	static class SharedAlbum implements GalleryContentProvider.Album {
		final String name;

		SharedAlbum(String name) {
			this.name = name;
		}

		@Override
		public String getName() {
			return name;
		}
	}

	/**
	 * Represents a shared picture.
	 */
	static class SharedPicture implements GalleryContentProvider.Picture {
		final String name;

		SharedPicture(String name) {
			this.name = name;
		}
		
		@Override
		public String getName() {
			return name;
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
				}
			}catch(Exception e){
				e.printStackTrace();
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
								if(!s.isConnected()){
									s.setConnected(true);
									System.out.println("Adding server: " + serviceURI.toString() );
									gui.updateAlbums();
								}
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
							if(gui!=null)
								gui.updateAlbums();
						}
					}
				}
			}catch(Exception e){
				e.printStackTrace();
			};
		}).start();
	}

	private void kafkaSubscriber (String messageServerHost){
		new Thread(() -> {
			Properties props = new Properties();
			props.put("bootstrap.servers", messageServerHost+":9092");
			props.put("group.id", "consumer-tutorial" + System.nanoTime());
			props.put("key.deserializer", StringDeserializer.class.getName());
			props.put("value.deserializer", StringDeserializer.class.getName());
			KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);
			
			
			consumer.subscribe(Arrays.asList("albumCreated", "albumDeleted", "pictureCreated", "pictureDeleted")); 

			try {
				  for(;;) {
				    ConsumerRecords<String, String> records = consumer.poll(1000);
				    records.forEach( r -> {			    	
				    	System.err.println( r.topic() + "/" + r.value());
				    	if(r.topic().equals("albumCreated") || r.topic().equals("albumDeleted"))
				    		gui.updateAlbums();
				    	else if(r.topic().equals("pictureCreated") || r.topic().equals("pictureDeleted"))
				    		gui.updateAlbum(new SharedAlbum(r.value()));
				    });
				  }
				} finally {
				  consumer.close();
			}
		}).start();
	}

	private void kafKaPublisher(String messageServerHost){
		Properties env = System.getProperties();
		Properties props = new Properties();
		
		props.put("zk.connect", env.getOrDefault("zk.connect", messageServerHost+":2181/"));
		props.put("bootstrap.servers", env.getOrDefault("bootstrap.servers", messageServerHost+":9092"));
		props.put("log.retention.ms", 1000);

		props.put("serializer.class", "kafka.serializer.StringEncoder");
		props.put("key.serializer", StringSerializer.class.getName());
		props.put("value.serializer", StringSerializer.class.getName());

		//System.err.println(props);

		producer = new KafkaProducer<>(props);

//		Random rnd = new Random();
//		try {
//			for(;;) {
//				
//				String topic = "topic" + rnd.nextInt(5);
//				String event = "" + System.nanoTime();
//				
//				ProducerRecord<String, String> data = new ProducerRecord<>(topic , event);
//				producer.send(data);
//				Thread.sleep(1000);
//			}
//		} catch (Exception x) {
//			x.printStackTrace();
//		}
//		producer.close();
	}

}
