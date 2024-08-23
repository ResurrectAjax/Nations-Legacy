package me.resurrectajax.nationslegacy.commands.war.info.validators;

import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import me.resurrectajax.ajaxplugin.interfaces.ParentCommand;
import me.resurrectajax.nationslegacy.general.CommandValidator;
import me.resurrectajax.nationslegacy.general.GeneralMethods;
import me.resurrectajax.nationslegacy.main.Nations;
import me.resurrectajax.nationslegacy.persistency.MappingRepository;
import me.resurrectajax.nationslegacy.persistency.NationMapping;
import me.resurrectajax.nationslegacy.persistency.PlayerMapping;
import me.resurrectajax.nationslegacy.persistency.WarMapping;

public class WarInfoValidator extends CommandValidator {

	public WarInfoValidator(CommandSender sender, String[] args, ParentCommand command) {
		super((Nations)command.getMain(), sender, args, command);
	}

	@Override
	public boolean validate() {
		MappingRepository mappingRepo = main.getMappingRepo();
		NationMapping nation = null, enemy = null;
		PlayerMapping player = null;
		FileConfiguration language = main.getLanguage();
		if(sender instanceof Player) player = mappingRepo.getPlayerByUUID(((Player)sender).getUniqueId());
		
		switch(args.length) {
		case 3:
			if(!(sender instanceof Player)) {
				sender.sendMessage(GeneralMethods.format(sender, language.getString("Command.Error.ByConsole.Message"), args[2]));
				return false;
			}
			
			if(player == null || player.getNationID() == null) return false;
			nation = mappingRepo.getNationByID(player.getNationID());
			enemy = mappingRepo.getNationByName(args[2]);
			break;
		case 4:
			nation = mappingRepo.getNationByName(args[2]);
			enemy = mappingRepo.getNationByName(args[3]);
			break;
		default:
			sender.sendMessage(GeneralMethods.getBadSyntaxMessage(main, command.getSyntax()));
			return false;
		}
		if(nation == null) {
			sender.sendMessage(GeneralMethods.format(sender, language.getString("Command.Nations.NotExist.Message"), args[2]));
			return false;
		}
		if(enemy == null && args.length == 4) {
			sender.sendMessage(GeneralMethods.format(sender, language.getString("Command.Nations.NotExist.Message"), args[3]));
			return false;
		}
		WarMapping war = mappingRepo.getWarByNationIDs(nation.getNationID(), enemy.getNationID());
		if(war != null) return true;
		else if(args.length == 3) sender.sendMessage(GeneralMethods.format(sender, language.getString("Command.Nations.War.Truce.Send.NotAtWar.Message"), args[2]));
		else sender.sendMessage(GeneralMethods.format(sender, language.getString("Command.Nations.War.Info.NotAtWar.Message"), args[3]));
		return false;
	}

}
