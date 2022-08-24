package nationmaps;

import java.awt.Color;
import java.util.HashSet;
import java.util.Set;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

import main.Main;
import persistency.MappingRepository;
import persistency.NationMapping;

public class NationRenderer extends MapRenderer{
	private Set<Integer> noRender = new HashSet<Integer>();
	
	@Override
	public void render(MapView map, MapCanvas canvas, Player player) {
		if(noRender.contains(map.getId())) return;
		noRender.add(map.getId());
		
		addChunkBorders(canvas, player);
	}
	
	public void addChunkBorders(MapCanvas canvas, Player player) {
		Location loc = player.getLocation();
		
		MappingRepository mappingRepo = Main.getInstance().getMappingRepo();
		int startX = loc.getChunk().getX()-5, startZ = loc.getChunk().getZ()-5;
		
		Chunk playChunk = loc.getChunk();
		int distanceToChunkX = loc.getBlockX() - playChunk.getBlock(0, 0, 0).getX();
		int distanceToChunkZ = loc.getBlockZ() - playChunk.getBlock(0, 0, 0).getZ();
		
		for(int i = 0; i < 10; i++) {
			for(int j = 0; j < 10; j++) {
				Chunk chunk = loc.getWorld().getChunkAt(startX + j, startZ + i);
				NationMapping nation = mappingRepo.getNationByChunk(chunk);
				if(nation == null) continue;
				
				int startChunkZ = ((i-1) * 16) - distanceToChunkZ, startChunkX = ((j-1) * 16) - distanceToChunkX;
				int endChunkZ = startChunkZ + 15, endChunkX = startChunkX + 15;
				
				
				setBorder(canvas, player, nation, startChunkX, endChunkX, startChunkZ, endChunkZ);
				
				
			}
		}
	}
	
	private void setBorder(MapCanvas canvas, Player player, NationMapping nation, int startX, int endX, int startZ, int endZ) {
		for(int i = startZ; i <= endZ; i++) {
			for(int j = startX; j <= endX; j++) {
				Color color = canvas.getBasePixelColor(j, i);
				
				MappingRepository mappingRepo = Main.getInstance().getMappingRepo();
				NationMapping playerNation = mappingRepo.getNationByID(mappingRepo.getPlayerByUUID(player.getUniqueId()).getNationID());
				
				Color newCol = new Color(color.getRed(), color.getGreen(), color.getBlue());
				if(playerNation == null) {
					switch(player.getWorld().getEnvironment()) {
					case NORMAL:
						newCol = new Color(color.getRed(), color.getBlue() > 150 ? 60 : color.getGreen(), color.getBlue() < 100 ? color.getBlue()+155 : color.getBlue());
						break;
					case NETHER:
						newCol = new Color(color.getRed(), color.getGreen(), 150);
						break;
					case THE_END:
						newCol = new Color(color.getRed() > 100 ? color.getRed()-100 : color.getRed(), color.getGreen() > 100 ? color.getGreen()-100 : color.getGreen(), color.getBlue() < 200 && color.getBlue() > 100 ? 190 : 200);
						break;
					default:
						newCol = new Color(color.getRed(), color.getBlue() > 150 ? 100 : color.getGreen(), color.getBlue() < 100 ? color.getBlue()+155 : color.getBlue());
						break;
					}
				}
				else if(nation.getNationID() == playerNation.getNationID() || mappingRepo.getAllianceNationsByNationID(nation.getNationID()).contains(playerNation)) {
					switch(player.getWorld().getEnvironment()) {
					case NORMAL:
						newCol = new Color(color.getRed(), color.getGreen() < 180 ? color.getGreen()+76 : color.getGreen(), color.getGreen() < 100 && color.getBlue() > 200 ? color.getBlue()-150 : color.getBlue());
						break;
					case NETHER:
						newCol = new Color(color.getRed(), color.getGreen() < 150 ? color.getGreen()+56 : color.getGreen(), color.getBlue());
						break;
					case THE_END:
						newCol = new Color(color.getRed() > 100 ? color.getRed()-100 : color.getRed(), color.getGreen() < 200 && color.getGreen() > 100 ? 190 : 200, color.getBlue() > 100 ? color.getBlue()-100 : color.getBlue());
						break;
					default:
						newCol = new Color(color.getRed(), color.getGreen() < 180 ? color.getGreen()+76 : color.getGreen(), color.getGreen() < 100 && color.getBlue() > 200 ? color.getBlue()-150 : color.getBlue());
						break;
					}
				}
				else if(mappingRepo.getWarNationsByNationID(nation.getNationID()).contains(playerNation)) {
					switch(player.getWorld().getEnvironment()) {
					case THE_END:
						newCol = new Color(color.getRed() < 200 && color.getRed() > 100 ? 190 : 200, color.getGreen() > 100 ? color.getGreen()-100 : color.getGreen(), color.getBlue() > 100 ? color.getBlue()-100 : color.getBlue());
						break;
					default:
						newCol = new Color(color.getRed() < 150 ? color.getRed()+106 : color.getRed(), color.getGreen(), color.getBlue());
						break;
					}
				}
				else {
					switch(player.getWorld().getEnvironment()) {
					case NORMAL:
						newCol = new Color(color.getRed(), color.getBlue() > 150 ? 100 : color.getGreen(), color.getBlue() < 100 ? color.getBlue()+155 : color.getBlue());
						break;
					case NETHER:
						newCol = new Color(color.getRed(), color.getGreen(), 150);
						break;
					case THE_END:
						newCol = new Color(color.getRed() > 100 ? color.getRed()-100 : color.getRed(), color.getGreen() > 100 ? color.getGreen()-100 : color.getGreen(), color.getBlue() < 200 && color.getBlue() > 100 ? 190 : 200);
						break;
					default:
						newCol = new Color(color.getRed(), color.getBlue() > 150 ? 100 : color.getGreen(), color.getBlue() < 100 ? color.getBlue()+155 : color.getBlue());
						break;
					}
				}
				canvas.setPixelColor(j, i, newCol);	
			}
		}
	}

}
