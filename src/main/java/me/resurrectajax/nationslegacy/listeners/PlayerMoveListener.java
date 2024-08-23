package me.resurrectajax.nationslegacy.listeners;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import me.resurrectajax.nationslegacy.events.nation.claim.ClaimAutoFillEvent;
import me.resurrectajax.nationslegacy.events.nation.claim.ClaimChunkEvent;
import me.resurrectajax.nationslegacy.events.nation.claim.UnclaimChunkEvent;
import me.resurrectajax.nationslegacy.general.GeneralMethods;
import me.resurrectajax.nationslegacy.main.Nations;
import me.resurrectajax.nationslegacy.persistency.MappingRepository;
import me.resurrectajax.nationslegacy.persistency.NationMapping;
import me.resurrectajax.nationslegacy.persistency.PlayerMapping;
import net.md_5.bungee.api.ChatColor;

public class PlayerMoveListener implements Listener{
	private MappingRepository mappingRepo;
	private Nations main;
	private HashMap<UUID, Chunk> lastChunk = new HashMap<UUID, Chunk>();
	private HashMap<UUID, Integer> lastNation = new HashMap<UUID, Integer>();
	public PlayerMoveListener(Nations main) {
		this.main = main;
		this.mappingRepo = main.getMappingRepo();
	}
	
	@EventHandler
	public void playerMoveEvent(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		
		
		if(event.getFrom().getX() == event.getTo().getX() && event.getFrom().getZ() == event.getTo().getZ()) return;
		
		Chunk chunk = player.getLocation().getChunk();
		NationMapping chunkNation = mappingRepo.getNationByChunk(chunk);
		
		if(lastChunk.containsKey(player.getUniqueId()) && lastChunk.get(player.getUniqueId()).equals(chunk) && chunkNation != null) return;
		Chunk last = lastChunk.get(player.getUniqueId());
		lastChunk.put(player.getUniqueId(), chunk);
		
		PlayerMapping playerMap = mappingRepo.getPlayerByUUID(player.getUniqueId());
		NationMapping nationMap = mappingRepo.getNationByPlayer(playerMap);
		if(chunkNation != null) {
			if(main.getMappingRepo().getUnclaimingSet().contains(player.getUniqueId())) {
				if(!nationMap.getClaimedChunks().contains(chunk)) return;
				
				Bukkit.getPluginManager().callEvent(new UnclaimChunkEvent(nationMap, player, chunk));
			}
			
			if(lastNation.containsKey(player.getUniqueId()) && lastNation.get(player.getUniqueId()) != null && lastNation.get(player.getUniqueId()) == chunkNation.getNationID()) return;
			lastNation.put(player.getUniqueId(), chunkNation.getNationID());
			
			String color;
			if(nationMap == null) color = "&9&l";
			else if(chunkNation == nationMap || mappingRepo.getAllianceNationsByNationID(nationMap.getNationID()).contains(chunkNation)) color = "&a&l";
			else if (mappingRepo.getWarNationsByNationID(nationMap.getNationID()).contains(chunkNation)) color = "&c&l";
			else color = "&9&l";
			
			player.sendTitle(ChatColor.translateAlternateColorCodes('&',color + chunkNation.getName()), GeneralMethods.format((OfflinePlayer) player, chunkNation.getDescription(), player.getName()), 10, 40, 10);	
			return;
		}
		else {
			List<Chunk> neighbouringChunks = new ArrayList<>(Arrays.asList(
					chunk.getWorld().getChunkAt(chunk.getX()-1, chunk.getZ()),
					chunk.getWorld().getChunkAt(chunk.getX()+1, chunk.getZ()),
					chunk.getWorld().getChunkAt(chunk.getX(), chunk.getZ()-1),
					chunk.getWorld().getChunkAt(chunk.getX(), chunk.getZ()+1)
					));
			neighbouringChunks.removeIf(el -> el.equals(last));
			
			if(main.getMappingRepo().getClaimingSet().contains(player.getUniqueId())) {
				if(nationMap.getClaimedChunks().contains(chunk)) return;
				
				if(neighbouringChunks.stream().anyMatch(el -> mappingRepo.getNationByChunk(el) != null 
						&& mappingRepo.getNationByChunk(el).equals(nationMap)) 
						&& shouldFill(nationMap, chunk)) {
					
					Bukkit.getPluginManager().callEvent(new ClaimAutoFillEvent(nationMap, player, chunk));
				}
				else Bukkit.getPluginManager().callEvent(new ClaimChunkEvent(nationMap, player, chunk));
			}
			
			if(lastNation.containsKey(player.getUniqueId()) && lastNation.get(player.getUniqueId()) == null) return;
			lastNation.put(player.getUniqueId(), null);
			
			FileConfiguration config = main.getConfig();
			player.sendTitle(GeneralMethods.format((OfflinePlayer) player, config.getString("Wilderness.Name"), player.getName()), GeneralMethods.format((OfflinePlayer) player, config.getString("Wilderness.Description"), player.getName()), 10, 40, 10);
		}
	}
	
	private boolean shouldFill(NationMapping nation, Chunk chunk) {
		List<Chunk> neighbouringChunks = new ArrayList<>(Arrays.asList(
				chunk.getWorld().getChunkAt(chunk.getX()-1, chunk.getZ()),
				chunk.getWorld().getChunkAt(chunk.getX()+1, chunk.getZ()),
				chunk.getWorld().getChunkAt(chunk.getX(), chunk.getZ()-1),
				chunk.getWorld().getChunkAt(chunk.getX(), chunk.getZ()+1)
				));
		List<Chunk> diagonalChunks = new ArrayList<>(Arrays.asList(
				chunk.getWorld().getChunkAt(chunk.getX()-1, chunk.getZ()-1),
				chunk.getWorld().getChunkAt(chunk.getX()+1, chunk.getZ()-1),
				chunk.getWorld().getChunkAt(chunk.getX()-1, chunk.getZ()+1),
				chunk.getWorld().getChunkAt(chunk.getX()+1, chunk.getZ()+1)
				));
		
		List<Chunk> emptyNeighbors = neighbouringChunks.stream().filter(el -> {
			NationMapping chunkNation = mappingRepo.getNationByChunk(el);
			return chunkNation == null || !chunkNation.equals(nation);
		}).collect(Collectors.toList());
		List<Chunk> emptyDiagonals = diagonalChunks.stream().filter(el -> {
			NationMapping chunkNation = mappingRepo.getNationByChunk(el);
			return chunkNation == null || !chunkNation.equals(nation);
		}).collect(Collectors.toList());
		
		if(emptyNeighbors.size() == 2) {
			if(Math.abs(emptyNeighbors.get(0).getX()-emptyNeighbors.get(1).getX()) == 2 || Math.abs(emptyNeighbors.get(0).getZ()-emptyNeighbors.get(1).getZ()) == 2) return true;
		}
		if(emptyDiagonals.size() >= 2) {
			for(Chunk one : emptyDiagonals) {
				for(Chunk two : emptyDiagonals) {
					if(one.equals(two)) continue;
					if(Math.abs(one.getX()-two.getX()) == 2 && Math.abs(one.getZ()-two.getZ()) == 2) return true;
				}
			}
		}
		if(emptyNeighbors.size() == 1 && emptyDiagonals.size() == 1) return true;
		
		return false;
	}
}
