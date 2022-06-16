package events;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;

import general.GeneralMethods;
import main.Main;
import persistency.MappingRepository;
import persistency.NationMapping;
import persistency.PlayerMapping;

public class DisbandNationEvent extends NationEvent{
	public DisbandNationEvent(NationMapping nation) {
		super(nation);
		
		if(super.isCancelled) return;
		
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

}
