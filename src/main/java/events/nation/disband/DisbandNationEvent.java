package events.nation.disband;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

public class DisbandNationEvent extends NationEvent{
	public DisbandNationEvent(NationMapping nation, CommandSender sender) {
		super(nation, sender);
		
		Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getInstance(), new Runnable() {
			
			@Override
			public void run() {
				if(isCancelled) return;
				
				List<PlayerMapping> claimers = new ArrayList<PlayerMapping>();
				claimers.addAll(nation.getLeaders());
				claimers.addAll(nation.getOfficers());
				if(claimers.stream()
						.map(el -> el.getUUID())
						.collect(Collectors.toSet()).removeIf(el -> Main.getInstance().getMappingRepo().getClaimingSet().contains(el))) ;
				
				FileConfiguration language = Main.getInstance().getLanguage();
				MappingRepository mappingRepo = Main.getInstance().getMappingRepo();
				
				List<PlayerMapping> members = new ArrayList<PlayerMapping>(nation.getAllMembers());
				
				List<PlayerMapping> onlineMembers = members.stream()
						.filter(el -> Bukkit.getPlayer(el.getUUID()) != null)
						.collect(Collectors.toList());
				
				OfflinePlayer player = Bukkit.getOfflinePlayer(onlineMembers.get(0).getUUID());
				
				onlineMembers.forEach(el -> {
					Player onplayer = Bukkit.getPlayer(el.getUUID());
					onplayer.sendMessage(GeneralMethods.format(player, language.getString("Command.Nations.Disband.Disbanded.Message"), nation.getName()));
					GeneralMethods.updatePlayerTab(onplayer);
				});
				
				mappingRepo.disbandNation(nation);
				
				GeneralMethods.updatePlayerTab((Player) sender);
				onlineMembers.forEach(el -> GeneralMethods.updatePlayerTab(Bukkit.getPlayer(el.getUUID())));
			}	
		}, 1L);
		
	}

}
