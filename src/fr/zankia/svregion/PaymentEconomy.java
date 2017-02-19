package fr.zankia.svregion;

import org.bukkit.entity.Player;

public class PaymentEconomy extends Payment {

	public PaymentEconomy(Player p, double price) {
		super(p, price);
	}

	@Override
	public boolean pay() {
		if(VaultLink.economy.has(p, this.price * this.quantity)) {
			VaultLink.economy.withdrawPlayer(this.p, this.price * this.quantity);
			return true;
		}
		return false;
	}

}
