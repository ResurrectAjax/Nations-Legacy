package commands;

import java.util.Arrays;
import java.util.UUID;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import commands.admin.AdminCommands;
import commands.alliance.AllyCommand;
import commands.claim.ClaimChunkCommand;
import commands.claim.UnclaimChunkCommand;
import commands.create.CreateNationCommand;
import commands.description.DescriptionCommand;
import commands.disband.DisbandNationCommand;
import commands.home.DeleteHomeCommand;
import commands.home.HomeCommand;
import commands.home.SetHomeCommand;
import commands.info.NationInfoCommand;
import commands.invite.NationInviteCommand;
import commands.invite.NationInviteAcceptCommand;
import commands.invite.NationInviteDenyCommand;
import commands.list.ListCommand;
import commands.map.MapCommand;
import commands.war.WarCommand;
import commands.who.WhoCommand;
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
				new HelpCommand(this),
				new AdminCommands(this),
				new CreateNationCommand(this),
				new NationInfoCommand(this),
				new WhoCommand(this),
				new DisbandNationCommand(this),
				new ClaimChunkCommand(this),
				new UnclaimChunkCommand(this),
				new DescriptionCommand(this),
				new NationInviteCommand(this),
				new NationInviteAcceptCommand(this),
				new NationInviteDenyCommand(this),
				new MapCommand(this),
				new AllyCommand(this),
				new WarCommand(this),
				new ListCommand(this),
				new SetHomeCommand(this),
				new DeleteHomeCommand(this),
				new HomeCommand(this)
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
			new HelpCommand(mainCommand).sendList(sender, mainCommand, 1);
			break;
		}
	}
}
