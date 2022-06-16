package managers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

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
			if(getStringList().contains(name.toLowerCase())) {
				if(command.getName().equalsIgnoreCase(name)) {
					return command;
				}	
			}
			else {
				if(command.getSubCommands() != null) {
					for(ParentCommand subcommands : command.getSubCommands()) {
						if(subcommands.getName().equalsIgnoreCase(name)) {
							return subcommands;
						}
					}	
				}
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
	
	
}
