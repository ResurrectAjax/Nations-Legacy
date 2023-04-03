package events.nation.join;

import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import enumeration.Rank;
import events.nation.NationEvent;
import main.Main;
import general.GeneralMethods;
import persistency.MappingRepository;
import persistency.NationMapping;
import persistency.PlayerMapping;
import persistency.WarMapping;

public class JoinNationEvent extends NationEvent{

	private Rank nationRank;
	
	public JoinNationEvent(NationMapping nation, CommandSender sender, Rank rank) {
		super(nation, sender);
		
		setNationRank(rank);
		
		Main main = Main.getInstance();
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
				
				switch(nationRank) {
					case Leader: 
						nation.addLeader(player);
						break;
					case Officer:
						nation.addOfficer(player);
						break;
					case Member:
						nation.addMember(player);
						break;
					default:
						break;
				}
				
				if(mappingRepo.getPlayerInvites().containsKey(player.getUUID()) && mappingRepo.getPlayerInvites().get(player.getUUID()).contains(nation.getNationID())) {
					mappingRepo.removePlayerInvite(nation.getNationID(), player.getUUID());
					Bukkit.getOnlinePlayers().stream()
						.filter(el -> (nation.getLeaders().contains(mappingRepo.getPlayerByUUID(el.getUniqueId())) || nation.getOfficers().contains(mappingRepo.getPlayerByUUID(el.getUniqueId())) || nation.getMembers().contains(mappingRepo.getPlayerByUUID(el.getUniqueId()))) && !el.getUniqueId().equals(((Player)sender).getUniqueId()))
						.forEach(el -> el.sendMessage(GeneralMethods.format((OfflinePlayer)sender, language.getString("Command.Player.Invite.Receive.Accepted.Message"), nation.getName())));	
				}
				
				GeneralMethods.updatePlayerTab((Player)sender);
				sender.sendMessage(GeneralMethods.format((OfflinePlayer)sender, language.getString("Command.Player.JoinedNation.Message"), nation.getName()));
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
