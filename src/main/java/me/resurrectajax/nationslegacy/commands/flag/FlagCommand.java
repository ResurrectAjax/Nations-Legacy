package me.resurrectajax.nationslegacy.commands.flag;

import java.util.Arrays;
import java.util.List;

import me.resurrectajax.ajaxplugin.interfaces.ParentCommand;
import me.resurrectajax.ajaxplugin.plugin.AjaxPlugin;
import me.resurrectajax.nationslegacy.commands.flag.info.FlagInfoCommand;
import me.resurrectajax.nationslegacy.commands.flag.set.FlagSetCommand;

public class FlagCommand extends ParentCommand{

	private ParentCommand parent;
	public FlagCommand(ParentCommand parent) {
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
		return "flag";
	}

	@Override
	public String getSyntax() {
		// TODO Auto-generated method stub
		return "/nations flag <subcommand>";
	}

	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		return "Execute a flag command";
	}

	@Override
	public List<ParentCommand> getSubCommands() {
		// TODO Auto-generated method stub
		return Arrays.asList(
				new FlagInfoCommand(this),
				new FlagSetCommand(this)
				);
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

	@Override
	public AjaxPlugin getMain() {
		// TODO Auto-generated method stub
		return parent.getMain();
	}

}
