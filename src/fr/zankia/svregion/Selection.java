package fr.zankia.svregion;

import java.util.ArrayList;
import java.util.List;

import com.sk89q.worldedit.BlockVector2D;

public class Selection {
	List<BlockVector2D> region;
	int min;
	int max;

	
	public Selection() {
		this.region = new ArrayList<BlockVector2D>();
		this.min = 256;
		this.max = 0;
	}
	
	public double getPrice(double bpb) {
		return bpb * (max - min);
	}

	public void addChunk(int x, int y, int z) {
		toggle(x, z);
		toggle(x + 16, z);
		toggle(x, z + 16);
		toggle(x + 16, z + 16);
		
		if(y < this.min)
			this.min = y;
		
		if(y + 16 > this.max)
			this.max = y + 16;
	}

	public void toggle(int x, int z) {
		for(BlockVector2D reg : this.region) {
			if(reg.getBlockX() == x && reg.getBlockZ() == z)
				this.region.remove(reg);
		}
	}

}
