package commands.war;

import java.util.List;

import interfaces.ParentCommand;

public class WarCommand extends ParentCommand{

	private ParentCommand parent;
	public WarCommand(ParentCommand parent) {
		this.parent = parent;
	}
	
	@Override
	public String getPermissionNode() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean hasTabCompletion() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "war";
	}

	@Override
	public String getSyntax() {
		// TODO Auto-generated method stub
		return "/nations war <subcommand>";
	}

	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		return "Run a war command";
	}

	@Override
	public List<ParentCommand> getSubCommands() {
		// TODO Auto-generated method stub
		return null;
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

}
