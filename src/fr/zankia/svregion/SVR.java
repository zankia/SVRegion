package fr.zankia.svregion;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public class SVR extends JavaPlugin {
	
	@Override
	public void onEnable() {
		saveDefaultConfig();
		getServer().getPluginManager().registerEvents(new PlayerListener(), this);
		getLogger().info("Enabled");
	}

	@Override
	public void onDisable() {
		getLogger().info("Disabled");
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String name, String[] args) {
		if(name.equalsIgnoreCase("svr") || name.equalsIgnoreCase("svregion")) {
			
			if(0 == args.length)
				return false;
			
			switch(args[0].toLowerCase()) {
			case "reload" :
				return SVRCommand.reloadCmd(sender, args);
				
			case "wand" :
				return SVRCommand.wandCmd(sender, args);
				
			case "info" :
				return SVRCommand.infoCmd(sender, args);
				
			case "confirm" :
				return SVRCommand.confirmCmd(sender, args);
			}
		}
		return false;
	}

}
