package fr.zankia.svregion;

import org.bukkit.configuration.ConfigurationSection;

public class Scheduler implements Runnable {

	@Override
	public void run() {
		//reduce all region days in config
		//delete the region if days < 0
		ConfigurationSection regions = SVR.getRegions().getConfigurationSection("regions");
		for(String uuid : regions.getKeys(false)) {
			int remaining = regions.getInt(uuid + ".remaining");
			if(remaining > 0) {
				regions.set(uuid + ".remaining", --remaining);
			} else {
				regions.set(uuid, null);
			}
		}
		SVR.saveRegions();
	}

}
