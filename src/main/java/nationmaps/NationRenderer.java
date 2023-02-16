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
				Color red = new Color(220, 0, 0), green = new Color(0, 220, 0), blue = new Color(0, 0, 220);
				if(playerNation == null) {
					Color darkBright = color;
					if(color.getBlue() >= 220) darkBright = new Color(color.getRed(), color.getGreen()+(color.getBlue()/2), color.getBlue());
					else if(color.getBlue() <= 180) darkBright = color.brighter();
					else darkBright = color;
					
					newCol = blend(darkBright, blue);
				}
				else if(nation.getNationID() == playerNation.getNationID() || mappingRepo.getAllianceNationsByNationID(nation.getNationID()).contains(playerNation)) {
					Color darkBright;
					if(color.getGreen() >= 240) darkBright = color.darker();
					else if(color.getGreen() <= 200) darkBright = color.brighter();
					else darkBright = color;
					
					newCol = blend(darkBright, green);
					
				}
				else if(mappingRepo.getWarNationsByNationID(nation.getNationID()).contains(playerNation)) {
					Color darkBright;
					if(color.getRed() >= 240) darkBright = color.darker();
					else if(color.getRed() <= 200) darkBright = color.brighter();
					else darkBright = color;
					
					newCol = blend(darkBright, red);
				}
				else {
					Color darkBright = color;
					if(color.getBlue() >= 220 && color.getGreen() <= 145) darkBright = new Color(color.getRed(), color.getGreen()+(color.getBlue()/2), color.getBlue());
					else if(color.getBlue() <= 180) darkBright = color.brighter();
					else darkBright = color;
					
					newCol = blend(darkBright, blue);
				}
				canvas.setPixelColor(j, i, newCol);	
			}
		}
	}

	public static Color blend(Color c0, Color c1) {
	    double totalAlpha = c0.getAlpha() + c1.getAlpha();
	    double weight0 = c0.getAlpha() / totalAlpha;
	    double weight1 = c1.getAlpha() / totalAlpha;

	    double r = weight0 * c0.getRed() + weight1 * c1.getRed();
	    double g = weight0 * c0.getGreen() + weight1 * c1.getGreen();
	    double b = weight0 * c0.getBlue() + weight1 * c1.getBlue();
	    double a = Math.max(c0.getAlpha(), c1.getAlpha());

	    return new Color((int) r, (int) g, (int) b, (int) a);
	  }
}
