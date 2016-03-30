package sd.tp1.srvREST;

public class Message {

	public String author;
	public String text;
	
	public Message() {}
	
	public Message(String author, String text ) {
		this.author = author;
		this.text = text;
	}
	
	public String toString() {
		return String.format("author: %s, text: %s", author, text);
	}
}
