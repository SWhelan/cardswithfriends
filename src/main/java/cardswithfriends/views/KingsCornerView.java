package cardswithfriends.views;

import java.util.LinkedList;
import java.util.List;

import cardswithfriends.KingsCorner;
import cardswithfriends.Player;

public class KingsCornerView {
	public int gameId;
	public String currentPlayerName;
	public List<String> players = new LinkedList<String>();
	public KingsCornerView(KingsCorner kc){
		gameId = kc.get_id();
		currentPlayerName = kc.getCurrentPlayerObject().getUserName();
		for(Player p : kc.turnOrder){
			players.add(p.getUserName());
		}
	}
}
