package me.resurrectajax.nationslegacy.listeners;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import me.resurrectajax.nationslegacy.events.nation.claim.UnclaimChunkEvent;
import me.resurrectajax.nationslegacy.general.GeneralMethods;
import me.resurrectajax.nationslegacy.main.Nations;
import me.resurrectajax.nationslegacy.persistency.MappingRepository;
import me.resurrectajax.nationslegacy.persistency.NationMapping;
import me.resurrectajax.nationslegacy.persistency.PlayerMapping;

public class ClaimListener implements Listener{
	
	private HashMap<UUID, Integer> cancelUnclaimTasks = new HashMap<>();
	private Nations main;
	private MappingRepository mappingRepo;
	
	public ClaimListener(Nations main) {
		this.main = main;
		this.mappingRepo = main.getMappingRepo();
	}
	
	@EventHandler
	public void onUnclaim(UnclaimChunkEvent event) {
		Player player = (Player) event.getSender();
		if(cancelUnclaimTasks.containsKey(player.getUniqueId())) {
			Bukkit.getScheduler().cancelTask(cancelUnclaimTasks.get(player.getUniqueId()));
			cancelUnclaimTasks.remove(player.getUniqueId());
		}
		
		cancelUnclaimTasks.put(player.getUniqueId(), Bukkit.getScheduler().scheduleSyncDelayedTask(main, new Runnable() {
			@Override
			public void run() {
				NationMapping chunkNation = mappingRepo.getNationByChunk(player.getLocation().getChunk());
				PlayerMapping playerMap = mappingRepo.getPlayerByUUID(player.getUniqueId());
				if((chunkNation == null && mappingRepo.getUnclaimingSet().contains(player.getUniqueId())) || (chunkNation != null && playerMap.getNationID() != chunkNation.getNationID())) {
					mappingRepo.getUnclaimingSet().remove(player.getUniqueId());
					player.sendMessage(GeneralMethods.format((OfflinePlayer) player, main.getLanguage().getString("Command.Nations.Unclaim.TurnedOff.Message"), player.getName()));
					player.sendMessage(GeneralMethods.format((OfflinePlayer) player, main.getLanguage().getString("Command.Nations.Unclaim.NotInClaim.Message"), player.getName()));
					return;
				}
			}
		}, 20*20L));
		
		
	}
	
}
