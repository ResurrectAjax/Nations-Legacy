package events.nation.join;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import enumeration.Rank;
import events.nation.NationEvent;
import general.GeneralMethods;
import main.Main;
import persistency.MappingRepository;
import persistency.NationMapping;
import persistency.PlayerMapping;

public class JoinNationEvent extends NationEvent{

	public JoinNationEvent(NationMapping nation, CommandSender sender) {
		super(nation);

		if(super.isCancelled) return;
		
		
		Main main = Main.getInstance();
		FileConfiguration language = main.getLanguage();
		MappingRepository mappingRepo = main.getMappingRepo();
		PlayerMapping player = mappingRepo.getPlayerByUUID(((Player)sender).getUniqueId());
		
		player.setNationID(nation.getNationID());
		player.setRank(Rank.Member);
		player.update();
		nation.addMember(player);
		nation.update();
		
		main.getCommandManager().removePlayerInvite(nation.getNationID(), player.getUUID());
		Bukkit.getOnlinePlayers().stream()
			.filter(el -> (nation.getLeaders().contains(mappingRepo.getPlayerByUUID(el.getUniqueId())) || nation.getOfficers().contains(mappingRepo.getPlayerByUUID(el.getUniqueId())) || nation.getMembers().contains(mappingRepo.getPlayerByUUID(el.getUniqueId()))) && !el.getUniqueId().equals(((Player)sender).getUniqueId()))
			.forEach(el -> el.sendMessage(GeneralMethods.format((OfflinePlayer)el, language.getString("Command.Player.Invite.Received.Accepted.Message"), nation.getName())));
		
		sender.sendMessage(GeneralMethods.format(sender, language.getString("Command.Player.JoinedNation.Message"), nation.getName()));
	}

}
