package me.resurrectajax.nationslegacy.listeners;

import java.util.Set;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import me.resurrectajax.nationslegacy.chunkgain.ChunkGainManager;
import me.resurrectajax.nationslegacy.events.ReloadEvent;
import me.resurrectajax.nationslegacy.general.GeneralMethods;
import me.resurrectajax.nationslegacy.main.Nations;
import me.resurrectajax.nationslegacy.persistency.MappingRepository;
import me.resurrectajax.nationslegacy.persistency.NationMapping;
import me.resurrectajax.nationslegacy.persistency.PlayerMapping;

public class JoinLeaveListener implements Listener{

	private Nations main;
	private MappingRepository mappingRepo;
	private ChunkGainManager chunkManager;
	private FileConfiguration config;
	private int limit = 0;
	
	public JoinLeaveListener(Nations main) {
		this.main = main;
		this.mappingRepo = main.getMappingRepo();
		this.chunkManager = mappingRepo.getChunkGainManager();
		
		reload();
	}
	
	private void reload() {
		this.config = main.getConfig();
		limit = config.getInt("Nations.Claiming.ChunkGain.Limit");
		
		mappingRepo.getNations().forEach(el -> chunkManager.removeChunkGain(el.getNationID()));
		
		Set<NationMapping> activeNations = mappingRepo.getNations().stream()
			.filter(nation -> nation.getPlayers().stream()
					.filter(el -> Bukkit.getPlayer(el.getUUID()) != null)
					.findAny()
					.orElse(null)
					!= null).collect(Collectors.toSet());
		
		activeNations.forEach(el -> {
			if(el.getGainedChunks() < limit || limit == -1) chunkManager.addChunkGain(el);
		});
	}
	
	@EventHandler
	public void onReload(ReloadEvent event) {
		reload();
	}
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		limit = config.getInt("Nations.Claiming.ChunkGain.Limit");
		
		mappingRepo = main.getMappingRepo();
		
		Player player = event.getPlayer();
		if(mappingRepo.getPlayerByUUID(player.getUniqueId()) == null) mappingRepo.addPlayer(player);
		
		main.reloadPermissions(player);
		
		mappingRepo.getScoreboardManager().updateScoreboard(player);
		
		PlayerMapping playerMap = mappingRepo.getPlayerByUUID(player.getUniqueId());
		
		GeneralMethods.updatePlayerTab(player);
		if(playerMap.getNationID() == null) return;
		
		NationMapping nation = mappingRepo.getNationByID(playerMap.getNationID());
		if(nation.getPlayers().stream().filter(el -> Bukkit.getPlayer(el.getUUID()) != null).collect(Collectors.toSet()).size() != 1) return;
		
		if(nation.getGainedChunks() < limit || limit == -1) chunkManager.addChunkGain(nation);
	}
	
	@EventHandler
	public void onPlayerLeave(PlayerQuitEvent event) {
		mappingRepo = main.getMappingRepo();
		
		PlayerMapping playerMap = mappingRepo.getPlayerByUUID(event.getPlayer().getUniqueId());
		if(playerMap.getNationID() == null) return;
		
		NationMapping nation = mappingRepo.getNationByID(playerMap.getNationID());
		if(nation.getPlayers().stream().filter(el -> Bukkit.getPlayer(el.getUUID()) != null).collect(Collectors.toSet()).size() != 1) return;
		
		chunkManager.removeChunkGain(nation.getNationID());
	}
}
