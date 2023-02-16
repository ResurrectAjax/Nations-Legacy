package commands.admin;
import java.util.Arrays;

import commands.admin.disband.DisbandNation;
import commands.admin.reload.Reload;
import me.resurrectajax.ajaxplugin.commands.AdminCommand;
import me.resurrectajax.ajaxplugin.help.HelpCommand;
import me.resurrectajax.ajaxplugin.interfaces.ParentCommand;

public class AdminCommands extends AdminCommand{
	public AdminCommands(ParentCommand parent) {
		super(parent);
		
		super.setSubcommands(Arrays.asList(
				new DisbandNation(this),
				new HelpCommand(this),
				new Reload(this)
				));
	}

}
