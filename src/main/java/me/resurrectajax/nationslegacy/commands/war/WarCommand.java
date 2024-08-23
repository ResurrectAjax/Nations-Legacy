package me.resurrectajax.nationslegacy.commands.war;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import me.resurrectajax.ajaxplugin.help.HelpCommand;
import me.resurrectajax.ajaxplugin.interfaces.ParentCommand;
import me.resurrectajax.ajaxplugin.plugin.AjaxPlugin;
import me.resurrectajax.nationslegacy.commands.war.add.WarAdd;
import me.resurrectajax.nationslegacy.commands.war.info.WarInfoCommand;
import me.resurrectajax.nationslegacy.commands.war.truce.TruceAcceptCommand;
import me.resurrectajax.nationslegacy.commands.war.truce.TruceCancelCommand;
import me.resurrectajax.nationslegacy.commands.war.truce.TruceDenyCommand;
import me.resurrectajax.nationslegacy.commands.war.truce.TruceCommand;

public class WarCommand extends ParentCommand{

	private ParentCommand parent;
	private HashMap<Integer, Set<Integer>> truceRequests = new HashMap<Integer, Set<Integer>>();
	
	public WarCommand(ParentCommand parent) {
		this.parent = parent;
	}
	
	@Override
	public String getPermissionNode() {
		// TODO Auto-generated method stub
		return "nations.player.war";
	}

	@Override
	public boolean hasTabCompletion() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "war";
	}

	@Override
	public String getSyntax() {
		// TODO Auto-generated method stub
		return "/nations war <subcommand>";
	}

	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		return parent.getMain().getLanguage().getString("HelpList.War.Description");
	}

	@Override
	public List<ParentCommand> getSubCommands() {
		return Arrays.asList(
				new HelpCommand(this),
				new WarAdd(this),
				new TruceCommand(this),
				new TruceAcceptCommand(this),
				new TruceDenyCommand(this),
				new TruceCancelCommand(this),
				new WarInfoCommand(this)
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
	
	public HashMap<Integer, Set<Integer>> getTruceRequests() {
		return truceRequests;
	}
	
	public void addTruceRequest(Integer receiverID, Integer senderID) {
		Set<Integer> requestIDs = new HashSet<Integer>();
		if(this.truceRequests.containsKey(receiverID)) requestIDs = new HashSet<Integer>(this.truceRequests.get(receiverID));
		requestIDs.add(senderID);
		this.truceRequests.put(receiverID, requestIDs);
	}
	
	public void removeTruceRequest(Integer receiverID, Integer senderID) {
		if(!this.truceRequests.containsKey(receiverID)) return;
		this.truceRequests.get(receiverID).remove(senderID);
	}

	@Override
	public AjaxPlugin getMain() {
		// TODO Auto-generated method stub
		return parent.getMain();
	}

}
