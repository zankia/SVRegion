package fr.zankia.svregion;

import java.util.HashMap;

import org.bukkit.entity.Player;

public class PlayerMap {
	private HashMap<Player, Selection> selections;

	private PlayerMap() {
		this.selections = new HashMap<Player, Selection>();
	}

	private static PlayerMap INSTANCE = new PlayerMap();

	public static PlayerMap getInstance(){
		return INSTANCE;
	}

	public boolean addPlayer(Player p) {
		if(this.isRegistered(p))
			return false;
		this.selections.put(p, new Selection());
		return true;
	}

	public boolean addPlayerRegion(Player p) {
		if(this.isRegistered(p))
			return false;
		this.selections.put(p, new Selection(p));
		return true;
	}

	public boolean removePlayer(Player p) {
		if(!this.isRegistered(p))
			return false;
		this.selections.remove(p);
		return true;
	}

	public Selection getSelection(Player p) {
		return this.selections.get(p);
	}

	public void setSelection(Player p, Selection sel) {
		this.selections.put(p, sel);
	}

	public boolean isRegistered(Player p) {
		return selections.containsKey(p);
	}

}
