package sd.tp1.srvREST;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

@Path("/messages")
public class MessageBoardResource {

	static List<Message> board = null;

	@GET
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	public Response getMessages() {
		System.err.printf("getMessages()\n");
		if (board == null)
			return Response.status(Status.NOT_FOUND).build();
		else
			return Response.ok(board.toString().getBytes()).build();
	}

	@GET
	@Path("/at/{index}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getMessageAt(@PathParam("index") int index) {
		System.err.printf("getMessageAt( index: %d) board: %s\n", index, board);
		if (board == null || board.size() <= index)
			return Response.status(Status.NOT_FOUND).build();
		else
			return Response.ok(board.get(index)).build();
	}

	@POST
	@Path("/new")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response create(Message msg) {
		System.err.printf("create(author: %s text: %s)\n", msg.author, msg.text);
		if (board != null)
			return Response.status(422).build();
		else {
			board = new ArrayList<>();
			board.add(msg);
			return Response.ok().build();
		}
	}

	@PUT
	@Path("add/{index}")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response putMessageAt(@PathParam("index") int index, Message msg) {
		System.err.printf("putMsg(index: %s, author: %s text: %s)\n", index, msg.author, msg.text);
		if (board == null)
			return Response.status(Status.NOT_FOUND).build();
		else {
			if (board.size() <= index)
				board.add(msg);
			else
				board.set(index, msg);

			return Response.ok().build();
		}
	}

	@DELETE
	public Response delete() {
		System.err.printf("delete()\n");
		if (board == null)
			return Response.status(Status.NOT_FOUND).build();
		else {
			board = null;
			return Response.ok().build();
		}
	}

}
