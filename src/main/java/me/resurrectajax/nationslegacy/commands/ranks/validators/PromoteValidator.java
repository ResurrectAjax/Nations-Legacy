package me.resurrectajax.nationslegacy.commands.ranks.validators;

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

public class PromoteValidator extends CommandValidator {

	public PromoteValidator(CommandSender sender, String[] args, ParentCommand command) {
		super((Nations) command.getMain(), sender, args, command);
	}

	@Override
	public boolean validate() {
		FileConfiguration language = main.getLanguage();
		
		if(args.length != 2) sender.sendMessage(GeneralMethods.getBadSyntaxMessage(main, command.getSyntax()));
		
		MappingRepository mappingRepo = main.getMappingRepo();
		PlayerMapping player = mappingRepo.getPlayerByName(args[1]), promoter = mappingRepo.getPlayerByUUID(((Player)sender).getUniqueId());
		
		if(player == null) {
			sender.sendMessage(GeneralMethods.format((OfflinePlayer)sender, language.getString("Command.Player.NotExist.Message"), args[1]));
			return false;
		}
		
		OfflinePlayer offPlayer = Bukkit.getOfflinePlayer(player.getUUID());
		if(promoter.getNationID() == null) {
			sender.sendMessage(GeneralMethods.format((OfflinePlayer)sender, language.getString("Command.Player.NotInNation.Message"), args[1]));
			return false;
		}
		
		if(player.getNationID() != promoter.getNationID()) sender.sendMessage(GeneralMethods.format((OfflinePlayer)sender, language.getString("Command.Player.NotInSameNation.Message"), args[1]));
		else if(!promoter.getRank().equals(Rank.getHighest())) sender.sendMessage(GeneralMethods.format((OfflinePlayer)sender, language.getString("Command.Player.NotALeader.Message"), args[1]));
		else if(player.getRank().equals(Rank.getHighest())) sender.sendMessage(GeneralMethods.relFormat(sender, (CommandSender)offPlayer, language.getString("Command.Player.Promote.AlreadyHighestRank.Message"), args[1]));
		else return true;
		return false;
	}

}
