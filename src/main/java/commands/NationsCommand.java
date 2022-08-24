package commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import commands.admin.AdminCommand;
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
import commands.who.Who;
import help.HelpCommand;
import interfaces.ParentCommand;
import main.Main;

public class NationsCommand extends ParentCommand{
	private List<ParentCommand> subcommands;
	private Main main;
	
	public NationsCommand(Main main) {
		this.main = main;
		subcommands = new ArrayList<ParentCommand>(Arrays.asList(
				new CreateNation(this),
				new NationInfo(this),
				new Who(this),
				new DisbandNation(this),
				new AdminCommand(this),
				new HelpCommand(this),
				new ClaimChunk(this),
				new UnclaimChunk(this),
				new DescriptionCommand(this),
				new NationInvite(this),
				new NationInviteAccept(this),
				new NationInviteDeny(this),
				new Map(this),
				new AllyCommand(this)
				));
	}
	
	@Override
	public Main getMain() {
		return main;
	}
	
	@Override
	public String getPermissionNode() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "nations";
	}

	@Override
	public String getSyntax() {
		// TODO Auto-generated method stub
		return "/nations <subcommand>";
	}

	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		return "Runs the nations command";
	}

	@Override
	public List<ParentCommand> getSubCommands() {
		// TODO Auto-generated method stub
		return subcommands;
	}

	@Override
	public boolean hasTabCompletion() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isConsole() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public ParentCommand getParentCommand() {
		// TODO Auto-generated method stub
		return null;
	}

}
