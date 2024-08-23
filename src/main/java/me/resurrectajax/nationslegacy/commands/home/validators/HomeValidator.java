package me.resurrectajax.nationslegacy.commands.home.validators;

import org.bukkit.OfflinePlayer;
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

public class HomeValidator extends CommandValidator {

	public HomeValidator(CommandSender sender, String[] args, ParentCommand command) {
		super((Nations) command.getMain(), sender, args, command);
	}

	@Override
	public boolean validate() {
		Player player = (Player) sender;
		MappingRepository mappingRepo = main.getMappingRepo();
		PlayerMapping playerMap = mappingRepo.getPlayerByUUID(player.getUniqueId());
		FileConfiguration language = main.getLanguage();
		
		String home = args.length > 1 ? args[1] : "home";
		
		NationMapping nation = mappingRepo.getNationByID(playerMap.getNationID());
		if(playerMap.getNationID() == null) player.sendMessage(GeneralMethods.format((OfflinePlayer)sender, language.getString("Command.Player.NotInNation.Message"), player.getName()));
		else if(!nation.getHomes().containsKey(home)) player.sendMessage(GeneralMethods.format((OfflinePlayer)sender, language.getString("Command.Nations.Home.DelHome.NotFound.Message"), nation.getName()));
		else return true;
		return false;
	}

}
