package fr.zankia.svregion;

import java.util.ListIterator;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import com.sk89q.worldedit.BlockVector2D;
import com.sk89q.worldedit.bukkit.selections.Polygonal2DSelection;
import com.sk89q.worldguard.domains.DefaultDomain;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.managers.storage.StorageException;
import com.sk89q.worldguard.protection.regions.ProtectedPolygonalRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;


public class Selection {
	private ChunkBitMap region;
	private int min;
	private int max;

	
	public Selection() {
		this.region = new ChunkBitMap();
		this.min = 256;
		this.max = 0;
	}
	
	public double getPrice(double bpc, Player p) {
		this.showSel(p);
		return bpc * ((this.max - this.min + 1) / 16) * this.region.size();
	}

	public int addChunk(Player p, int x, int y, int z) {
		int res;
		// >> 4 is coordinates to chunk
		res = this.region.toggle(x >> 4, z >> 4);
		switch(res) {
		case 0:
			return res;
		
		case 1:
			if(y < this.min)
				this.min = y;
			if(y + 15 > this.max)
				this.max = y + 15;
			break;
		
		case -1:
			if(y == this.min)
				this.min += 16;
			if(y + 15 == this.max)
				this.max -= 16;
			break;
		}
		
		if(!this.region.getCorners().isEmpty() && SVR.getWE().getSession(p).hasCUISupport()) {
			SVR.getWE().setSelection(p, new Polygonal2DSelection(p.getWorld(), this.region.getCorners(), min, max));
		}
		return res;
	}

	public boolean setRegion(Player p) {
		if(this.region.getCorners().isEmpty())
			return false;
		ProtectedRegion wgRegion = new ProtectedPolygonalRegion(p.getUniqueId().toString(), this.region.getCorners(), this.min, this.max);
		DefaultDomain owner = new DefaultDomain();
		owner.addPlayer(p.getUniqueId());
		wgRegion.setOwners(owner);

		RegionManager rm = SVR.getWG().getRegionContainer().get(p.getWorld());
		rm.addRegion(wgRegion);
		try {
			rm.save();
		} catch (StorageException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	private void showSel(Player p) {
		for(ListIterator<BlockVector2D> reg = this.region.getCorners().listIterator(); reg.hasNext(); ) {
			BlockVector2D pos1 = reg.next();
			for(int y = this.min; y < this.max; ++y)
				sendBlock(p, pos1.getBlockX(), y, pos1.getBlockZ());
			
			if(reg.hasNext()) {
				BlockVector2D pos2 = reg.next();
				
				int dx = pos2.getBlockX() - pos1.getBlockX();
				int dz = pos2.getBlockZ() - pos1.getBlockZ();
				if(dx > 0) {
					for(int x = 0; x < dx; ++x) {
						sendBlock(p, pos1.getBlockX() + x, this.min, pos1.getBlockZ());
						sendBlock(p, pos1.getBlockX() + x, this.max, pos1.getBlockZ());
					}
				} else if (dx < 0) {
					for(int x = 0; x > dx; --x) {
						sendBlock(p, pos1.getBlockX() + x, this.min, pos1.getBlockZ());
						sendBlock(p, pos1.getBlockX() + x, this.max, pos1.getBlockZ());
					}
				} else if (dz > 0) {
					for(int z = 0; z < dz; ++z) {
						sendBlock(p, pos1.getBlockX() 	 , this.min, pos1.getBlockZ() + z);
						sendBlock(p, pos1.getBlockX() 	 , this.max, pos1.getBlockZ() + z);
					}
				} else if (dz < 0) {
					for(int z = 0; z > dz; --z) {
						sendBlock(p, pos1.getBlockX() 	 , this.min, pos1.getBlockZ() + z);
						sendBlock(p, pos1.getBlockX() 	 , this.max, pos1.getBlockZ() + z);
					}
				}
				reg.previous();
			}
		}
	}

	@SuppressWarnings("deprecation")
	private void sendBlock(Player p, int x, int y, int z) {
		p.sendBlockChange(new Location(p.getWorld(), x, y, z), Material.STAINED_GLASS, (byte) 3);
	}

}
