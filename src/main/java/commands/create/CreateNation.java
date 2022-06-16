package commands.create;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;

import general.GeneralMethods;
import interfaces.ChildCommand;
import interfaces.ParentCommand;
import main.Main;
import persistency.MappingRepository;

public class CreateNation extends ChildCommand{

	private Main main;
	public CreateNation(Main main) {
		this.main = main;
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
		super.beforePerform(sender, args.length < 2 ? "" : args[1]);
		
		if(args.length != 2) sender.sendMessage(GeneralMethods.getBadSyntaxMessage(getSyntax()));
		else if(!Pattern.matches("[a-zA-Z]+", args[1])) sender.sendMessage(GeneralMethods.format(player, lang.getString("Command.Error.SpecialCharacters.Message"), args[1]));
		else if(mappingRepo.getPlayerByUUID(player.getUniqueId()).getNationID() != null) sender.sendMessage(GeneralMethods.format(player, lang.getString("Command.Nations.Create.AlreadyInNation.Message"), args[1]));
		else if(mappingRepo.getNationByName(args[1]) != null) sender.sendMessage(GeneralMethods.format(player, lang.getString("Command.Nations.Create.AlreadyExists.Message"), args[1]));
		else {
			mappingRepo.createNation(args[1], mappingRepo.getPlayerByUUID(player.getUniqueId()));
			sender.sendMessage(GeneralMethods.format(player, lang.getString("Command.Nations.Create.Created.Message"), args[1]));
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

}
