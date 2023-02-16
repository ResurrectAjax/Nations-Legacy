package commands;

import java.util.Arrays;

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
import commands.map.Map;
import commands.war.WarCommand;
import commands.who.Who;
import main.Main;
import me.resurrectajax.ajaxplugin.commands.MainCommand;
import me.resurrectajax.ajaxplugin.help.HelpCommand;

public class NationsCommand extends MainCommand{
	public NationsCommand(Main main) {
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
				new WarCommand(this)
				));
	}
	
	@Override
	public String getName() {
		return "nations";
	}

}
