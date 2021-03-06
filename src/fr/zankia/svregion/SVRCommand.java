package fr.zankia.svregion;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import com.sk89q.worldedit.bukkit.selections.CuboidSelection;
import com.sk89q.worldguard.domains.DefaultDomain;

public final class SVRCommand {
	private static final String NO_PERMISSION = ChatColor.RED + "Erreur : Vous n'avez pas la permission pour cette commande.";
	private static final String NOT_PLAYER = ChatColor.RED + "Erreur : Vous devez être un joueur pour utiliser cette commande.";
	private static final String NO_WAND = ChatColor.RED + "Erreur : Vous devez d'abord vous procurer le sélecteur.";
	private static final String NO_REGION = ChatColor.RED + "Erreur : Vous n'avez pas de région.";
	private static final String NOT_ENOUGH_MONEY = ChatColor.RED + "Erreur : Vous n'avez pas assez d'argent.";;
	private static final String ALREADY_DONE = ChatColor.RED + "Erreur : Vous avez déjà demandé le sélecteur.";
	private static final String PLAYER_NOT_FOUND = ChatColor.RED + "Erreur : Le joueur est introuvable.";
	private static final String CONFIG_ERROR = ChatColor.RED + "Erreur dans la configuration. Veuillez contacter l'administrateur.";
	private static final String PLUGIN_TITLE = ChatColor.RED + "SVRegion : " + ChatColor.GREEN;
	
	private static SVR pl = SVR.getPlugin(SVR.class);
	

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
							+ (SVR.getConfigs().getBoolean("useEconomy")
							? VaultLink.economy.currencyNamePlural() : "items") + " par jour.");
				} else {
					if(hasRegion(p)) {
						ConfigurationSection pinfo = SVR.getRegions().getConfigurationSection(
								"regions." + p.getWorld().getName() + "."
								+ p.getUniqueId().toString());
						p.sendMessage(PLUGIN_TITLE + "Cela vous coute " + pinfo.getDouble("price")
								+ " " + (SVR.getConfigs().getBoolean("useEconomy")
								? VaultLink.economy.currencyNamePlural() : "items") + " par jour.");
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
					Payment pay = SVR.getConfigs().getBoolean("useEconomy")
							? new PaymentEconomy(p, getPrice(p)) : new PaymentItem(p, getPrice(p));
					pay.setCurrentPrice();
					if(pay.pay()) {
						if(map.getSelection(p).setRegion(p)) {
						String path = "regions." + p.getWorld().getName() + "."
								+ p.getUniqueId().toString();
						SVR.getRegions().createSection(path);
						SVR.getRegions().set(path + ".price", getPrice(p));
						SVR.getRegions().set(path + ".remaining", 0);
						SVR.saveRegions();
						p.sendMessage(PLUGIN_TITLE + "Région créée");
						} else {
							p.sendMessage(PLUGIN_TITLE + "Erreur lors de la création de la région");
						}
					} else {
						p.sendMessage(NOT_ENOUGH_MONEY);
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
					sender.sendMessage(PLUGIN_TITLE + "Sélection annulée");
				} else
					sender.sendMessage(NO_WAND);
			} else
				sender.sendMessage(NOT_PLAYER);
		} else
			sender.sendMessage(NO_PERMISSION);
		return true;
	}

	public static boolean modifyCmd(CommandSender sender, String[] args) {
		if(sender.hasPermission("svregion.user")) {
			if(sender instanceof Player) {
				Player p = (Player) sender;
				if(hasRegion(p)) {
					if(PlayerMap.getInstance().addPlayerRegion(p)) {
						Material mat = Material.getMaterial(
								SVR.getConfigs().getString("selectorName"));
						if(mat != null) {
							p.getInventory().addItem(new Selector(mat));
							p.sendMessage(PLUGIN_TITLE + "Voici votre sélecteur.");
						} else {
							p.sendMessage(CONFIG_ERROR);
						}
					} else
						p.sendMessage(ALREADY_DONE);
				} else
					sender.sendMessage(NO_REGION);
			} else
				sender.sendMessage(NOT_PLAYER);
		} else
			sender.sendMessage(NO_PERMISSION);
		return true;
	}

	public static boolean addCmd(CommandSender sender, String[] args) {
		if(sender.hasPermission("svregion.user")) {
			if(sender instanceof Player) {
				Player p = (Player) sender;
				if(hasRegion(p)) {
					Player toAdd = pl.getServer().getPlayer(args[1]);
					if(toAdd != null) {
						getRegion(p).addPlayer(toAdd.getUniqueId());
						sender.sendMessage(PLUGIN_TITLE + args[1] + " a été ajouté");
					} else
						sender.sendMessage(PLAYER_NOT_FOUND);
				} else
					sender.sendMessage(NO_REGION);
			} else
				sender.sendMessage(NOT_PLAYER);
		} else
			sender.sendMessage(NO_PERMISSION);
		return true;
	}

	public static boolean removeCmd(CommandSender sender, String[] args) {
		if(sender.hasPermission("svregion.user")) {
			if(sender instanceof Player) {
				Player p = (Player) sender;
				if(hasRegion(p)) {
					Player toRemove = pl.getServer().getPlayer(args[1]);
					if(toRemove != null) {
						getRegion(p).removePlayer(toRemove.getUniqueId());
						sender.sendMessage(PLUGIN_TITLE + args[1] + " a été retiré");
					} else
						sender.sendMessage(PLAYER_NOT_FOUND);
				} else
					sender.sendMessage(NO_REGION);
			} else
				sender.sendMessage(NOT_PLAYER);
		} else
			sender.sendMessage(NO_PERMISSION);
		return true;
	}

	public static boolean payCmd(CommandSender sender, String[] args) {
		if(sender.hasPermission("svregion.pay")) {
			if(sender instanceof Player) {
				Player p = (Player) sender;
				if(hasRegion(p)) {
					if(args.length == 2) {
						ConfigurationSection reg = SVR.getRegions().getConfigurationSection(
								"regions." + p.getWorld().getName() + "."
								+ p.getUniqueId().toString());
						Payment pay = SVR.getConfigs().getBoolean("useEconomy")
								? new PaymentEconomy(p, reg.getDouble("price"))
								: new PaymentItem(p, reg.getDouble("price"));
						try {
							pay.setQuantity(Integer.parseInt(args[1]));
						} catch(NumberFormatException e) {
							return false;
						}
						if(pay.pay()) {
							reg.set("remaining",  pay.getQuantity() + (int) reg.get("remaining"));
						} else {
							sender.sendMessage(NOT_ENOUGH_MONEY);
						}
					} else {
						if(SVR.getConfigs().getBoolean("useEconomy")) {
							return false;
						} else {
							new PaymentTerminal(p);
						}
					}
				} else
					sender.sendMessage(NO_REGION);
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
		return PlayerMap.getInstance().getSelection(p).getPrice(SVR.getConfigs().getDouble("upc"), p);
	}

	private static boolean hasRegion(Player p) {
		return SVR.getRegions().contains("regions." + p.getWorld().getName() + "."
				+ p.getUniqueId().toString());
	}
	
	private static DefaultDomain getRegion(Player p) {
		return SVR.getWG().getRegionManager(p.getWorld()).getRegion(
				p.getUniqueId().toString()).getMembers();
	}
}
