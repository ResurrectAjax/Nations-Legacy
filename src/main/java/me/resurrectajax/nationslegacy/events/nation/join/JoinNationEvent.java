package me.resurrectajax.nationslegacy.events.nation.join;

import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import me.resurrectajax.nationslegacy.ranking.Rank;
import me.resurrectajax.nationslegacy.events.nation.NationEvent;
import me.resurrectajax.nationslegacy.general.GeneralMethods;
import me.resurrectajax.nationslegacy.main.Nations;
import me.resurrectajax.nationslegacy.persistency.MappingRepository;
import me.resurrectajax.nationslegacy.persistency.NationMapping;
import me.resurrectajax.nationslegacy.persistency.PlayerMapping;
import me.resurrectajax.nationslegacy.persistency.WarMapping;

public class JoinNationEvent extends NationEvent{

	private Rank nationRank;
	
	public JoinNationEvent(NationMapping nation, CommandSender sender, Rank rank) {
		super(nation, sender);
		
		setNationRank(rank);
		
		Nations main = Nations.getInstance();
		Bukkit.getScheduler().scheduleSyncDelayedTask(main, new Runnable() {
			
			@Override
			public void run() {
				if(isCancelled) return;
				
				FileConfiguration language = main.getLanguage();
				MappingRepository mappingRepo = main.getMappingRepo();
				PlayerMapping player = mappingRepo.getPlayerByUUID(((Player)sender).getUniqueId());
				
				if(player.getNationID() != null) {
					NationMapping nation = mappingRepo.getNationByID(player.getNationID());
					nation.kickPlayer(player);
				}
				
				
				nation.addPlayerWithRank(player, rank);

				
				if(mappingRepo.getPlayerInvites().containsKey(player.getUUID()) && mappingRepo.getPlayerInvites().get(player.getUUID()).contains(nation.getNationID())) {
					mappingRepo.removePlayerInvite(nation.getNationID(), player.getUUID());
					Bukkit.getOnlinePlayers().stream()
						.filter(el -> (nation.getPlayers().contains(mappingRepo.getPlayerByUUID(el.getUniqueId()))) && !el.getUniqueId().equals(((Player)sender).getUniqueId()))
						.forEach(el -> el.sendMessage(GeneralMethods.format((OfflinePlayer)sender, language.getString("Command.Player.Invite.Receive.Accepted.Message"), nation.getName())));	
				}
				
				GeneralMethods.updatePlayerTab((Player)sender);
				sender.sendMessage(GeneralMethods.format((OfflinePlayer)sender, language.getString("Command.Player.JoinedNation.Message"), nation.getName()));
				main.reloadPermissions(Bukkit.getPlayer(player.getUUID()));
				
				Set<WarMapping> wars = mappingRepo.getWarsByNationID(nation.getNationID());
				if(wars.isEmpty()) return;
				wars.stream().forEach(el -> el.updateGoal());
				mappingRepo.updateNationWars(nation.getNationID());
			}
		}, 1L);
	}

	public Rank getNationRank() {
		return nationRank;
	}

	public void setNationRank(Rank nationRank) {
		this.nationRank = nationRank;
	}
	
	

}
