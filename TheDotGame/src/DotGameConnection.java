import java.io.*;
import java.net.*;
import java.util.*;
import java.awt.*;

public abstract class DotGameConnection {
	// constants
	public static final int STATUS_UNOPENED = 0;
	public static final int STATUS_OPENED = 1;
	public static final int STATUS_CLOSED = 2;
	
	public static final int ID_PLAYER = 0;
	public static final int ID_LINE = 1;
	public static final int ID_CHAT = 2;
	public static final int ID_START = 3;
	public static final int ID_STOP = 4;
	
	// private variables
	private int status = STATUS_UNOPENED;
	private ConnectionListener listener;
	private BufferedReader in;
	private PrintWriter out;
	private Thread listenThread;
	
	///////////////
	// ACCESSORS //
	///////////////
	
	public int getConnectionStatus() {
		return status;
	}
	
	//////////////////
	// CONSTRUCTORS //
	//////////////////
	
	public DotGameConnection(ConnectionListener listener) {
		this.listener = listener;
	}
	
	////////////////////
	// PUBLIC METHODS //
	////////////////////
	
	// opens the connection, depending on if instance is Host or Client
	public void openConnection() {
		initSockets(); // opens sockets and prepares I/O streams
		
		// set status to 'open'
		status = STATUS_OPENED;
		
		// determine who's turn it is by sending a random number 0-99,
		// bigger number goes first, and if numbers are equal, retry
		int rand = 0, other = 0;
		do {
			// generate a random number, and send it to opponent
			rand = (int)(Math.random()*100);
			out.println(rand);
			
			// read in the opponent's random number
			String inputLine = null;
			try { inputLine = in.readLine(); }
			catch (Exception e) { }
			if (inputLine != null)
				other = Integer.parseInt(inputLine);
		} while (rand == other);
		
		// send or retrieve size depending if this is a Host or Client
		int size = 3;
		if (this instanceof HostConnection) {
			HostConnection host = (HostConnection)this;
			size = host.getSize();
			out.println(size);
		} else {
			String inputLine = null;
			try { inputLine = in.readLine(); }
			catch (Exception e) { }
			if (inputLine != null)
				size = Integer.parseInt(inputLine);
		}
		
		// start based on random number - bigger goes first
		// if nums are equal, the host goes first
		if (rand > other)
			listener.startReceived(1, size);
		else if (rand < other)
			listener.startReceived(2, size);
		else if (this instanceof HostConnection)
			listener.startReceived(1, size);
		else
			listener.startReceived(2, size);
		
		listenThread = new Thread(new Runnable() {
			public void run() {
				listen();
			}
		});
		listenThread.start();
	}
	
	// closes I/O streams and sockets
	public void close() {
		try {
			if (status == STATUS_OPENED) {
				status = STATUS_CLOSED;
				in.close();
				out.close();
				closeSockets(); // abstract method call
			}
		} catch (IOException ex) {
			System.err.println("There was an error closing the connection.");
		}
	}
	
	public void sendPlayerInfo(Player p) {
		Color c = p.getColor();
		int r = c.getRed();
		int g = c.getGreen();
		int b = c.getBlue();
		out.println(ID_PLAYER + "" + p.getName() + " " + r + " " + g + " " + b);
	}
	
	public void sendLine(int row, int col) {
		out.println(ID_LINE + "" + row + " " + col);
	}
	
	public void sendChat(String msg) {
		out.println(ID_CHAT + "" + msg);
	}
	
	public void sendStart() {
		out.println(ID_START);
	}
	
	public void sendStop() {
		out.println(ID_STOP);
	}
	
	/////////////////////
	// PRIVATE METHODS //
	/////////////////////
	
	private void listen() {
		while (status == STATUS_OPENED) {
			try {
				String inputLine = in.readLine();
				int id = Integer.parseInt(inputLine.substring(0, 1));
				String line = inputLine.substring(1, inputLine.length());
				String[] data = line.split(" ");
				
				switch (id) {
					case ID_PLAYER:
						String name = data[0];
						int r = Integer.parseInt(data[1]);
						int g = Integer.parseInt(data[2]);
						int b = Integer.parseInt(data[3]);
						listener.playerInfoReceived(new Player(name, new Color(r, g, b)));
						break;
					case ID_LINE:
						int row = Integer.parseInt(data[0]);
						int col = Integer.parseInt(data[1]);
						listener.lineReceived(row, col);
						break;
					case ID_CHAT:
						listener.chatReceived(line);
						break;
					case ID_START:
						break;
					case ID_STOP:
						listener.stopReceived();
						break;
				}
			} catch (IOException e) {
				System.out.println("IOException - closing connection.");
				close();
			} catch (Exception e) {
				
			} 
		}
	}
	
	///////////////////////
	// PROTECTED METHODS //
	///////////////////////
	
	protected void setStreams(OutputStream outStream, InputStream inStream) {
		out = new PrintWriter(outStream, true);
        in = new BufferedReader(new InputStreamReader(inStream));
	}
	
	//////////////////////
	// ABSTRACT METHODS //
	//////////////////////
	
	// subclasses open sockets and open the connection
	protected abstract boolean initSockets();
	
	// subclasses must close the sockets they open
	protected abstract boolean closeSockets();
}