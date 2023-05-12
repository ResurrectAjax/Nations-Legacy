package me.resurrectajax.nationslegacy.commands.create;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;

import me.resurrectajax.ajaxplugin.interfaces.ChildCommand;
import me.resurrectajax.ajaxplugin.interfaces.ParentCommand;
import me.resurrectajax.ajaxplugin.plugin.AjaxPlugin;
import me.resurrectajax.nationslegacy.events.nation.create.CreateNationEvent;
import me.resurrectajax.nationslegacy.general.GeneralMethods;
import me.resurrectajax.nationslegacy.main.Nations;
import me.resurrectajax.nationslegacy.persistency.MappingRepository;
import me.resurrectajax.nationslegacy.persistency.NationMapping;

public class CreateNationCommand extends ChildCommand{

	private ParentCommand parent;
	private Nations main;
	public CreateNationCommand(ParentCommand parent) {
		this.main = (Nations) parent.getMain();
		this.parent = parent;
	}

	@Override
	public String getPermissionNode() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "create";
	}

	@Override
	public String getSyntax() {
		// TODO Auto-generated method stub
		return "/nations create <name>";
	}

	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		return "Create a nation";
	}

	@Override
	public List<ParentCommand> getSubCommands() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String[] getArguments(UUID uuid) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void perform(CommandSender sender, String[] args) {
		FileConfiguration lang = main.getLanguage();
		MappingRepository mappingRepo = main.getMappingRepo();
		
		OfflinePlayer player = (OfflinePlayer) sender;
		super.setLastArg(main, sender, args.length < 2 ? "" : args[1]);
		
		if(args.length != 2) sender.sendMessage(GeneralMethods.getBadSyntaxMessage(main, getSyntax()));
		else if(!Pattern.matches("[a-zA-Z]+", args[1])) sender.sendMessage(GeneralMethods.format(player, lang.getString("Command.Error.SpecialCharacters.Message"), args[1]));
		else if(mappingRepo.getPlayerByUUID(player.getUniqueId()).getNationID() != null) sender.sendMessage(GeneralMethods.format(player, lang.getString("Command.Nations.Create.AlreadyInNation.Message"), args[1]));
		else if(mappingRepo.getNationByName(args[1]) != null) sender.sendMessage(GeneralMethods.format(player, lang.getString("Command.Nations.Create.AlreadyExists.Message"), args[1]));
		else {
			NationMapping nation = mappingRepo.createNation(args[1], mappingRepo.getPlayerByUUID(player.getUniqueId()));
			Bukkit.getPluginManager().callEvent(new CreateNationEvent(nation, sender));
		}
	}

	@Override
	public boolean hasTabCompletion() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isConsole() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public ParentCommand getParentCommand() {
		// TODO Auto-generated method stub
		return parent;
	}

	@Override
	public String[] getSubArguments(String[] args) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AjaxPlugin getMain() {
		// TODO Auto-generated method stub
		return main;
	}

}
