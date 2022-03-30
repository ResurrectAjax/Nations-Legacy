package Commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import Interfaces.ParentCommand;
import Main.Main;

public class NationsCommand extends ParentCommand{
	private List<ParentCommand> subcommands;
	
	public NationsCommand(Main main) {
		subcommands = new ArrayList<ParentCommand>(Arrays.asList(
				
				));
	}
	
	@Override
	public String getPermissionNode() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "multichat";
	}

	@Override
	public String getSyntax() {
		// TODO Auto-generated method stub
		return "/multichat <subcommand>";
	}

	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		return "Runs the multichat command";
	}

	@Override
	public List<ParentCommand> getSubCommands() {
		// TODO Auto-generated method stub
		return subcommands;
	}

	@Override
	public boolean hasGUI() {
		// TODO Auto-generated method stub
		return false;
	}

}
