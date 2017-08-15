import java.io.*;
import java.net.*;

public class HostConnection extends DotGameConnection {
	// private variables
	private ServerSocket serverSocket;
	private int port;
	private Socket socket;
	private int size;
	
	///////////////
	// ACCESSORS //
	///////////////
	
	public int getSize() {
		return size;
	}
	
	//////////////////
	// CONSTRUCTORS //
	//////////////////
	
	public HostConnection(ConnectionListener listener, int port, int size) {
		super(listener);
		this.port = port;
		this.size = size;
	}
	
	/////////////////////////
	// DOT GAME CONNECTION //
	/////////////////////////
	
	protected boolean initSockets() {
		// attempt to create the server socket
		serverSocket = null;
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            System.err.println("Could not listen on port " + port);
            return false;
        }
		
		//System.out.println("The Dot Game is now listening for connections.");
		
		// listen for client connections until one is made, create new socket
        socket = null;
        try {
            socket = serverSocket.accept();
        } catch (IOException e) {
            System.err.println("Socket accept failed.");
            return false;
        }

		//System.out.println("The Dot Game has connected to a client.");
		
		// create writer and reader for the socket streams
		try {
			setStreams(socket.getOutputStream(), socket.getInputStream());
		} catch (IOException e) {
			System.err.println("Error opening socket I/O streams.");
			return false;
		}
		
		// host decides whose turn it is
		/*int turn = (int)(Math.random()*2)+1;
		out.println("start " + turn);
		parent.actionPerformed(new ActionEvent(this, 0, "start " + ((turn == 1) ? 2 : 1)));
		System.out.println("Host told itself to start the game.");*/
		
		return true;
	}
	
	protected boolean closeSockets() {
		try {
			if (socket != null)
				socket.close();
			if (serverSocket != null)
				serverSocket.close();
			return true;
		} catch (IOException e) {
			System.err.println("There was an error closing the sockets.");
			return false;
		}
	}
}