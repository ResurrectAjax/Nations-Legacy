package me.resurrectajax.nationslegacy.events.nation.war;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import me.resurrectajax.nationslegacy.commands.war.WarCommand;
import me.resurrectajax.nationslegacy.general.GeneralMethods;
import me.resurrectajax.nationslegacy.main.Nations;
import me.resurrectajax.nationslegacy.persistency.MappingRepository;
import me.resurrectajax.nationslegacy.persistency.NationMapping;
import me.resurrectajax.nationslegacy.persistency.PlayerMapping;

public class AcceptTruceEvent extends WarEvent{

	public AcceptTruceEvent(NationMapping nation, NationMapping enemy, WarCommand warCommand, CommandSender sender) {
		super(nation, enemy, sender);
		
		Nations main = Nations.getInstance();
		Bukkit.getScheduler().scheduleSyncDelayedTask(main, new Runnable() {
			
			@Override
			public void run() {
				if(isCancelled) return;
				
				FileConfiguration language = main.getLanguage();
				MappingRepository mappingRepo = main.getMappingRepo();
				
				mappingRepo.removeWar(nation.getNationID(), enemy.getNationID());
				warCommand.removeTruceRequest(nation.getNationID(), enemy.getNationID());
				
				Set<PlayerMapping> players = new HashSet<PlayerMapping>();
				players.addAll(nation.getAllMembers());
				players.addAll(enemy.getAllMembers());
				
				Player player = Bukkit.getOnlinePlayers().stream().filter(el -> nation.getAllMembers().contains(mappingRepo.getPlayerByUUID(el.getUniqueId()))).findFirst().orElse(null);
				Player enemyPlayer = Bukkit.getOnlinePlayers().stream().filter(el -> enemy.getAllMembers().contains(mappingRepo.getPlayerByUUID(el.getUniqueId()))).findFirst().orElse(null);
				Bukkit.getOnlinePlayers().stream()
					.filter(el -> players.contains(mappingRepo.getPlayerByUUID(el.getUniqueId())))
					.forEach(el -> {
						if(enemyPlayer == null) el.sendMessage(GeneralMethods.format((OfflinePlayer)el, language.getString("Command.Nations.War.Truce.Receive.Accepted.Message"), nation.getName()));
						el.sendMessage(GeneralMethods.relFormat(player, enemyPlayer, language.getString("Command.Nations.War.Truce.Receive.Accepted.Message"), nation.getName()));
					});
			}
		}, 1L);
	}

}
