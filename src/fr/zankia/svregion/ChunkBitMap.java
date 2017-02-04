package fr.zankia.svregion;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.TreeSet;

import com.sk89q.worldedit.BlockVector2D;

import java.util.Stack;

/**
 * @author Juleno_
 */

/**                    Z
 *     +--------------------------->
 *     |
 *     |
 *     |															grille de chunk 
 *   X |
 *     |
 *     |
 *    \/
 *
 *  Nécéssaire pour comprendre des commentaires tel que "le coin en haut a gauche"
 */

public class ChunkBitMap {
	private HashMap<Integer,TreeSet<Integer>> chunks;
	private TreeSet<Integer> chunksX;
	private ArrayList<BlockVector2D> corners;
	private int minZ,maxZ;//bordures du "tableau" (grille de chunk)
	private boolean modified;
	private boolean searchX;
	private static Pattern[] patterns;

	public ChunkBitMap() {
		this.chunks = new HashMap<Integer,TreeSet<Integer>>();
		this.chunksX = new TreeSet<Integer>();
		this.modified = true;
		this.searchX = false;

		//configuration des patterns
		if(patterns == null) {
			patterns = new Pattern[3];
			for(int i = 0; i < patterns.length; ++i)
				patterns[i] = new Pattern();
			patterns[0].tab = new boolean[][] {{true,false},{false,false}};
			patterns[0].corners = new PointInt[] {new PointInt(15,15)};
			patterns[0].nbChunk = 1;
			patterns[1].tab = new boolean[][] {{true,true},{true,false}};
			patterns[1].corners = new PointInt[] {new PointInt(15,15)};
			patterns[1].nbChunk = 3;
			patterns[2].tab = new boolean[][] {{true,false},{false,true}};
			patterns[2].corners = new PointInt[] {new PointInt(15,15),new PointInt(16,16)};
			patterns[2].nbChunk = 2;
			//patterns[3].tab=new boolean[][]{{true,true},{false,false}};  pas nécéssaire pour l'instant
			//patterns[3].corners=new PointInt[]{};
			//patterns[3].nbChunk=2;
		}
	}
	
	public boolean contains(int x, int z) {
		return (this.chunksX.contains(x) && this.chunks.get(x).contains(z));
	}
	
	public boolean contains(PointInt x) {
		return contains(x.getX(), x.getZ());
	}
	
	public int toggle(int x, int z) {//renvoi false si il y a eu une erreur
		this.modified = true;
		if(this.contains(x, z)) {
			//au lieu recalculer le maxZ, on le laisse (il sera recalculer lors du prochain appel à getCorners)
			this.chunks.get(x).remove(z);
			if(this.chunks.get(x).isEmpty()) {
				this.chunks.remove(x);
				this.chunksX.remove(x);
			}
			return -1;
		} else {
			if(this.chunksX.size() == 0) {
				this.minZ = z;
				this.maxZ = z;
				this.chunks.put(x, new TreeSet<Integer>());
				this.chunksX.add(x);
				this.chunks.get(x).add(z);
			} else if(this.contains(x-1, z) || this.contains(x, z-1) || this.contains(x+1, z)
					|| this.contains(x, z+1)) {
				//si il y a un chunk adjacent (pour garder la sélection contigue) 
				if(z < this.minZ)
					this.minZ = z;
				if(z > this.maxZ)
					this.maxZ = z;
				if(!this.chunksX.contains(x)) {
					this.chunks.put(x, new TreeSet<Integer>());
					this.chunksX.add(x);
				}
				this.chunks.get(x).add(z);
			} else
				return 0;
			return 1;
		}
	}
	
	public ArrayList<BlockVector2D> getCorners() {
		if(!this.modified)
			return this.corners;
		this.modified=false;

		this.corners = new ArrayList<BlockVector2D>();

		if(this.chunksX.size() > 0) {
			for(int x = this.chunksX.first()-1; x <= this.chunksX.last(); ++x) {
				for(int z = this.minZ-1; z <= this.maxZ; ++z) {
					//DEBUG  System.out.println(x+":"+z);
					for(Pattern pattern : patterns) {
						int orientation;
						if(pattern.nbChunk == countNbChunk(x, z) && (orientation = match(x, z, pattern)) != 0) {
							//DEBUG  System.out.println(x+":"+z+" - "+orientation);
							addCorners(x, z, pattern,orientation);
							break;//on pourrait éviter de tout regénérer chaque fois en gardant une liste des chunks de la sélection modifiés dernièrement et ainsi regénéré que la partie modifiée (il faut quand même re-trier)
						}
					}
				}
			}
		}

		ArrayList<BlockVector2D> sortedCorners = new ArrayList<BlockVector2D>();
		Stack<BlockVector2D> starts = new Stack<BlockVector2D>();
		BlockVector2D lastStart;
		BlockVector2D firstStart = null;
		if(this.corners.size() > 0)
			firstStart = this.corners.get(0);

		BlockVector2D corner;
		while(this.corners.size() > 0) {
			lastStart = corner = this.corners.remove(0);//return the element that was removed from the list
			sortedCorners.add(corner);
			starts.push(corner);
			int i; 
			//DEBUG  System.out.println("debut de boucle avec : "+sortedCorners);
			while((i = findNearest(corner)) != -1) {
				//DEBUG  System.out.println(sortedCorners);
				//DEBUG  System.out.println(corner+" match -> "+i+"ème of "+corners);
				sortedCorners.add(corner = this.corners.remove(i));//return the element that was removed from the list
			}
			if(lastStart != firstStart)
				sortedCorners.add(lastStart);//fermeture de la boucle 
			sortedCorners.add(firstStart);
		}

		this.corners = sortedCorners;
		return this.corners;
	}

	private int match(int x, int z,Pattern patInTable) {
		Pattern pat = new Pattern(patInTable);
		boolean ok = false;
		int rotation;
		for(rotation = 0; !ok && rotation < 4; ++rotation) {
			ok = true;
			for(int rx = 0; rx<2; ++rx)
				for(int rz = 0; rz < 2; ++rz)
					if(!(pat.tab[rx][rz] == this.contains(x + rx, z + rz)))
						ok = false;
			//DEBUG System.out.println("  [["+pat.tab[0][0]+","+pat.tab[1][0]+"],");
			//DEBUG System.out.println("   ["+pat.tab[0][1]+","+pat.tab[1][1]+"]]=======");
			//rotation du pattern
			boolean tmp = pat.tab[0][0];
			pat.tab[0][0] = pat.tab[0][1];
			pat.tab[0][1] = pat.tab[1][1];
			pat.tab[1][1] = pat.tab[1][0];
			pat.tab[1][0] = tmp;
		}
		return ok ? rotation : 0;
	}
	
	private void addCorners(int x, int z, Pattern patInTable, int rotation) {
		Pattern pat = new Pattern(patInTable);
		for(PointInt corner : pat.corners) {
			//DEBUG  System.out.println(corner+" -- "+rotation);
			for(int i = 1; i < rotation; ++i) {
				if(corner.getX() / 16 + corner.getZ() / 16 == 1)// soit dans le chunk en bas à gauche soit dans le chunk en haut à droite 
					corner.setZ(16 * 2 - 1 - corner.getZ());
				else
					corner.setX(16 * 2 - 1 - corner.getX());
				//DEBUG  System.out.println("->"+corner);
			}
			this.corners.add(new BlockVector2D(x * 16 + corner.getX(), z * 16 + corner.getZ()));
		}
	}
	
	private int countNbChunk(int x, int z) {
		int count = 0;
		for(int i = 0; i < 4; ++i)
			if(this.contains(x + i / 2, z + i % 2))
				++count;
		return count;
	}
	
	private int findNearest(BlockVector2D pt) {
		int nearestIndex = -1;
		int nearestDist = Math.max(this.chunksX.size(), maxZ - minZ + 1) * 16;//plus grande distance possible dans la grille
		for(int i = 0; i < this.corners.size(); ++i) {
			int dist = nearestDist;
			if(this.searchX) {
				if(this.corners.get(i).getBlockX() == pt.getBlockX()) {
					dist = Math.abs(this.corners.get(i).getBlockZ() - pt.getBlockZ());
				}
			} else {
				if(this.corners.get(i).getBlockZ() == pt.getBlockZ()) {
					dist = Math.abs(this.corners.get(i).getBlockX() - pt.getBlockX());
				}
			}
			//DEBUG  System.out.print(this.corners.get(i));
			if(dist < nearestDist) {
				nearestDist = dist;
				nearestIndex = i;
			}
		}
		this.searchX = !this.searchX;
		return nearestIndex;
	}

/*
	public void add(int x, int z) {
		this.modified = true;
		if(this.contains(x, z))
			return;
		if(!chunksX.contains(x)) {
			this.chunks.put(x, new TreeSet<Integer>());
			this.chunksX.add(x);
		}
		this.chunks.get(x).add(z);
	}
	
	public void remove(int x, int z) {
		this.modified = true;
		if(!this.contains(x, z))
			return;
		this.chunks.get(x).remove(z);
		if(this.chunks.get(x).isEmpty()) {
			this.chunks.remove(x);
			this.chunksX.remove(x);
		}
	}
*/
	public int size() {
		int count = 0;
		for(int i : this.chunksX) {
			count += this.chunks.get(i).size();
		}
		return count;
	}
	
	public String toString() {
		String output = "";
		for(int key : this.chunksX) {
			output += (key + " : " + this.chunks.get(key) + "\n");
		}
		return output;
	}
	
	private class Pattern {
		public boolean[][] tab;
		public PointInt[] corners;
		public int nbChunk;
		public Pattern(){ }
		public Pattern(Pattern p) {
			this.tab = new boolean[2][2];
			for(int i = 0; i < 4; ++i) {
				this.tab[i / 2][i % 2] = p.tab[i / 2][i % 2];
			}
			this.corners = new PointInt[p.corners.length];
			for(int i = 0; i < p.corners.length; ++i)
				this.corners[i] = new PointInt(p.corners[i]);
			this.nbChunk = p.nbChunk;
		}
	}
	
	private class PointInt {
		private int x;
		private int z;
		public PointInt(int x,int z) { this.x=x;this.z=z; }
		public PointInt(PointInt old) { this.x=old.x;this.z=old.z; }
		public int getX() { return x; }
		public int getZ() { return z; }
		public void setX(int a) { this.x = a; }
		public void setZ(int a) { this.z = a; }
	}
}

