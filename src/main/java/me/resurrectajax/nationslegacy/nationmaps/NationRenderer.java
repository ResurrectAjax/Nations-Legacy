package me.resurrectajax.nationslegacy.nationmaps;

import java.awt.Color;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

import me.resurrectajax.nationslegacy.main.Nations;
import me.resurrectajax.nationslegacy.persistency.MappingRepository;
import me.resurrectajax.nationslegacy.persistency.NationMapping;

public class NationRenderer extends MapRenderer{
	private Set<Integer> noRender = new HashSet<Integer>();
	private Set<Pixel> pixels = new HashSet<Pixel>();
	
	@Override
	public void render(MapView map, MapCanvas canvas, Player player) {
		if(noRender.contains(map.getId())) return;
		noRender.add(map.getId());
		
		addChunkBorders(canvas, player);
	}
	
	public void addChunkBorders(MapCanvas canvas, Player player) {
		Location loc = player.getLocation();
		
		MappingRepository mappingRepo = Nations.getInstance().getMappingRepo();
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
				
				
				markTerritory(canvas, player, nation, startChunkX, endChunkX, startChunkZ, endChunkZ);
				
				
			}
		}
		
		for(Pixel pixelA : pixels) {
			if(isBorder(pixelA)) canvas.setPixelColor(pixelA.x, pixelA.z, pixelA.color);
		}
	}
	
	private void markTerritory(MapCanvas canvas, Player player, NationMapping nation, int startX, int endX, int startZ, int endZ) {
		for(int i = startZ; i <= endZ; i++) {
			for(int j = startX; j <= endX; j++) {
				Stance stance;
				
				Color color = canvas.getBasePixelColor(j, i);
				
				MappingRepository mappingRepo = Nations.getInstance().getMappingRepo();
				NationMapping playerNation = mappingRepo.getNationByID(mappingRepo.getPlayerByUUID(player.getUniqueId()).getNationID());
				
				Color newCol = new Color(color.getRed(), color.getGreen(), color.getBlue());
				Color red = new Color(220, 0, 0), green = new Color(0, 220, 0), blue = new Color(0, 0, 220);
				if(playerNation == null) {
					Color darkBright = color;
					if(color.getBlue() >= 220 && color.getGreen() <= 145) darkBright = new Color(color.getRed(), color.getGreen()+(color.getBlue()/2), color.getBlue());
					else if(color.getBlue() <= 180) darkBright = color.brighter();
					else darkBright = color;
					
					newCol = blend(darkBright, blue);
					stance = Stance.NEUTRAL;
				}
				else if(nation.getNationID() == playerNation.getNationID() || mappingRepo.getAllianceNationsByNationID(nation.getNationID()).contains(playerNation)) {
					Color darkBright;
					if(color.getGreen() >= 240) darkBright = color.darker();
					else if(color.getGreen() <= 200) darkBright = color.brighter();
					else darkBright = color;
					
					newCol = blend(darkBright, green);
					if(nation.getNationID() == playerNation.getNationID()) stance = Stance.SELF;
					else stance = Stance.ALLY;;
				}
				else if(mappingRepo.getWarNationsByNationID(nation.getNationID()).contains(playerNation)) {
					Color darkBright;
					if(color.getRed() >= 240) darkBright = color.darker();
					else if(color.getRed() <= 200) darkBright = color.brighter();
					else darkBright = color;
					
					newCol = blend(darkBright, red);
					stance = Stance.ENEMY;
				}
				else {
					Color darkBright = color;
					if(color.getBlue() >= 220 && color.getGreen() <= 145) darkBright = new Color(color.getRed(), color.getGreen()+(color.getBlue()/2), color.getBlue());
					else if(color.getBlue() <= 180) darkBright = color.brighter();
					else darkBright = color;
					
					newCol = blend(darkBright, blue);
					stance = Stance.NEUTRAL;
				}
				canvas.setPixelColor(j, i, newCol);
				
				if(j == startX || j == endX || i == startZ || i == endZ) pixels.add(new Pixel(j, i, stance));
				else pixels.add(new Pixel(j, i, stance, newCol));
			}
		}
	}
	
	private boolean isBorder(Pixel pixel) {
		List<Pixel> pixels = Arrays.asList(
				getPixelByCoordinates(pixel.x-1, pixel.z),
				getPixelByCoordinates(pixel.x, pixel.z-1),
				getPixelByCoordinates(pixel.x+1, pixel.z),
				getPixelByCoordinates(pixel.x, pixel.z+1)
				);
		
		for(Pixel pixelA : pixels) {
			if(pixelA == null) return true;
			if(!pixelA.stance.equals(pixel.stance)) return true;
		}
		return false;
	}
	
	private Pixel getPixelByCoordinates(int x, int z) {
		return pixels.stream().filter(el -> el.x == x && el.z == z).findAny().orElse(null);
	}
	
	private class Pixel {
		private int x, z;
		private Stance stance;
		private Color color;
		
		public Pixel(int x, int z, Stance stance, Color color) {
			this.x = x;
			this.z = z;
			this.stance = stance;
		}
		
		public Pixel(int x, int z, Stance stance) {
			this.x = x;
			this.z = z;
			this.stance = stance;
			setBorderColor(stance);
		}
		
		private void setBorderColor(Stance stance) {
			switch(stance) {
			case SELF:
			case ALLY:
				this.color = Color.GREEN.darker();
				break;
			case ENEMY:
				this.color = Color.RED.darker();
				break;
			case NEUTRAL:
				this.color = Color.BLUE.darker();
				break;
			}
		}
	}
	
	private enum Stance {
		SELF,
		ALLY,
		ENEMY,
		NEUTRAL
	}

	private static Color blend(Color c0, Color c1) {
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
