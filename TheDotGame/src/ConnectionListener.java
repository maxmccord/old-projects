import java.util.*;

public interface ConnectionListener extends EventListener {
	public void playerInfoReceived(Player p);
	public void lineReceived(int row, int col);
	public void chatReceived(String msg);
	public void startReceived(int turn, int size);
	public void stopReceived();
}