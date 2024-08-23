package me.resurrectajax.nationslegacy.commands.alliance.add.validators;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import me.resurrectajax.nationslegacy.commands.alliance.AllyCommand;
import me.resurrectajax.nationslegacy.commands.alliance.add.AllyCancel;
import me.resurrectajax.nationslegacy.general.CommandValidator;
import me.resurrectajax.nationslegacy.general.GeneralMethods;
import me.resurrectajax.nationslegacy.main.Nations;
import me.resurrectajax.nationslegacy.persistency.MappingRepository;
import me.resurrectajax.nationslegacy.persistency.NationMapping;

public class AllyCancelValidator extends CommandValidator {

	public AllyCancelValidator(Nations main, CommandSender sender, String[] args, AllyCancel command) {
		super(main, sender, args, command);
	}

	@Override
	public boolean validate() {
		FileConfiguration language = main.getLanguage();
		MappingRepository mappingRepo = main.getMappingRepo();
		
		NationMapping senderNation = mappingRepo.getNationByPlayer(mappingRepo.getPlayerByUUID(((Player) sender).getUniqueId()));
		NationMapping nation = mappingRepo.getNationByName(args.length < 3 ? "" : args[2]);
		AllyCommand parentCommand = (AllyCommand) command.getParentCommand();
		
		if(args.length < 3) sender.sendMessage(GeneralMethods.getBadSyntaxMessage(main, command.getSyntax()));
		else if(senderNation == null) sender.sendMessage(GeneralMethods.format((OfflinePlayer)sender, language.getString("Command.Player.NotInNation.Message"), ""));
		else if(nation == null ||
				!parentCommand.getAllianceRequests().containsKey(nation.getNationID()) ||  
				!parentCommand.getAllianceRequests().get(nation.getNationID()).contains(senderNation.getNationID())) sender.sendMessage(GeneralMethods.format((OfflinePlayer)sender, language.getString("Command.Nations.Alliance.Add.Receive.NoRequest.Message"), args[2]));
		else return true;
		return false;
	}

}
