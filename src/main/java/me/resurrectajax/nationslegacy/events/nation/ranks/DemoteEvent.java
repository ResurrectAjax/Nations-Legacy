package me.resurrectajax.nationslegacy.events.nation.ranks;

import java.util.Set;

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
import me.resurrectajax.nationslegacy.persistency.WarMapping;

public class DemoteEvent extends NationEvent{
	private PlayerMapping demotedPlayer;
	
	public DemoteEvent(NationMapping nation, CommandSender sender, PlayerMapping player) {
		super(nation, sender);
		Nations main = Nations.getInstance();
		MappingRepository mappingRepo = main.getMappingRepo();
		FileConfiguration language = main.getLanguage();
		
		setDemotedPlayer(player);
		Bukkit.getScheduler().scheduleSyncDelayedTask(main, new Runnable() {
			
			@Override
			public void run() {
				if(isCancelled) return;
				
				nation.demotePlayer(player);
				
				OfflinePlayer playerO = Bukkit.getOfflinePlayer(player.getUUID());
				for(PlayerMapping playerA : nation.getAllMembers()) {
					if(Bukkit.getPlayer(playerA.getUUID()) == null) continue;
					Player playerAB = Bukkit.getPlayer(playerA.getUUID());
					PlayerMapping playerOMap = mappingRepo.getPlayerByUUID(playerO.getUniqueId());
					playerAB.sendMessage(GeneralMethods.relFormat(sender, (CommandSender)playerO, language.getString("Command.Player.Demote.Demoted.Message"), playerO.getName(), playerOMap.getRank().toString()));
				}
				
				Set<WarMapping> wars = mappingRepo.getWarsByNationID(nation.getNationID());
				if(wars.isEmpty()) return;
				wars.stream().forEach(el -> el.updateGoal());
				mappingRepo.updateNationWars(nation.getNationID());
			}
		}, 1L);
	}

	public PlayerMapping getDemotedPlayer() {
		return demotedPlayer;
	}

	private void setDemotedPlayer(PlayerMapping promotedPlayer) {
		this.demotedPlayer = promotedPlayer;
	}

}
