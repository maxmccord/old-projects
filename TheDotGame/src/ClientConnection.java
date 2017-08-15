import java.io.*;
import java.net.*;

public class ClientConnection extends DotGameConnection {
	// private variables
	private String ip;
	private int port;
	private Socket socket;
	
	//////////////////
	// CONSTRUCTORS //
	//////////////////
	
	public ClientConnection(ConnectionListener listener, String ip, int port) {
		super(listener);
		this.ip = ip;
		this.port = port;
	}
	
	/////////////////////////
	// DOT GAME CONNECTION //
	/////////////////////////
	
	protected boolean initSockets() {
		socket = null;
		// attempt to find another dot game client communicating at this ip and port
        try {
            socket = new Socket(ip, port);
            setStreams(socket.getOutputStream(), socket.getInputStream());
        } catch (UnknownHostException e) {
            System.err.println("Unkown host exception.");
            return false;
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection.");
            return false;
        }
		
		//System.out.println("Now communicating with server.");
		
		return true;
	}
	
	protected boolean closeSockets() {
		try {
			if (socket != null)
				socket.close();
			return true;
		} catch (IOException e) {
			System.err.println("There was an error closing the socket.");
			return false;
		}
	}
}