package me.resurrectajax.nationslegacy.events.nation.disband;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import me.resurrectajax.nationslegacy.events.nation.NationEvent;
import me.resurrectajax.nationslegacy.general.GeneralMethods;
import me.resurrectajax.nationslegacy.main.Nations;
import me.resurrectajax.nationslegacy.persistency.MappingRepository;
import me.resurrectajax.nationslegacy.persistency.NationMapping;
import me.resurrectajax.nationslegacy.persistency.PlayerMapping;

public class DisbandNationEvent extends NationEvent{
	public DisbandNationEvent(NationMapping nation, CommandSender sender) {
		super(nation, sender);
		
		Bukkit.getScheduler().scheduleSyncDelayedTask(Nations.getInstance(), new Runnable() {
			
			@Override
			public void run() {
				if(isCancelled) return;
				
				Nations main = Nations.getInstance();
				
				Set<UUID> claimers = new HashSet<>(main.getMappingRepo()
						.getClaimingSet()
						.stream()
						.filter(el -> nation.getPlayers()
								.stream()
								.map(pla -> pla.getUUID())
								.anyMatch(ma -> el.equals(ma)))
						.collect(Collectors.toSet()));
				Set<UUID> unclaimers = new HashSet<>(main.getMappingRepo()
						.getUnclaimingSet()
						.stream()
						.filter(el -> nation.getPlayers()
								.stream()
								.map(pla -> pla.getUUID())
								.anyMatch(ma -> el.equals(ma)))
						.collect(Collectors.toSet()));
				claimers.addAll(unclaimers);
				main.getMappingRepo().getUnclaimingSet().removeAll(claimers);
				
				FileConfiguration language = Nations.getInstance().getLanguage();
				MappingRepository mappingRepo = Nations.getInstance().getMappingRepo();
				
				List<PlayerMapping> members = new ArrayList<PlayerMapping>(nation.getPlayers());
				
				List<PlayerMapping> onlineMembers = members.stream()
						.filter(el -> Bukkit.getPlayer(el.getUUID()) != null)
						.collect(Collectors.toList());
				
				if(!onlineMembers.isEmpty()) {
					OfflinePlayer player = Bukkit.getOfflinePlayer(onlineMembers.get(0).getUUID());
					
					onlineMembers.forEach(el -> {
						Player onplayer = Bukkit.getPlayer(el.getUUID());
						onplayer.sendMessage(GeneralMethods.format(player, language.getString("Command.Nations.Disband.Disbanded.Message"), nation.getName()));
					});	
				}
				
				mappingRepo.disbandNation(nation);
				
				GeneralMethods.updatePlayerTab((Player) sender);
				onlineMembers.forEach(el -> {
					Player onPlayer = Bukkit.getPlayer(el.getUUID());
					GeneralMethods.updatePlayerTab(onPlayer);
					main.reloadPermissions(onPlayer);
				});
			}	
		}, 1L);
		
	}

}
