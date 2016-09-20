package fr.zankia.svregion;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;


public class PlayerListener implements Listener {
	
	@EventHandler (priority = EventPriority.NORMAL)
	public void onClick(PlayerInteractEvent e) {
		if(Selector.isSelector(e.getPlayer().getInventory().getItemInMainHand())) {
			e.getPlayer().sendMessage("Vous avez sélectionné un chunk");
			Selection sel = ((Selector) e.getItem()).getSel();
			int x = e.getPlayer().getLocation().getBlockX() / 16 * 16;
			int y = e.getPlayer().getLocation().getBlockY() / 16 * 16;
			int z = e.getPlayer().getLocation().getBlockZ() / 16 * 16;
			
			sel.addChunk(x, y, z);
			
			e.setCancelled(true);
		}
	}

}
