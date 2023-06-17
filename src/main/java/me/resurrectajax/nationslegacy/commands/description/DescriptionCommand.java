package me.resurrectajax.nationslegacy.commands.description;

import java.util.Arrays;
import java.util.List;

import me.resurrectajax.ajaxplugin.interfaces.ParentCommand;
import me.resurrectajax.ajaxplugin.plugin.AjaxPlugin;
import me.resurrectajax.nationslegacy.commands.description.remove.RemoveDescription;
import me.resurrectajax.nationslegacy.commands.description.set.SetDescription;

public class DescriptionCommand extends ParentCommand{
	private ParentCommand parent;
	public DescriptionCommand(ParentCommand parent) {
		this.parent = parent;
	}
	
	@Override
	public String getPermissionNode() {
		// TODO Auto-generated method stub
		return "nations.player.description";
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
		return parent.getMain().getLanguage().getString("HelpList.Description.Description");
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

	@Override
	public AjaxPlugin getMain() {
		// TODO Auto-generated method stub
		return parent.getMain();
	}

}
