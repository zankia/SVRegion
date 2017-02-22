package fr.zankia.svregion;

import java.util.Iterator;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class PaymentTerminal implements Listener{
	private Inventory inventory;

	public PaymentTerminal(Player player) {
		super();
		this.inventory = Bukkit.createInventory(player, 9, "Déposez vos items");
		player.openInventory(this.inventory);
		Bukkit.getPluginManager().registerEvents(this, Bukkit.getPluginManager().getPlugin("SVRegion"));
		
	}
	
	@EventHandler
	public void onClose(InventoryCloseEvent e) {
		if(e.getInventory().equals(this.inventory)) {
			InventoryCloseEvent.getHandlerList().unregister(this);
			HumanEntity p = e.getPlayer();
			Inventory pInv = p.getInventory();
			int amount = 0;
			Material mat = Material.getMaterial(SVR.getConfigs().getString("unit"));
			for(Iterator<ItemStack> i = this.inventory.iterator(); i.hasNext() ;) {
				ItemStack item = i.next();
				if(item != null) {
					if(item.getType() == mat) {
						amount += item.getAmount();
					} else {
						pInv.addItem(item);
					}
				}
			}
			ConfigurationSection region = SVR.getRegions().getConfigurationSection("regions."
					+ p.getWorld().getName() + "." + p.getUniqueId().toString());
			int price = (int) region.getDouble("price");
			region.set("remaining", region.getInt("remaining") + (amount / price));
			ItemStack stack = new ItemStack(mat);
			stack.setAmount(amount % price);
			pInv.addItem(stack);
		}
	}

}
