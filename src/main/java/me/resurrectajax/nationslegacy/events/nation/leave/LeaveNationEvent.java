package me.resurrectajax.nationslegacy.events.nation.leave;

import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import me.resurrectajax.nationslegacy.ranking.Rank;
import me.resurrectajax.nationslegacy.events.nation.NationEvent;
import me.resurrectajax.nationslegacy.events.nation.disband.DisbandNationEvent;
import me.resurrectajax.nationslegacy.general.GeneralMethods;
import me.resurrectajax.nationslegacy.main.Nations;
import me.resurrectajax.nationslegacy.persistency.MappingRepository;
import me.resurrectajax.nationslegacy.persistency.NationMapping;
import me.resurrectajax.nationslegacy.persistency.PlayerMapping;
import me.resurrectajax.nationslegacy.persistency.WarMapping;

public class LeaveNationEvent extends NationEvent{

	public LeaveNationEvent(NationMapping nation, CommandSender sender) {
		super(nation, sender);
		
		Nations main = (Nations) Nations.getInstance();
		Bukkit.getScheduler().scheduleSyncDelayedTask(main, new Runnable() {
			
			@Override
			public void run() {
				if(isCancelled || !(sender instanceof Player)) return;
				
				MappingRepository mappingRepo = main.getMappingRepo();
				PlayerMapping playerMap = mappingRepo.getPlayerByUUID(((Player)sender).getUniqueId());
				NationMapping nation = mappingRepo.getNationByID(playerMap.getNationID());
				FileConfiguration language = main.getLanguage();
				
				
				nation.getPlayers()
					.stream()
					.filter(el -> !el.getUUID().equals(playerMap.getUUID()) && Bukkit.getPlayer(el.getUUID()) != null)
					.map(el -> Bukkit.getPlayer(el.getUUID()))
					.forEach(el -> el.sendMessage(GeneralMethods.format((OfflinePlayer)sender, language.getString("Command.Nations.Player.Leave.Message"), sender.getName())));
				sender.sendMessage(GeneralMethods.format((OfflinePlayer) sender, language.getString("Command.Player.Leave.Message"), sender.getName()));
				
				if(nation.getPlayersByRank(Rank.getHighest()).size() == 1 && playerMap.getRank().equals(Rank.getHighest())) main.getServer().getPluginManager().callEvent(new DisbandNationEvent(nation, sender));
				else {
					nation.kickPlayer(playerMap);
					GeneralMethods.updatePlayerTab((Player)sender);
					main.reloadPermissions(Bukkit.getPlayer(playerMap.getUUID()));
					
					Set<WarMapping> wars = mappingRepo.getWarsByNationID(nation.getNationID());
					if(wars.isEmpty()) return;
					wars.stream().forEach(el -> el.updateGoal());
					mappingRepo.updateNationWars(nation.getNationID());
				}
			}
		}, 1L);
	}

}
