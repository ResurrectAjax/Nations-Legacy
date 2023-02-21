package commands;

import java.util.Arrays;
import java.util.UUID;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import commands.admin.AdminCommands;
import commands.alliance.AllyCommand;
import commands.claim.ClaimChunk;
import commands.claim.UnclaimChunk;
import commands.create.CreateNation;
import commands.description.DescriptionCommand;
import commands.disband.DisbandNation;
import commands.info.NationInfo;
import commands.invite.NationInvite;
import commands.invite.NationInviteAccept;
import commands.invite.NationInviteDeny;
import commands.list.ListCommand;
import commands.map.Map;
import commands.war.WarCommand;
import commands.who.Who;
import general.GeneralMethods;
import main.Main;
import me.resurrectajax.ajaxplugin.commands.MainCommand;
import me.resurrectajax.ajaxplugin.help.HelpCommand;
import me.resurrectajax.ajaxplugin.interfaces.ParentCommand;
import me.resurrectajax.ajaxplugin.plugin.AjaxPlugin;

public class NationsCommand extends MainCommand{
	public NationsCommand(Main main) {
		super.setName("nations");
		
		super.setSubCommands(Arrays.asList(
				new CreateNation(this),
				new NationInfo(this),
				new Who(this),
				new DisbandNation(this),
				new AdminCommands(this),
				new HelpCommand(this),
				new ClaimChunk(this),
				new UnclaimChunk(this),
				new DescriptionCommand(this),
				new NationInvite(this),
				new NationInviteAccept(this),
				new NationInviteDeny(this),
				new Map(this),
				new AllyCommand(this),
				new WarCommand(this),
				new ListCommand(this)
				));
	}

	@Override
	public void perform(CommandSender sender, String[] args) {
		switch(args.length) {
		case 1:
			UUID uuid = null;
			if(sender instanceof Player) uuid = ((Player)sender).getUniqueId();
			if(!Arrays.asList(getArguments(uuid)).contains(args[0].toLowerCase())) sender.sendMessage(GeneralMethods.getBadSyntaxMessage(getSyntax()));
			break;
		default:
			AjaxPlugin main = AjaxPlugin.getInstance();
			ParentCommand mainCommand = main.getCommandManager().getMainCommand();
			new HelpCommand(mainCommand).createList(sender, mainCommand, 1);
			break;
		}
	}
}
