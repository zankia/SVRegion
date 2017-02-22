package fr.zankia.svregion;

import java.util.Calendar;

import org.bukkit.entity.Player;

public abstract class Payment {
	
	protected double price;
	protected int quantity;
	protected Player player;
	
	public Payment(Player player, double price) {
		this.price = price;
		this.player = player;
		this.quantity = 1;
	}
	
	public void setQuantity(int quantity) {
		if(quantity > 0) {
			this.quantity = quantity;
		}
	}
	
	public int getQuantity() {
		return this.quantity;
	}
	
	public void setCurrentPrice() {
		Calendar time = Calendar.getInstance();
		int remaining = 1440 - (time.get(Calendar.HOUR_OF_DAY) * 60 + time.get(Calendar.MINUTE));
		this.price = price / 1440 * remaining;
		this.quantity = 1;
	}

	public abstract boolean pay();


}
