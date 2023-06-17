package me.resurrectajax.nationslegacy.commands;

import java.util.Arrays;
import java.util.UUID;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.resurrectajax.ajaxplugin.commands.MainCommand;
import me.resurrectajax.ajaxplugin.help.HelpCommand;
import me.resurrectajax.ajaxplugin.interfaces.ParentCommand;
import me.resurrectajax.nationslegacy.commands.admin.AdminCommands;
import me.resurrectajax.nationslegacy.commands.alliance.AllyCommand;
import me.resurrectajax.nationslegacy.commands.claim.ClaimChunkCommand;
import me.resurrectajax.nationslegacy.commands.claim.UnclaimChunkCommand;
import me.resurrectajax.nationslegacy.commands.create.CreateNationCommand;
import me.resurrectajax.nationslegacy.commands.description.DescriptionCommand;
import me.resurrectajax.nationslegacy.commands.disband.DisbandNationCommand;
import me.resurrectajax.nationslegacy.commands.flag.FlagCommand;
import me.resurrectajax.nationslegacy.commands.home.DeleteHomeCommand;
import me.resurrectajax.nationslegacy.commands.home.HomeCommand;
import me.resurrectajax.nationslegacy.commands.home.SetHomeCommand;
import me.resurrectajax.nationslegacy.commands.info.NationInfoCommand;
import me.resurrectajax.nationslegacy.commands.invite.NationInviteAcceptCommand;
import me.resurrectajax.nationslegacy.commands.invite.NationInviteCancel;
import me.resurrectajax.nationslegacy.commands.invite.NationInviteCommand;
import me.resurrectajax.nationslegacy.commands.invite.NationInviteDenyCommand;
import me.resurrectajax.nationslegacy.commands.kick.KickCommand;
import me.resurrectajax.nationslegacy.commands.leave.LeaveCommand;
import me.resurrectajax.nationslegacy.commands.list.ListCommand;
import me.resurrectajax.nationslegacy.commands.map.MapCommand;
import me.resurrectajax.nationslegacy.commands.ranks.DemoteCommand;
import me.resurrectajax.nationslegacy.commands.ranks.PromoteCommand;
import me.resurrectajax.nationslegacy.commands.transfer.TransferClaimCommand;
import me.resurrectajax.nationslegacy.commands.war.WarCommand;
import me.resurrectajax.nationslegacy.commands.who.WhoCommand;
import me.resurrectajax.nationslegacy.general.GeneralMethods;
import me.resurrectajax.nationslegacy.main.Nations;

public class NationsCommand extends MainCommand{
	
	private Nations main;
	public NationsCommand(Nations main) {
		super(main);
		this.main = main;
		
		super.setPluginName("nations");
		
		super.setSubcommands(Arrays.asList(
				new HelpCommand(this),
				new AdminCommands(this),
				new CreateNationCommand(this),
				new NationInfoCommand(this),
				new WhoCommand(this),
				new DisbandNationCommand(this),
				new ClaimChunkCommand(this),
				new UnclaimChunkCommand(this),
				new TransferClaimCommand(this),
				new DescriptionCommand(this),
				new NationInviteCommand(this),
				new NationInviteAcceptCommand(this),
				new NationInviteDenyCommand(this),
				new NationInviteCancel(this),
				new MapCommand(this),
				new AllyCommand(this),
				new WarCommand(this),
				new ListCommand(this),
				new SetHomeCommand(this),
				new DeleteHomeCommand(this),
				new HomeCommand(this),
				new PromoteCommand(this),
				new DemoteCommand(this),
				new LeaveCommand(this),
				new KickCommand(this),
				new FlagCommand(this)
				));
	}

	@Override
	public void perform(CommandSender sender, String[] args) {
		switch(args.length) {
		case 1:
			UUID uuid = null;
			if(sender instanceof Player) uuid = ((Player)sender).getUniqueId();
			if(!Arrays.asList(getArguments(uuid)).contains(args[0].toLowerCase())) sender.sendMessage(GeneralMethods.getBadSyntaxMessage(main, getSyntax()));
			break;
		default:
			ParentCommand mainCommand = main.getCommandManager().getMainCommand();
			new HelpCommand(mainCommand).sendList(sender, mainCommand, 1);
			break;
		}
	}
}
