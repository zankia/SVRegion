package fr.zankia.svregion;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;

import net.milkbowl.vault.economy.Economy;

public class SVR extends JavaPlugin {
	private static FileConfiguration configs;
	private static FileConfiguration regions;
	private static File regionsFile;
	private static WorldEditPlugin we;
	private static WorldGuardPlugin wg;
	private ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

	@Override
	public void onEnable() {
		this.saveDefaultConfig();
		regionsFile = new File(this.getDataFolder(), "regions.yml");
		if(!regionsFile.exists())
			this.saveResource("regions.yml", false);
		this.updateConfigs();
		this.getServer().getPluginManager().registerEvents(new PlayerListener(), this);
		we = ((WorldEditPlugin) this.getServer().getPluginManager().getPlugin("WorldEdit"));
		wg = ((WorldGuardPlugin) this.getServer().getPluginManager().getPlugin("WorldGuard"));
		VaultLink.setupEconomy(this.getServer().getServicesManager().getRegistration(Economy.class));
		this.setupScheduler();
		this.getLogger().info("Enabled");
	}

	@Override
	public void onDisable() {
		saveRegions();
		this.scheduler.shutdown();
		this.getLogger().info("Disabled");
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

			case "cancel" :
				return SVRCommand.cancelCmd(sender, args);
				
			case "modify" :
				return SVRCommand.modifyCmd(sender, args);
				
			case "add" :
				return SVRCommand.addCmd(sender, args);

			case "remove" :
				return SVRCommand.removeCmd(sender, args);
				
			case "pay" :
				return SVRCommand.payCmd(sender, args);
			}
		}
		return false;
	}

	public static FileConfiguration getConfigs() {
		return configs;
	}

	public void updateConfigs() {
		configs = this.getConfig();
		regions = YamlConfiguration.loadConfiguration(regionsFile);
	}

	public static void saveRegions() {
		try {
			regions.save(regionsFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static FileConfiguration getRegions() {
		return regions;
	}

	public static WorldEditPlugin getWE() {
		return we;
	}

	public static WorldGuardPlugin getWG() {
		return wg;
	}

	private void setupScheduler() {
		Calendar time = Calendar.getInstance();
		int remaining = 1440 - (time.get(Calendar.HOUR_OF_DAY) * 60 + time.get(Calendar.MINUTE));
		this.scheduler.scheduleWithFixedDelay(new Scheduler(), remaining, 1440, TimeUnit.MINUTES);
	}

}
