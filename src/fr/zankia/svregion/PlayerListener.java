package fr.zankia.svregion;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;


public class PlayerListener implements Listener {
	private PlayerMap map = PlayerMap.getInstance();
	
	@EventHandler (priority = EventPriority.NORMAL)
	public void onClick(PlayerInteractEvent e) {
		Player p = e.getPlayer();
		if(e.getItem() != null && map.isRegistered(e.getPlayer()) && Selector.isSelector(e.getItem())) {
			Selection sel = map.getSelection(p);
			int x = p.getLocation().getBlockX() / 16 * 16;
			int y = p.getLocation().getBlockY() / 16 * 16;
			int z = p.getLocation().getBlockZ() / 16 * 16;
			
			if(p.getLocation().getBlockX() < 0)
				--x;
			
			if(p.getLocation().getBlockZ() < 0)
				--z;
			
			int message = sel.addChunk(p, x, y, z);
			switch(message) {
			case -1:
				p.sendMessage("Vous avez retiré un chunk");
				break;
			
			case 0:
				p.sendMessage( "Erreur : Le chunk est trop loin");
				break;
			
			case 1:
				p.sendMessage("Vous avez sélectionné un chunk");
				break;
			}
			
			map.setSelection(p, sel);
			
			e.setCancelled(true);
		}
	}
	
	//disallow wand droping
	@EventHandler (priority = EventPriority.NORMAL)
	public void onDrop(PlayerDropItemEvent e) {
		if(map.isRegistered(e.getPlayer()) && Selector.isSelector(e.getItemDrop().getItemStack()))
			e.setCancelled(true);
	}

}
