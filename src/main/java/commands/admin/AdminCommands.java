package commands.admin;
import java.util.Arrays;

import commands.admin.disband.DisbandNationCommand;
import commands.admin.reload.ReloadCommand;
import me.resurrectajax.ajaxplugin.commands.AdminCommand;
import me.resurrectajax.ajaxplugin.help.HelpCommand;
import me.resurrectajax.ajaxplugin.interfaces.ParentCommand;

public class AdminCommands extends AdminCommand{
	public AdminCommands(ParentCommand parent) {
		super(parent);
		
		super.setPluginName("nations");
		
		super.setSubcommands(Arrays.asList(
				new HelpCommand(this),
				new DisbandNationCommand(this),
				new ReloadCommand(this)
				));
	}

}
