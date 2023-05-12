package me.resurrectajax.nationslegacy.events.nation.alliance;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import me.resurrectajax.nationslegacy.commands.alliance.AllyCommand;
import me.resurrectajax.nationslegacy.events.nation.NationEvent;
import me.resurrectajax.nationslegacy.general.GeneralMethods;
import me.resurrectajax.nationslegacy.main.Nations;
import me.resurrectajax.nationslegacy.persistency.MappingRepository;
import me.resurrectajax.nationslegacy.persistency.NationMapping;
import me.resurrectajax.nationslegacy.persistency.PlayerMapping;

public class RemoveAllianceEvent extends NationEvent{
	private NationMapping ally;
	
	public RemoveAllianceEvent(NationMapping nation, NationMapping allyA, AllyCommand allyCommand, CommandSender sender) {
		super(nation, sender);
		this.ally = allyA;

		if(super.isCancelled) return;
		
		Nations main = Nations.getInstance();
		FileConfiguration language = main.getLanguage();
		MappingRepository mappingRepo = main.getMappingRepo();
		
		
		Bukkit.getScheduler().scheduleSyncDelayedTask(main, new Runnable() {
			
			@Override
			public void run() {
				mappingRepo.removeAlliance(nation.getNationID(), ally.getNationID());
				
				Set<PlayerMapping> players = new HashSet<PlayerMapping>();
				players.addAll(nation.getAllMembers());
				players.addAll(ally.getAllMembers());
				
				Player player = Bukkit.getOnlinePlayers().stream().filter(el -> nation.getAllMembers().contains(mappingRepo.getPlayerByUUID(el.getUniqueId()))).findFirst().orElse(null);
				Player allyPlayer = Bukkit.getOnlinePlayers().stream().filter(el -> ally.getAllMembers().contains(mappingRepo.getPlayerByUUID(el.getUniqueId()))).findFirst().orElse(null);
				Bukkit.getOnlinePlayers().stream()
					.filter(el -> players.contains(mappingRepo.getPlayerByUUID(el.getUniqueId())))
					.forEach(el -> {
						if(allyPlayer == null) el.sendMessage(GeneralMethods.format((OfflinePlayer)el, language.getString("Command.Nations.Alliance.Remove.Removed.Message"), nation.getName()));
						el.sendMessage(GeneralMethods.relFormat(player, allyPlayer, language.getString("Command.Nations.Alliance.Remove.Removed.Message"), nation.getName()));
					});
			}
		}, 1L);
	}
	
	public NationMapping getAlly() {
		return ally;
	}

}
