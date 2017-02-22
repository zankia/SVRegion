package fr.zankia.svregion;

import org.bukkit.entity.Player;

public class PaymentEconomy extends Payment {

	public PaymentEconomy(Player player, double price) {
		super(player, price);
	}

	@Override
	public boolean pay() {
		if(VaultLink.economy.has(player, this.price * this.quantity)) {
			VaultLink.economy.withdrawPlayer(this.player, this.price * this.quantity);
			return true;
		}
		return false;
	}

}
