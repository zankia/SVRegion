package fr.zankia.svregion;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import com.sk89q.worldedit.bukkit.selections.CuboidSelection;

public final class SVRCommand {
	private static final String NO_PERMISSION = ChatColor.RED + "Erreur : Vous n'avez pas la permission pour cette commande.";
	private static final String NOT_PLAYER = ChatColor.RED + "Erreur : Vous devez être un joueur pour utiliser cette commande.";
	private static final String NO_WAND = ChatColor.RED + "Erreur : Vous devez d'abord vous procurer le sélecteur.";
	private static final String NO_REGION = ChatColor.RED + "Erreur : Vous n'avez pas de région.";
	private static final String ALREADY_DONE = ChatColor.RED + "Erreur : Vous avez déjà demandé le sélecteur.";
	private static final String CONFIG_ERROR = ChatColor.RED + "Erreur dans la configuration. Veuillez contacter l'administrateur.";
	private static final String PLUGIN_TITLE = ChatColor.RED + "SVRegion : " + ChatColor.GREEN;
	
	private static SVR pl = (SVR) Bukkit.getPluginManager().getPlugin("SVRegion");
	

	public static boolean reloadCmd(CommandSender sender, String[] args) {
		if(sender.hasPermission("svregion.admin")) {
				pl.reloadConfig();
				pl.updateConfigs();
				sender.sendMessage(PLUGIN_TITLE + "Reload done.");
		} else
			sender.sendMessage(NO_PERMISSION);
		return true;
	}

	public static boolean wandCmd(CommandSender sender, String[] args) {
		if(sender.hasPermission("svregion.user")) {
			if(sender instanceof Player) {
				Player p = (Player) sender;
				
				if(PlayerMap.getInstance().addPlayer(p)) {
					Material mat = Material.getMaterial(SVR.getConfigs().getString("selectorName"));
					if(mat != null) {
						p.getInventory().addItem(new Selector(mat));
						p.sendMessage(PLUGIN_TITLE + "Voici votre sélecteur.");
					} else {
						p.sendMessage(CONFIG_ERROR);
					}
				} else
					p.sendMessage(ALREADY_DONE);
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
				PlayerMap map = PlayerMap.getInstance();
				if(map.isRegistered(p)) {
					p.sendMessage(PLUGIN_TITLE + "Cela vous coutera " + getPrice(p) + " "
							+ VaultLink.economy.currencyNamePlural() + " par jour.");
				} else {
					if(SVR.getRegions().contains("regions." + p.getUniqueId().toString())) {
						ConfigurationSection pinfo = SVR.getRegions().getConfigurationSection("regions."
								+ p.getUniqueId().toString());
						p.sendMessage(PLUGIN_TITLE + "Cela vous coute " + pinfo.getDouble("price") + " "
								+ VaultLink.economy.currencyNamePlural() + " par jour.");
						p.sendMessage(PLUGIN_TITLE + "Il vous reste " + pinfo.getInt("remaining")
								+ (pinfo.getInt("remaining") > 1 ? " jours." : " jour."));
					} else
						sender.sendMessage(NO_REGION);
				}
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
				PlayerMap map = PlayerMap.getInstance();
				if(map.isRegistered(p)) {
					if(map.getSelection(p).setRegion(p)) {
						String path = "regions." + p.getUniqueId().toString();
						SVR.getRegions().createSection(path);
						SVR.getRegions().set(path + ".price", getPrice(p));
						SVR.getRegions().set(path + ".remaining", 0);
						SVR.saveRegions();
						p.sendMessage(PLUGIN_TITLE + "Région créée");
					} else {
						p.sendMessage(PLUGIN_TITLE + "Erreur lors de la création de la région");
					}
					removeWand(p);
					map.removePlayer(p);
				} else
					p.sendMessage(NO_WAND);
			} else
				sender.sendMessage(NOT_PLAYER);
		} else
			sender.sendMessage(NO_PERMISSION);
		return true;
	}

	public static boolean cancelCmd(CommandSender sender, String[] args) {
		if(sender.hasPermission("svregion.user")) {
			if(sender instanceof Player) {
				Player p = (Player) sender;
				if(PlayerMap.getInstance().removePlayer(p)) {
					removeWand(p);
					sender.sendMessage(PLUGIN_TITLE + ("Sélection annulée"));
				} else
					sender.sendMessage(NO_WAND);
			} else
				sender.sendMessage(NOT_PLAYER);
		} else
			sender.sendMessage(NO_PERMISSION);
		return true;
	}

	//TODO : Find a better way to remove ONLY the wand
	private static void removeWand(Player p) {
		Material mat = Material.getMaterial(SVR.getConfigs().getString("selectorName"));
		if(mat != null) {
			p.getInventory().remove(mat);
		} else {
			p.sendMessage(CONFIG_ERROR);
		}
		removeSelection(p);
	}
	
	private static void removeSelection(Player p) {
		if(SVR.getWE().getSession(p).hasCUISupport()) {
			Location l = new Location(p.getWorld(), 0, 0, 0);
			SVR.getWE().setSelection(p, new CuboidSelection(p.getWorld(), l, l));
		}
	}

	private static double getPrice(Player p) {
		return PlayerMap.getInstance().getSelection(p).getPrice(SVR.getConfigs().getDouble("bpc"), p);
	}
}
