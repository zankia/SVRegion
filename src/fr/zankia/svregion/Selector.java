package fr.zankia.svregion;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class Selector extends ItemStack {
	private static Material type;
	private static final String name = "Sélecteur";

	public Selector(Material mat) {
		super(mat);
		type = mat;
		
		ItemMeta meta = this.getItemMeta();
		meta.setDisplayName(name);
		
		ArrayList<String> lore = new ArrayList<String>();
		lore.add("Cliquez pour ajouter ou retirer");
		lore.add("un chunk à la sélection.");
		meta.setLore(lore);
		
		this.setItemMeta(meta);
	}
	
	public static boolean isSelector(ItemStack item) {
		return (item.getType() == type && item.getItemMeta().getDisplayName().equals(name));
	}

}
