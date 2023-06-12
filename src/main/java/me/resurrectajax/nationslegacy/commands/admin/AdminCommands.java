package me.resurrectajax.nationslegacy.commands.admin;
import java.util.Arrays;

import me.resurrectajax.ajaxplugin.commands.admin.AdminCommand;
import me.resurrectajax.ajaxplugin.help.HelpCommand;
import me.resurrectajax.ajaxplugin.interfaces.ParentCommand;
import me.resurrectajax.ajaxplugin.plugin.AjaxPlugin;
import me.resurrectajax.nationslegacy.commands.admin.disband.DisbandNationCommand;
import me.resurrectajax.nationslegacy.commands.admin.join.JoinNationCommand;
import me.resurrectajax.nationslegacy.commands.admin.reload.ReloadCommand;

public class AdminCommands extends AdminCommand{
	private AjaxPlugin main;
	public AdminCommands(ParentCommand parent) {
		super(parent);
		this.main = parent.getMain();
		super.setPluginName("nations");
		super.setSubcommands(Arrays.asList(
				new HelpCommand(this),
				new DisbandNationCommand(this),
				new ReloadCommand(this),
				new JoinNationCommand(this)
				));
	}

	@Override
	public AjaxPlugin getMain() {
		// TODO Auto-generated method stub
		return main;
	}

}
