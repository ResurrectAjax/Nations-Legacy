package managers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import commands.NationsCommand;
import interfaces.ParentCommand;
import main.Main;

/**
 * Manages all the base commands
 * @author ResurrectAjax
 * */
public class CommandManager {
	private List<ParentCommand> commands = new ArrayList<ParentCommand>();
	private HashMap<String, String> lastArg = new HashMap<String, String>();
	
	private Set<UUID> isClaiming = new HashSet<UUID>();
	private Set<UUID> isUnclaiming = new HashSet<UUID>();
	
	private HashMap<UUID, Set<Integer>> playerInvites = new HashMap<UUID, Set<Integer>>();
	/**
	 * Constructor of CommandManager<br>
	 * Loads all the base commands
	 * @param main instance of the {@link me.ResurrectAjax.Main.Main} class
	 * */
	public CommandManager(Main main) {
		commands = new ArrayList<ParentCommand>(Arrays.asList(
				new NationsCommand(main)
				));
	}
	
	/**
	 * Gets a list of all the base commands
	 * @return list of all the base commands
	 * */
	public List<ParentCommand> getCommands() {
		return commands;
	}
	
	/**
	 * Gets a list of all the base command names
	 * @return list of all the command names
	 * */
	public List<String> getStringList() {
		List<String> commandStrings = new ArrayList<String>();
		for(ParentCommand command : commands) {
			commandStrings.add(command.getName().toLowerCase());
		}
		return commandStrings;
	}
	
	/**
	 * Gets the base command by name
	 * @param name name of the command
	 * @return instance of {@link ParentCommand.ResurrectAjax.Commands.Managers.CommandInterface}
	 * */
	public ParentCommand getCommandByName(String name) {
		for(ParentCommand command : commands) {
			return getCommand(name, command);
		}	
		return null;
	}
	
	private ParentCommand getCommand(String name, ParentCommand command) {
		if(getStringList().contains(name.toLowerCase())) {
			if(command.getName().equalsIgnoreCase(name)) return command;
		}
		else if(command.getSubCommands() == null) return null;
		else if(command.getSubCommands().stream().map(el -> el.getName()).collect(Collectors.toList()).contains(name)) {
			for(ParentCommand subcommands : command.getSubCommands()) {
				if(subcommands.getName().equalsIgnoreCase(name)) return subcommands;
			}	
		}
		else {
			for(ParentCommand subcommands : command.getSubCommands()) {
				getCommand(name, subcommands);
			}	
		}
		return null;
	}

	public String getLastArg(String sender) {
		return lastArg.get(sender);
	}

	public void setLastArg(String sender, String arg) {
		this.lastArg.put(sender, arg);
	}

	public Set<UUID> getClaimingSet() {
		return isClaiming;
	}

	public void setIsClaiming(UUID player) {
		this.isClaiming.add(player);
	}

	public Set<UUID> getUnclaimingSet() {
		return isUnclaiming;
	}

	public void setIsUnclaiming(UUID player) {
		this.isUnclaiming.add(player);
	}

	public HashMap<UUID, Set<Integer>> getPlayerInvites() {
		return playerInvites;
	}

	public void addPlayerInvite(Integer nationID, UUID receiver) {
		Set<Integer> invites = new HashSet<Integer>();
		if(this.playerInvites.containsKey(receiver)) invites.addAll(this.playerInvites.get(receiver));
		invites.add(nationID);
		
		this.playerInvites.put(receiver, invites);
	}
	public void removePlayerInvite(Integer nationID, UUID receiver) {
		if(!this.playerInvites.containsKey(receiver)) return;
		this.playerInvites.get(receiver).remove(nationID);
	}
	
}
