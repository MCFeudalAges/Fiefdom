package org.nowireless.factions.integration.dynmap;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.dynmap.DynmapAPI;
import org.dynmap.markers.AreaMarker;
import org.dynmap.markers.Marker;
import org.dynmap.markers.MarkerAPI;
import org.dynmap.markers.MarkerIcon;
import org.dynmap.markers.MarkerSet;
import org.nowireless.factions.entity.Region;
import org.nowireless.factions.entity.RegionBoardCollections;
import org.nowireless.factions.entity.RegionCollection;
import org.nowireless.factions.entity.RegionCollections;
import org.nowireless.factions.event.FactionsEventRegionChanged;
import org.nowireless.factions.event.FactionsEventRegionChunkChange;
import org.nowireless.factions.event.FactionsEventRegionClaim;
import org.nowireless.factions.event.FactionsEventRegionCreate;
import org.nowireless.factions.event.FactionsEventRegionDestroy;

import com.massivecraft.factions.Factions;
import com.massivecraft.massivecore.event.EventMassiveCore;
import com.massivecraft.massivecore.ps.PS;
import com.massivecraft.massivecore.util.Txt;

public class EngineDynmap implements Listener {
	private enum Direction { 
		XPLUS,
		ZPLUS,
		XMINUS,
		ZMINUS
	};
	
	private class AreaStyle{
		public String strokecolor = "#FF0000";
		public double strokeopacity = 0.8;
		public int strokeweight = 3;
		public String fillcolor = "#FF0000";
		public double fillopacity = 0.35;
		public String homemarker = null;
		public boolean boost = false;
		public MarkerIcon homeicon;
		
		public AreaStyle() {
			if(homemarker != null) {
				homeicon = EngineDynmap.this.markerAPI.getMarkerIcon(homemarker);
				if(homeicon == null) {
					Factions.get().log(Txt.parse("<bad> Invalid home icon <v>" + homemarker));
					homeicon = EngineDynmap.this.markerAPI.getMarkerIcon("blueicone");
				}
			}
		}
	}
	
	private class Updater implements Runnable {
		public boolean runOnce;

		@Override
		public void run() {
			if(run) {
				EngineDynmap.this.updateRegions();
				if(!runOnce) {
					Bukkit.getScheduler().scheduleSyncDelayedTask(Factions.get(), this, EngineDynmap.this.config.updatePeriodMS);
				}
			} else if(pending == this) {
				pending = null;
			}
		}
	}
	
	private class RBlock {
		private int x;
		private int z;
	}
	
	private class RBlocks {
		private Map<String, LinkedList<RBlock>> blocks = new HashMap<String, LinkedList<RBlock>>();
	}
	
	
	private static EngineDynmap i = new EngineDynmap();
	public static EngineDynmap get() { return i; }
	private EngineDynmap() {}
	
	private final String markerSetName = "factions.markerset";
	private DynmapConfig config;
	private Plugin dynmap;
	private DynmapAPI api;
	private MarkerAPI markerAPI;
	private MarkerSet markerSet;
	private boolean run = false;
	private Updater pending = null;
	private AreaStyle defaultStyle = new AreaStyle();
	private int blocksize = 16;
	
	private Map<String, AreaMarker> resAreas = new HashMap<String, AreaMarker>();
	private Map<String, Marker> resMark = new HashMap<String, Marker>();
	
	public void activate() {
		DynmapConfig.get().load();
		config = DynmapConfig.get();
		
		dynmap = Bukkit.getPluginManager().getPlugin("dynmap");
		if(dynmap == null) {
			Factions.get().log(Txt.parse("<bad>Could not get dynmap"));
			return;
		}
		
		if(dynmap instanceof DynmapAPI) {
			api = (DynmapAPI)dynmap;
		} else {
			Factions.get().log(Txt.parse("<bad>Dynmap plugin is not a instance of the dynmap api"));
			return;
		}
		
		markerAPI = api.getMarkerAPI();
		if(markerAPI == null) {
			Factions.get().log(Txt.parse("<bad>Could not get marker api"));
			return;
		}
		
		markerSet = markerAPI.getMarkerSet(markerSetName);
		if(markerSet == null) {
			markerSet = markerAPI.createMarkerSet(markerSetName, config.regionLayerName, null, false);
		} else {
			markerSet.setMarkerSetLabel(config.regionLayerName);
		}
		if(markerSet == null) {
			Factions.get().log(Txt.parse("<bad>Could not create marker set"));
		}
		
		resAreas.clear();
		resMark.clear();
		
		int minZoom = config.minZoom;
		if(minZoom > 0) {
			markerSet.setMinZoom(minZoom);
		}
		
		markerSet.setLayerPriority(config.layerPriority);
		markerSet.setHideByDefault(config.layerHidenByDefault);
		
		run = true;
		Bukkit.getScheduler().scheduleSyncDelayedTask(Factions.get(), new Updater(), 40);
		
		Bukkit.getPluginManager().registerEvents(this, Factions.get());
	}
	
	public void deactivate() {
		if(markerSet != null) {
			markerSet.deleteMarkerSet();
			markerSet = null;
		}
		
		resAreas.clear();
		
		run = false;
		HandlerList.unregisterAll(this);
	}
	
	private void requestUpdate() {
		Factions.get().log(Txt.parse("<i>Requesing Dynmap Update"));
		if(pending == null) {
			Updater update = new Updater();
			update.runOnce = true;
			pending = update;
			Bukkit.getScheduler().scheduleSyncDelayedTask(Factions.get(), update, 20);
		}
	}
	
	private void updateRegions() {
		Factions.get().log(Txt.parse("<i>Stating Dynmap Update"));
		Map<String, AreaMarker> newMap = new HashMap<String, AreaMarker>();
		Map<String, Marker> newMark = new HashMap<String, Marker>();
		
		Map<String, RBlocks> blocksByRegion = new HashMap<String, EngineDynmap.RBlocks>();
		
		for(RegionCollection rc : RegionCollections.get().getColls()) {
			Collection<Region> regions = rc.getAll();
			for(Region region : regions) {
				Set<PS> chunks = RegionBoardCollections.get().getChunks(region);
				String rId = rc.getUniverse() + "_" + region.getId();
				RBlocks rBlocks = blocksByRegion.get(rId);
				if(rBlocks == null) {
					rBlocks = new RBlocks();
					blocksByRegion.put(rId, rBlocks);
				}
				
				for(PS chunk : chunks) {
					String world = chunk.getWorld();
					LinkedList<RBlock> blocks = rBlocks.blocks.get(world);
					if(blocks == null) {
						blocks = new LinkedList<RBlock>();
						rBlocks.blocks.put(world, blocks);
					}
					RBlock rB = new RBlock();
					rB.x = chunk.getChunkX();
					rB.z = chunk.getChunkZ();
					blocks.add(rB);
				}
			}
			
			for(Region region : regions) {
				String regionName = ChatColor.stripColor(region.getName());
				String rId = rc.getUniverse() + "_" + region.getId();
				RBlocks rBlocks = blocksByRegion.get(rId);
				
				for(Map.Entry<String, LinkedList<RBlock>> worldBlocks : rBlocks.blocks.entrySet()) {
					/*
					 * Handle faction here for a world
					 */
					this.handleFactionOnWorld(regionName, region, worldBlocks.getKey(), worldBlocks.getValue(), newMap, newMark);
				}
				rBlocks.blocks.clear();
			}
		}
		
		blocksByRegion.clear();
		for(AreaMarker oldM : resAreas.values()) {
			oldM.deleteMarker();
		}
		
		for(Marker oldM : resMark.values()) {
			oldM.deleteMarker();
		}
		resAreas = newMap;
		resMark = newMark;
	}
	
	private void handleFactionOnWorld(String regionName, Region region, String world, LinkedList<RBlock> blocks, Map<String, AreaMarker> newMap, Map<String, Marker> newMark) {
		double[] x = null;
		double[] z = null;
		int polyIndex = 0;
		String desc = this.formatInfoWindow(region);

		if (isVisable(regionName, world)) {
			if (blocks.isEmpty()) return;
			LinkedList<RBlock> nodeVals = new LinkedList<RBlock>();
			TileFlags curblks = new TileFlags();
			for (RBlock b : blocks) {
				curblks.setFlag(b.x, b.z, true);
				nodeVals.addLast(b);
			}

			while (nodeVals != null) {
				LinkedList<RBlock> ourNodes = null;
				LinkedList<RBlock> newList = null;
				TileFlags ourBlocks = null;
				int minX = Integer.MAX_VALUE;
				int minZ = Integer.MAX_VALUE;
				for (RBlock node : nodeVals) {
					int nodeX = node.x;
					int nodeZ = node.z;
					if ((ourBlocks == null) && curblks.getFlag(nodeX, nodeZ)) {
						ourBlocks = new TileFlags();
						ourNodes = new LinkedList<EngineDynmap.RBlock>();
						this.floodFillTarget(curblks, ourBlocks, nodeX, nodeZ);
						ourNodes.add(node);
						minX = nodeX;
						minZ = nodeZ;
					} else if ((ourBlocks != null)
							&& ourBlocks.getFlag(nodeX, nodeZ)) {
						ourNodes.add(node);
						if (nodeX < minX) {
							minX = nodeX;
							minZ = nodeZ;
						} else if ((nodeX == minX) && (nodeZ < minZ)) {
							minZ = nodeZ;
						}
					} else {
						if (newList == null) newList = new LinkedList<EngineDynmap.RBlock>();
						newList.add(node);
					}
				}
				nodeVals = newList;
				if (ourBlocks != null) {
					int initX = minX;
					int initZ = minZ;
					int curX = minX;
					int curZ = minZ;
					Direction dir = Direction.XPLUS;
					ArrayList<int[]> lineList = new ArrayList<int[]>();
					lineList.add(new int[] { initX, initZ });
					while ((curX != initX) || (curZ != initZ) || (dir != Direction.ZMINUS)) {
						switch (dir) {
						case XPLUS: /* Segment in X+ direction */
							if (!ourBlocks.getFlag(curX + 1, curZ)) { 
								lineList.add(new int[] { curX + 1, curZ });
								dir = Direction.ZPLUS; /* Change direction */
							} else if (!ourBlocks.getFlag(curX + 1, curZ - 1)) { 
								curX++;
							} else { /* Left turn */
								lineList.add(new int[] { curX + 1, curZ });
								dir = Direction.ZMINUS;
								curX++;
								curZ--;
							}
							break;
						case ZPLUS: /* Segment in Z+ direction */
							if (!ourBlocks.getFlag(curX, curZ + 1)) { 
								lineList.add(new int[] { curX + 1, curZ + 1 }); 
								dir = Direction.XMINUS; /* Change direction */
							} else if (!ourBlocks.getFlag(curX + 1, curZ + 1)) { 
								curZ++;
							} else { /* Left turn */
								lineList.add(new int[] { curX + 1, curZ + 1 }); 
								dir = Direction.XPLUS;
								curX++;
								curZ++;
							}
							break;
						case XMINUS: /* Segment in X- direction */
							if (!ourBlocks.getFlag(curX - 1, curZ)) { 
								lineList.add(new int[] { curX, curZ + 1 }); 
								dir = Direction.ZMINUS; /* Change direction */
							} else if (!ourBlocks.getFlag(curX - 1, curZ + 1)) {
								curX--;
							} else { /* Left turn */
								lineList.add(new int[] { curX, curZ + 1 }); 
								dir = Direction.ZPLUS;
								curX--;
								curZ++;
							}
							break;
						case ZMINUS: /* Segment in Z- direction */
							if (!ourBlocks.getFlag(curX, curZ - 1)) { 
								lineList.add(new int[] { curX, curZ }); 
								dir = Direction.XPLUS; /* Change direction */
							} else if (!ourBlocks.getFlag(curX - 1, curZ - 1)) {
								curZ--;
							} else { /* Left turn */
								lineList.add(new int[] { curX, curZ });
								dir = Direction.XMINUS;
								curX--;
								curZ--;
							}
							break;
						}
					}
					/* Build information for specific area */
					String polyid = regionName + "__" + world + "__"
							+ polyIndex;
					int sz = lineList.size();
					x = new double[sz];
					z = new double[sz];
					for (int i = 0; i < sz; i++) {
						int[] line = lineList.get(i);
						x[i] = (double) line[0] * (double) blocksize;
						z[i] = (double) line[1] * (double) blocksize;
					}
					/* Find existing one */
					//Factions.get().log(Txt.parse("<i>Res Area <v>" + resAreas.toString()));
					AreaMarker m = resAreas.remove(polyid); /* Existing area? */
					if (m == null) {
						m = markerSet.createAreaMarker(polyid, regionName,
								false, world, x, z, false);
						if (m == null) {
							Factions.get().log(Txt.parse("<bad>Error adding area marker <v>" + polyid));
							return;
						}
					} else {
						m.setCornerLocations(x, z); /*
													 * Replace corner locations
													 */
						m.setLabel(regionName); /* Update label */
					}
					m.setDescription(desc); /* Set popup */
					/* Set line and fill properties */
					addStyle(regionName, m, region);

					/* Add to map */
					newMap.put(polyid, m);
					polyIndex++;

				}
			}
		}
	}
	
	 /**
     * Find all contiguous blocks, set in target and clear in source
     */
    private int floodFillTarget(TileFlags src, TileFlags dest, int x, int y) {
        int cnt = 0;
        ArrayDeque<int[]> stack = new ArrayDeque<int[]>();
        stack.push(new int[] { x, y });
        
        while(stack.isEmpty() == false) {
            int[] nxt = stack.pop();
            x = nxt[0];
            y = nxt[1];
            if(src.getFlag(x, y)) { /* Set in src */
                src.setFlag(x, y, false);   /* Clear source */
                dest.setFlag(x, y, true);   /* Set in destination */
                cnt++;
                if(src.getFlag(x+1, y))
                    stack.push(new int[] { x+1, y });
                if(src.getFlag(x-1, y))
                    stack.push(new int[] { x-1, y });
                if(src.getFlag(x, y+1))
                    stack.push(new int[] { x, y+1 });
                if(src.getFlag(x, y-1))
                    stack.push(new int[] { x, y-1 });
            }
        }
        return cnt;
    }
    
    private void addStyle(String resid, AreaMarker m, Region region) {
        AreaStyle as = this.defaultStyle;
        
        int sc = 0x0000FF;
        int fc = 0x0000FF;
      
        if(region.isOwned()) {
        	sc = 0xFF0000;
        	fc = 0xFF0000;
        } else if(region.isClaimable()) {
        	sc = 0x00FF00;
        	fc = 0x00FF00;
        }
    
        m.setLineStyle(as.strokeweight, as.strokeopacity, sc);
        m.setFillStyle(as.fillopacity, fc);
        m.setBoostFlag(as.boost);
    }
	
	private String formatInfoWindow(Region region) {
		String infowindow = "<div class=\"infowindow\"><span style=\"font-size:120%;\">%regionname%</span><br />Flags<br /><span style=\"font-weight:bold;\">%flags%</span></div>";
		String v = "<div class=\"regioninfo\">"+infowindow+"</div>";
		v = v.replace("%regionname%", ChatColor.stripColor(region.getName()));
		String flags = "Open: " + region.isClaimable();
		flags += "<br/>" + "Owner: " + ChatColor.stripColor(region.getFactionName());
		v = v.replace("%flags%", flags);
		Factions.get().log(v);
		return v;
	}
	
	private boolean isVisable(String id, String world) {
		return true;
	}
	
	/*
	 * Listeners for regions
	 */
	@EventHandler
	public void onRegionChange(FactionsEventRegionChanged event) {
		this.handleEvent(event);
	}

	@EventHandler
	public void onRegionCreate(FactionsEventRegionCreate event) {
		this.handleEvent(event);
	}
	
	@EventHandler
	public void onRegionDisolve(FactionsEventRegionDestroy event) {
		this.handleEvent(event);
	}
	
	@EventHandler
	public void onRegionChunkChange(FactionsEventRegionChunkChange event) {
		this.handleEvent(event);
	}
	
	public void onRegionClaim(FactionsEventRegionClaim event) {
		this.handleEvent(event);
	}
	
	private void handleEvent(EventMassiveCore event) {
		if(event.isCancelled()) return;
		this.requestUpdate();
	}
	
}
