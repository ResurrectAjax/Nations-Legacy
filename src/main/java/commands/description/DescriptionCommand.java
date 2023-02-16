package commands.description;

import java.util.Arrays;
import java.util.List;

import commands.description.remove.RemoveDescription;
import commands.description.set.SetDescription;
import me.resurrectajax.ajaxplugin.interfaces.ParentCommand;

public class DescriptionCommand extends ParentCommand{
	private ParentCommand parent;
	public DescriptionCommand(ParentCommand parent) {
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
		return "description";
	}

	@Override
	public String getSyntax() {
		// TODO Auto-generated method stub
		return "/nations description <set | remove>";
	}

	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		return "Set or remove your nation's description";
	}

	@Override
	public List<ParentCommand> getSubCommands() {
		// TODO Auto-generated method stub
		return Arrays.asList(
				new SetDescription(this),
				new RemoveDescription(this)
				);
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
