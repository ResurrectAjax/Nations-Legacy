package events.nation.disband;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;

import events.nation.NationEvent;
import main.Main;
import general.GeneralMethods;
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
				
				List<PlayerMapping> members = new ArrayList<PlayerMapping>();
				members.addAll(nation.getLeaders());
				members.addAll(nation.getOfficers());
				members.addAll(nation.getMembers());
				
				List<PlayerMapping> onlineMembers = members.stream()
						.filter(el -> Bukkit.getPlayer(el.getUUID()) != null)
						.collect(Collectors.toList());
				
				OfflinePlayer player = Bukkit.getOfflinePlayer(onlineMembers.get(0).getUUID());
				
				Bukkit.broadcastMessage(GeneralMethods.format(player, language.getString("Command.Nations.Disband.Disbanded.Message"), nation.getName()));
				mappingRepo.disbandNation(nation);
			}	
		}, 1L);
		
	}

}
