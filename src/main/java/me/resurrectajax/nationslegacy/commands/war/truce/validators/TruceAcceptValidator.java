package me.resurrectajax.nationslegacy.commands.war.truce.validators;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import me.resurrectajax.ajaxplugin.interfaces.ParentCommand;
import me.resurrectajax.nationslegacy.commands.war.WarCommand;
import me.resurrectajax.nationslegacy.general.CommandValidator;
import me.resurrectajax.nationslegacy.general.GeneralMethods;
import me.resurrectajax.nationslegacy.main.Nations;
import me.resurrectajax.nationslegacy.persistency.MappingRepository;
import me.resurrectajax.nationslegacy.persistency.NationMapping;
import me.resurrectajax.nationslegacy.persistency.PlayerMapping;
import me.resurrectajax.nationslegacy.ranking.Rank;

public class TruceAcceptValidator extends CommandValidator {

	public TruceAcceptValidator(CommandSender sender, String[] args, ParentCommand command) {
		super((Nations)command.getMain(), sender, args, command);
	}

	@Override
	public boolean validate() {
		FileConfiguration language = main.getLanguage();
		Player player = (Player) sender;
		
		MappingRepository mappingRepo = main.getMappingRepo();
		PlayerMapping playerMap = mappingRepo.getPlayerByUUID(player.getUniqueId());
		NationMapping nation = mappingRepo.getNationByPlayer(playerMap);
		NationMapping senderNation = mappingRepo.getNationByName(args.length < 3 ? "" : args[2]);
		
		if(args.length < 3) sender.sendMessage(GeneralMethods.getBadSyntaxMessage(main, command.getSyntax()));
		else if(nation == null) player.sendMessage(GeneralMethods.format((OfflinePlayer)sender, language.getString("Command.Player.NotInNation.Message"), args[2]));
		else if(!playerMap.getRank().equals(Rank.getHighest())) sender.sendMessage(GeneralMethods.format((OfflinePlayer)sender, language.getString("Command.Player.NotALeader.Message"), nation.getName()));
		else if(senderNation == null) player.sendMessage(GeneralMethods.format((OfflinePlayer)sender, language.getString("Command.Nations.NotExist.Message"), args[2]));
		else if(nation == senderNation) player.sendMessage(GeneralMethods.format((OfflinePlayer)sender, language.getString("Command.Nations.War.Add.Self.Message"), args[2]));
		else if(!mappingRepo.getWarNationsByNationID(nation.getNationID()).contains(senderNation)) player.sendMessage(GeneralMethods.format((OfflinePlayer)sender, language.getString("Command.Nations.War.Add.NotAtWar.Message"), args[2]));
		else if(!((WarCommand)command.getParentCommand()).getTruceRequests().containsKey(nation.getNationID()) || 
				!((WarCommand)command.getParentCommand()).getTruceRequests().get(nation.getNationID()).contains(senderNation.getNationID())) sender.sendMessage(GeneralMethods.format((OfflinePlayer)sender, language.getString("Command.Nations.War.Truce.Receive.NoRequest.Message"), args[1]));
		else return true;
		return false;
	}

}
