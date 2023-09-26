package me.resurrectajax.nationslegacy.events.nation.kick;

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

public class KickFromNationEvent extends NationEvent{

	public KickFromNationEvent(NationMapping nation, CommandSender sender, PlayerMapping player) {
		super(nation, sender);
		
		Nations main = Nations.getInstance();
		MappingRepository mappingRepo = main.getMappingRepo();
		FileConfiguration language = main.getLanguage();
		Bukkit.getScheduler().scheduleSyncDelayedTask(main, new Runnable() {
			@Override
			public void run() {
				if(isCancelled) return;
				
				OfflinePlayer play = (OfflinePlayer) sender;
				
				String message = GeneralMethods.format(play, language.getString("Command.Nations.Player.Kick.Message"), Bukkit.getOfflinePlayer(player.getUUID()).getName());
				nation.getPlayers().forEach(el -> {
					if(Bukkit.getPlayer(el.getUUID()) != null) Bukkit.getPlayer(el.getUUID()).sendMessage(message);
				});
				
				nation.kickPlayer(player);
				GeneralMethods.updatePlayerTab(Bukkit.getPlayer(play.getUniqueId()));
				Player kickedPlayer = Bukkit.getPlayer(player.getUUID());
				if(kickedPlayer != null) main.reloadPermissions(kickedPlayer);
				
				Set<WarMapping> wars = mappingRepo.getWarsByNationID(nation.getNationID());
				if(wars.isEmpty()) return;
				wars.stream().forEach(el -> el.updateGoal());
				mappingRepo.updateNationWars(nation.getNationID());
			}
		}, 1L);
	}
	
}
