package fr.zankia.svregion;

import org.bukkit.configuration.ConfigurationSection;

public class Scheduler implements Runnable {

	@Override
	public void run() {
		//reduce all region days in config
		//delete the region if days < 0
		ConfigurationSection regions = SVR.getRegions().getConfigurationSection("regions");
		for(String worlds : regions.getKeys(false)) {
			ConfigurationSection world = regions.getConfigurationSection(worlds);
			for(String uuid : world.getKeys(false)) {
				int remaining = world.getInt(uuid + ".remaining");
				if(remaining > 0) {
					world.set(uuid + ".remaining", --remaining);
				} else {
					world.set(uuid, null);
					SVR.getWG().getRegionManager(SVR.getPlugin(SVR.class).getServer().getWorld(worlds)).removeRegion(uuid);
				}
			}
		}
		SVR.saveRegions();
	}

}
