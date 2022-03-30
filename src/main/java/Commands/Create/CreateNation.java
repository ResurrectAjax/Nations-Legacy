package Commands.Create;

import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import General.GeneralMethods;
import Interfaces.ChildCommand;
import Interfaces.ParentCommand;
import Main.Main;
import Persistency.MappingRepository;

public class CreateNation extends ChildCommand{

	private Main main;
	public CreateNation(Main main) {
		this.main = main;
	}
	
	@Override
	public void perform(Player player, String[] args) {
		FileConfiguration lang = main.getLanguage();
		MappingRepository mappingRepo = main.getMappingRepo();
		String message;
		
		if(args.length != 2) {
			player.sendMessage(GeneralMethods.getBadSyntaxMessage(getSyntax()));
		}
		else if(!Pattern.matches("[a-zA-Z]+", args[1])) {
			message = GeneralMethods.format(lang.getString("Command.Error.SpecialCharacters.Message"));
			player.sendMessage(message);
		}
		else if(mappingRepo.getNationByName(args[1]) != null) {
			
		}
	}

	@Override
	public String getPermissionNode() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean hasGUI() {
		// TODO Auto-generated method stub
		return false;
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

}
