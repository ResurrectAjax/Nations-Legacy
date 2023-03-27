package listeners;

import java.util.Set;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import chunkgain.ChunkGainManager;
import events.nation.ReloadEvent;
import main.Main;
import persistency.MappingRepository;
import persistency.NationMapping;
import persistency.PlayerMapping;

public class JoinLeaveListener implements Listener{

	private Main main;
	private MappingRepository mappingRepo;
	private ChunkGainManager chunkManager;
	private FileConfiguration config;
	private int limit = 0;
	
	public JoinLeaveListener(Main main) {
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
			.filter(nation -> nation.getAllMembers().stream()
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
		
		if(mappingRepo.getPlayerByUUID(event.getPlayer().getUniqueId()) == null) mappingRepo.addPlayer(event.getPlayer());
		
		
		mappingRepo.getScoreboardManager().updateScoreboard(event.getPlayer());
		
		PlayerMapping playerMap = mappingRepo.getPlayerByUUID(event.getPlayer().getUniqueId());
		if(playerMap.getNationID() == null) return;
		
		NationMapping nation = mappingRepo.getNationByID(playerMap.getNationID());
		if(nation.getAllMembers().stream().filter(el -> Bukkit.getPlayer(el.getUUID()) != null).collect(Collectors.toSet()).size() != 1) return;
		
		if(nation.getGainedChunks() < limit || limit == -1) chunkManager.addChunkGain(nation);
	}
	
	@EventHandler
	public void onPlayerLeave(PlayerQuitEvent event) {
		mappingRepo = main.getMappingRepo();
		
		PlayerMapping playerMap = mappingRepo.getPlayerByUUID(event.getPlayer().getUniqueId());
		if(playerMap.getNationID() == null) return;
		
		NationMapping nation = mappingRepo.getNationByID(playerMap.getNationID());
		if(nation.getAllMembers().stream().filter(el -> Bukkit.getPlayer(el.getUUID()) != null).collect(Collectors.toSet()).size() != 1) return;
		
		chunkManager.removeChunkGain(nation.getNationID());
	}
}
