package listeners;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import events.nation.claim.ClaimChunkEvent;
import events.nation.claim.UnclaimChunkEvent;
import main.Main;
import general.GeneralMethods;
import persistency.MappingRepository;
import net.md_5.bungee.api.ChatColor;
import persistency.NationMapping;
import persistency.PlayerMapping;

public class PlayerMoveListener implements Listener{
	private Main main;
	private HashMap<UUID, Chunk> lastChunk = new HashMap<UUID, Chunk>();
	private HashMap<UUID, Integer> lastNation = new HashMap<UUID, Integer>();
	public PlayerMoveListener(Main main) {
		this.main = main;
	}
	
	@EventHandler
	public void playerMoveEvent(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		
		
		if(event.getFrom().getX() == event.getTo().getX() && event.getFrom().getZ() == event.getTo().getZ()) return;
		
		Chunk chunk = player.getLocation().getChunk();
		lastChunk.put(player.getUniqueId(), chunk);
		
		MappingRepository mappingRepo = main.getMappingRepo();
		
		NationMapping chunkNation = mappingRepo.getNationByChunk(chunk);
		
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
			
			if(main.getMappingRepo().getClaimingSet().contains(player.getUniqueId())) {
				if(nationMap.getClaimedChunks().contains(chunk)) return;
				
				Bukkit.getPluginManager().callEvent(new ClaimChunkEvent(nationMap, player, chunk));	
			}
			
			if(lastNation.containsKey(player.getUniqueId()) && lastNation.get(player.getUniqueId()) == null) return;
			lastNation.put(player.getUniqueId(), null);
			
			FileConfiguration config = main.getConfig();
			player.sendTitle(GeneralMethods.format((OfflinePlayer) player, config.getString("Wilderness.Name"), player.getName()), GeneralMethods.format((OfflinePlayer) player, config.getString("Wilderness.Description"), player.getName()), 10, 40, 10);
		}
	}
}
