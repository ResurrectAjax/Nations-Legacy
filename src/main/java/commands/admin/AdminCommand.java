package commands.admin;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import commands.admin.disband.DisbandNation;
import commands.admin.reload.Reload;
import help.HelpCommand;
import interfaces.ParentCommand;

public class AdminCommand extends ParentCommand{

	private ParentCommand parent;
	private List<ParentCommand> subcommands = new ArrayList<ParentCommand>();
	
	public AdminCommand(ParentCommand parent) {
		this.parent = parent;
		subcommands = new ArrayList<ParentCommand>(Arrays.asList(
				new DisbandNation(this),
				new HelpCommand(this),
				new Reload(this)
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

	@Override
	public ParentCommand getParentCommand() {
		// TODO Auto-generated method stub
		return parent;
	}

}
