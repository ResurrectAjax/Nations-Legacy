package events.nation.alliance;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import commands.alliance.AllyCommand;
import events.nation.NationEvent;
import main.Main;
import general.GeneralMethods;
import persistency.MappingRepository;
import persistency.NationMapping;
import persistency.PlayerMapping;

public class CreateAllianceEvent extends NationEvent{
	private NationMapping ally;
	
	public CreateAllianceEvent(NationMapping nation, NationMapping allyA, AllyCommand allyCommand, CommandSender sender) {
		super(nation, sender);
		this.ally = allyA;

		if(super.isCancelled) return;
		
		Main main = Main.getInstance();
		FileConfiguration language = main.getLanguage();
		MappingRepository mappingRepo = main.getMappingRepo();
		
		Bukkit.getScheduler().scheduleSyncDelayedTask(main, new Runnable() {
			
			@Override
			public void run() {
				mappingRepo.createAlliance(nation.getNationID(), ally.getNationID());
				allyCommand.removeAllianceRequest(nation.getNationID(), ally.getNationID());
				
				Set<PlayerMapping> players = new HashSet<PlayerMapping>();
				players.addAll(nation.getAllMembers());
				players.addAll(ally.getAllMembers());
				
				Player player = Bukkit.getOnlinePlayers().stream().filter(el -> nation.getAllMembers().contains(mappingRepo.getPlayerByUUID(el.getUniqueId()))).findFirst().orElse(null);
				Player allyPlayer = Bukkit.getOnlinePlayers().stream().filter(el -> ally.getAllMembers().contains(mappingRepo.getPlayerByUUID(el.getUniqueId()))).findFirst().orElse(null);
				Bukkit.getOnlinePlayers().stream()
					.filter(el -> players.contains(mappingRepo.getPlayerByUUID(el.getUniqueId())))
					.forEach(el -> {
						if(allyPlayer == null) el.sendMessage(GeneralMethods.format((OfflinePlayer)el, language.getString("Command.Nations.Alliance.Add.Receive.Accepted.Message"), nation.getName()));
						el.sendMessage(GeneralMethods.relFormat(player, allyPlayer, language.getString("Command.Nations.Alliance.Add.Receive.Accepted.Message"), nation.getName()));
					});
			}
		}, 1L);
	}
	
	public NationMapping getAlly() {
		return ally;
	}

}
