package commands.admin;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import commands.admin.disband.DisbandNation;
import help.HelpCommand;
import interfaces.ParentCommand;
import main.Main;

public class AdminCommand extends ParentCommand{

	private List<ParentCommand> subcommands = new ArrayList<ParentCommand>();
	
	public AdminCommand(Main main) {
		subcommands = new ArrayList<ParentCommand>(Arrays.asList(
				new DisbandNation(main),
				new HelpCommand(main)
				));
	}
	
	@Override
	public String getPermissionNode() {
		// TODO Auto-generated method stub
		return "nations.admin";
	}

	@Override
	public boolean hasTabCompletion() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "admin";
	}

	@Override
	public String getSyntax() {
		// TODO Auto-generated method stub
		return "/nations admin <subcommand>";
	}

	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		return "Runs an admin command";
	}

	@Override
	public List<ParentCommand> getSubCommands() {
		// TODO Auto-generated method stub
		return subcommands;
	}

	@Override
	public boolean isConsole() {
		// TODO Auto-generated method stub
		return true;
	}

}
