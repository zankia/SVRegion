package fr.zankia.svregion;

import org.bukkit.plugin.RegisteredServiceProvider;

import net.milkbowl.vault.economy.Economy;

public class VaultLink {
	public static Economy economy = null;

	protected static void setupEconomy(RegisteredServiceProvider<Economy> economyProvider) {
		economy = economyProvider.getProvider();
	}
}
