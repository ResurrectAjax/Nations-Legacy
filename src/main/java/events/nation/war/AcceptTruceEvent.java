package events.nation.war;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import commands.war.WarCommand;
import events.nation.NationEvent;
import main.Main;
import general.GeneralMethods;
import persistency.MappingRepository;
import persistency.NationMapping;
import persistency.PlayerMapping;

public class AcceptTruceEvent extends NationEvent{

	public AcceptTruceEvent(NationMapping nation, NationMapping enemy, WarCommand warCommand, CommandSender sender) {
		super(nation, sender);
		
		if(this.isCancelled) return;
		
		Main main = Main.getInstance();
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

}
