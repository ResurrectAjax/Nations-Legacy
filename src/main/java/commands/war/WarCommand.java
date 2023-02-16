package commands.war;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import commands.war.add.WarAdd;
import commands.war.truce.TruceAccept;
import commands.war.truce.TruceCancel;
import commands.war.truce.TruceDeny;
import commands.war.truce.WarTruce;
import me.resurrectajax.ajaxplugin.help.HelpCommand;
import me.resurrectajax.ajaxplugin.interfaces.ParentCommand;

public class WarCommand extends ParentCommand{

	private ParentCommand parent;
	private HashMap<Integer, Set<Integer>> truceRequests = new HashMap<Integer, Set<Integer>>();
	
	public WarCommand(ParentCommand parent) {
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
		return "Run a war command";
	}

	@Override
	public List<ParentCommand> getSubCommands() {
		return Arrays.asList(
				new WarAdd(this),
				new WarTruce(this),
				new TruceAccept(this),
				new TruceDeny(this),
				new TruceCancel(this),
				new HelpCommand(this)
				);
	}

	@Override
	public boolean isConsole() {
		// TODO Auto-generated method stub
		return false;
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

}
