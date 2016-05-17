package sd.tp1.common;

import java.util.LinkedList;
import java.util.List;

import sd.tp1.ServerObjectClass;

public class AlbumClass {
	private String name;
	private List<ServerObjectClass> servers;
	
	public AlbumClass(String name, ServerObjectClass s) {
		this.name = name;
		servers = new LinkedList<>();
		servers.add(s);
	}

	public String getName() {
		return name;
	}

	public List<ServerObjectClass> getServers() {
		return servers;
	}
	
	public void addServer(ServerObjectClass s){
		if(!servers.contains(s))
			servers.add(s);
	}

	@Override
	public String toString() {
		return "AlbumClass [name=" + name + ", servers=" + servers + "]";
	}
}
