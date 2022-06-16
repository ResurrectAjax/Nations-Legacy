package commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import commands.admin.AdminCommand;
import commands.create.CreateNation;
import commands.disband.DisbandNation;
import commands.info.NationInfo;
import commands.who.Who;
import help.HelpCommand;
import interfaces.ParentCommand;
import main.Main;

public class NationsCommand extends ParentCommand{
	private List<ParentCommand> subcommands;
	
	public NationsCommand(Main main) {
		subcommands = new ArrayList<ParentCommand>(Arrays.asList(
				new CreateNation(main),
				new NationInfo(main),
				new Who(main),
				new DisbandNation(main),
				new AdminCommand(main),
				new HelpCommand(main)
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
		return "nations";
	}

	@Override
	public String getSyntax() {
		// TODO Auto-generated method stub
		return "/nations <subcommand>";
	}

	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		return "Runs the nations command";
	}

	@Override
	public List<ParentCommand> getSubCommands() {
		// TODO Auto-generated method stub
		return subcommands;
	}

	@Override
	public boolean hasTabCompletion() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isConsole() {
		// TODO Auto-generated method stub
		return true;
	}

}
