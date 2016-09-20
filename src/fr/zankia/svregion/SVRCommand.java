package fr.zankia.svregion;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

public final class SVRCommand {
	private static final String NO_PERMISSION = ChatColor.RED + "Erreur : Vous n'avez pas la permission pour cette commande.";
	private static final String NOT_PLAYER = ChatColor.RED + "Erreur : Vous devez être un joueur pour utiliser cette commande.";
	private static final String PLUGIN_TITLE = ChatColor.RED + "SVRegion : " + ChatColor.GREEN;
	private static final String NOT_SELECTOR = ChatColor.RED + "Erreur : Vous devez avoir le sélecteur dans la main.";
	
	private static Plugin pl = Bukkit.getPluginManager().getPlugin("SVRegion");
	private static FileConfiguration config = pl.getConfig();
	

	public static boolean reloadCmd(CommandSender sender, String[] args) {
		if(sender.hasPermission("svregion.admin")) {
				pl.reloadConfig();
				config = pl.getConfig();
				sender.sendMessage(PLUGIN_TITLE + "Reload done.");
		} else
			sender.sendMessage(NO_PERMISSION);
		return true;
	}

	public static boolean wandCmd(CommandSender sender, String[] args) {
		if(sender.hasPermission("svregion.user")) {
			if(sender instanceof Player) {
				((Player) sender).getInventory().addItem(new Selector());
				sender.sendMessage(PLUGIN_TITLE + "Voici votre sélecteur.");
			} else
				sender.sendMessage(NOT_PLAYER);
		} else
			sender.sendMessage(NO_PERMISSION);
		return true;
	}

	public static boolean infoCmd(CommandSender sender, String[] args) {
		if(sender.hasPermission("svregion.user")) {
			if(sender instanceof Player) {
				Player p = (Player) sender;
				ItemStack wand = p.getInventory().getItemInMainHand();
				if(Selector.isSelector(wand)) {
					Selection sel = ((Selector) wand).getSel();
					sender.sendMessage(PLUGIN_TITLE + "Cela vous coutera" +
							sel.getPrice(config.getDouble("bpb")) + ".");
				} else
					sender.sendMessage(NOT_SELECTOR);
			} else
				sender.sendMessage(NOT_PLAYER);
		} else
			sender.sendMessage(NO_PERMISSION);
		return true;
	}

	public static boolean confirmCmd(CommandSender sender, String[] args) {
		if(sender.hasPermission("svregion.user")) {
			if(sender instanceof Player) {
				Player p = (Player) sender;
				ItemStack wand = p.getInventory().getItemInMainHand();
				if(Selector.isSelector(wand)) {
					sender.sendMessage(PLUGIN_TITLE + (((Selector) wand).setRegion() ?
							"Région créée" : "Erreur lors de la création de la région"));
				} else
					sender.sendMessage(NOT_SELECTOR);
			} else
				sender.sendMessage(NOT_PLAYER);
		} else
			sender.sendMessage(NO_PERMISSION);
		return true;
	}

}
