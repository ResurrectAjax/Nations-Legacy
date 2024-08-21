package me.resurrectajax.nationslegacy.commands.kick.validators;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import me.resurrectajax.ajaxplugin.interfaces.ParentCommand;
import me.resurrectajax.nationslegacy.general.CommandValidator;
import me.resurrectajax.nationslegacy.general.GeneralMethods;
import me.resurrectajax.nationslegacy.main.Nations;
import me.resurrectajax.nationslegacy.persistency.MappingRepository;
import me.resurrectajax.nationslegacy.persistency.PlayerMapping;
import me.resurrectajax.nationslegacy.ranking.Rank;

public class KickValidator extends CommandValidator {

	public KickValidator(CommandSender sender, String[] args, ParentCommand command) {
		super((Nations) command.getMain(), sender, args, command);
	}

	@Override
	public boolean validate() {
		MappingRepository mappingRepo = main.getMappingRepo();
		FileConfiguration language = main.getLanguage();
		
		PlayerMapping player = mappingRepo.getPlayerByName(args.length == 2 ? args[1] : "");
		if(args.length < 2) sender.sendMessage(GeneralMethods.getBadSyntaxMessage(main, command.getSyntax()));
		else if(player == null) sender.sendMessage(GeneralMethods.format((OfflinePlayer) sender, language.getString("Command.Player.NotExist.Message"), args[1]));
		else {
			PlayerMapping senderMap = mappingRepo.getPlayerByUUID(((Player)sender).getUniqueId());
			OfflinePlayer offPlayer = Bukkit.getOfflinePlayer(player.getUUID());
			
			if(senderMap.getNationID() == null) sender.sendMessage(GeneralMethods.format((OfflinePlayer) sender, language.getString("Command.Player.NotInNation.Message"), offPlayer.getName()));
			else if(player.getNationID() != senderMap.getNationID()) sender.sendMessage(GeneralMethods.format((OfflinePlayer) sender, language.getString("Command.Player.NotInSameNation.Message"), offPlayer.getName()));
			else if(!senderMap.getRank().equals(Rank.getHighest())) sender.sendMessage(GeneralMethods.format((OfflinePlayer) sender, language.getString("Command.Player.NotALeader.Message"), offPlayer.getName()));
			else if(player.getRank().equals(Rank.getHighest())) sender.sendMessage(GeneralMethods.format((OfflinePlayer) sender, language.getString("Command.Player.Kick.Leader.Message"), offPlayer.getName()));
			else return true;
		}
		return false;
	}

}
