package commands.war.add;

import java.util.List;
import java.util.UUID;

import org.bukkit.command.CommandSender;

import interfaces.ChildCommand;
import interfaces.ParentCommand;
import main.Main;

public class WarAdd extends ChildCommand{
	private ParentCommand parent;
	private Main main;
	public WarAdd(ParentCommand parent) {
		this.parent = parent;
		this.main = parent.getMain();
	}
	
	@Override
	public void perform(CommandSender sender, String[] args) {
		
	}

	@Override
	public String[] getArguments(UUID uuid) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getPermissionNode() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean hasTabCompletion() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "add";
	}

	@Override
	public String getSyntax() {
		// TODO Auto-generated method stub
		return "/nations war add <nation>";
	}

	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		return "Declare war with another nation";
	}

	@Override
	public List<ParentCommand> getSubCommands() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ParentCommand getParentCommand() {
		// TODO Auto-generated method stub
		return parent;
	}

	@Override
	public boolean isConsole() {
		// TODO Auto-generated method stub
		return false;
	}

}
