package fr.zankia.svregion;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class Selector extends ItemStack {
	Selection sel;

	public Selector() {
		super(Material.WOOD_HOE);
		
		ItemMeta meta = this.getItemMeta();
		meta.setDisplayName("Sélecteur");
		
		ArrayList<String> lore = new ArrayList<String>();
		lore.add("Cliquez pour ajouter ou retirer");
		lore.add("un chunk à la sélection.");
		meta.setLore(lore);
		
		this.setItemMeta(meta);
		
		this.sel = new Selection();
	}
	
	public Selection getSel() {
		return sel;
	}

	public boolean setRegion() {
		return true;
	}
	
	public static boolean isSelector(ItemStack item) {
		return (item.getType() == Material.WOOD_HOE && item.getItemMeta().getDisplayName().equals("Sélecteur"));
	}

}
