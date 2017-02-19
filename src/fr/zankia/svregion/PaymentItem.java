package fr.zankia.svregion;

import java.util.ListIterator;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class PaymentItem extends Payment {

	public PaymentItem(Player p, double price) {
		super(p, price);
	}

	@Override
	public boolean pay() {
		Material mat = Material.getMaterial(SVR.getConfigs().getString("unit"));
		PlayerInventory inv = this.p.getInventory();
		int amount = this.quantity * (int) (this.price);
		for(ListIterator<ItemStack> ite =  inv.iterator(inv.first(mat)); ite.hasNext() ; ) {
			ItemStack item = ite.next();
			if(item != null && item.getType() == mat) {
				if(item.getAmount() < amount) {
					//insuficient amount in itemstack
					amount -= item.getAmount() * inv.all(item).size();
					inv.remove(item);
					if(amount < 0) {
						item.setAmount(-amount);
						inv.addItem(item);
						return true;
					}
					
				} else {
					//sufficient amount
					item.setAmount(item.getAmount() - amount);
					return true;
				}
			}
		}
		ItemStack item = new ItemStack(mat);
		item.setAmount(this.quantity * (int) (this.price) - amount);
		inv.addItem(item);
		return false;
	}

}
