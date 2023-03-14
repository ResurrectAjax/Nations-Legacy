package events.nation.leave;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import events.nation.NationEvent;
import general.GeneralMethods;
import main.Main;
import persistency.MappingRepository;
import persistency.NationMapping;
import persistency.PlayerMapping;

public class LeaveEvent extends NationEvent{

	public LeaveEvent(NationMapping nation, CommandSender sender) {
		super(nation, sender);
		
		Main main = (Main) Main.getInstance();
		Bukkit.getScheduler().scheduleSyncDelayedTask(main, new Runnable() {
			
			@Override
			public void run() {
				if(isCancelled || !(sender instanceof Player)) return;
				
				MappingRepository mappingRepo = main.getMappingRepo();
				PlayerMapping playerMap = mappingRepo.getPlayerByUUID(((Player)sender).getUniqueId());
				NationMapping nation = mappingRepo.getNationByID(playerMap.getNationID());
				FileConfiguration language = main.getLanguage();
				
				
				nation.getAllMembers()
					.stream()
					.filter(el -> !el.getUUID().equals(playerMap.getUUID()) && Bukkit.getPlayer(el.getUUID()) != null)
					.map(el -> Bukkit.getPlayer(el.getUUID()))
					.forEach(el -> el.sendMessage(GeneralMethods.format((OfflinePlayer)sender, language.getString("Command.Nations.Player.Leave.Message"), sender.getName())));
				sender.sendMessage(GeneralMethods.format((OfflinePlayer) sender, language.getString("Command.Player.Leave.Message"), sender.getName()));
				nation.kickPlayer(playerMap);
			}
		}, 1L);
	}

}
