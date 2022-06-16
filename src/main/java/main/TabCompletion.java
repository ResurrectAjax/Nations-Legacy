package main;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import interfaces.ParentCommand;
import interfaces.SubChildCommand;
import managers.CommandManager;

/**
 * Class for handling tab completions
 * 
 * @author ResurrectAjax
 * */
public class TabCompletion implements TabCompleter{
	private CommandManager commandManager;
	/**
	 * Constructor<br>
	 * @param main instance of the {@link Main.Main} class
	 * */
	public TabCompletion(Main main) {
		commandManager = main.getCommandManager();
	}
	
	/**
	 * Code run when player uses tab completion
	 * 
	 * @param sender {@link CommandSender} who sent the command
	 * @param command {@link Command} sent by sender
	 * @param alias {@link String} command alias
	 * @param args {@link String}[] arguments
	 * */
	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		
		List<String> tabCommands = new ArrayList<String>();
		
		UUID uuid = null;
		if(sender instanceof Player) uuid = ((Player) sender).getUniqueId();
		
		if(commandManager.getStringList().contains(command.getName().toLowerCase())) {
			ParentCommand commands = commandManager.getCommandByName(command.getName());
			if(args.length > 1 && args[0] != null) {
				getCommandArgs(uuid, commands, args[args.length-2], tabCommands);
			}
			else {
				tabCommands.addAll(Arrays.asList(commands.getArguments(uuid)));
				for(ParentCommand comman : commands.getSubCommands()) {
					if(comman.getPermissionNode() != null && !sender.hasPermission(comman.getPermissionNode())) tabCommands.remove(comman.getName());
					if(!comman.hasTabCompletion()) tabCommands.remove(comman.getName());
				}
			}
		}
			
		
		return tabCommands;
	}
	
	/**
	 * Get the arguments of a command
	 * @param uuid {@link UUID} of the player who is using tabcompletion
	 * @param command {@link ParentCommand} of the current command
	 * @param arg last argument the player wrote
	 * @param tabCommands {@link List} of arguments to show in the tab section
	 * */
	public void getCommandArgs(UUID uuid, ParentCommand command, String arg, List<String> tabCommands) {
		if(command instanceof SubChildCommand) {
			SubChildCommand subchild = (SubChildCommand) command;
			List<String> args = Arrays.asList(subchild.getArguments(uuid)), subargs = Arrays.asList(subchild.getSubArguments());
			if(args.contains(arg)) tabCommands.addAll(subargs);
			return;
		}
		
		if (command.getSubCommands() == null || command.getSubCommands().isEmpty()) return;
		for(ParentCommand subcommand : command.getSubCommands()) {
			if(subcommand.getArguments(uuid) == null) continue;
			if(subcommand.getName().equalsIgnoreCase(arg)) {
				String permission = subcommand.getPermissionNode();
				if(uuid != null && permission != null && !Bukkit.getPlayer(uuid).hasPermission(permission)) continue;
				if(!subcommand.hasTabCompletion()) continue;
				tabCommands.addAll(Arrays.asList(subcommand.getArguments(uuid)));
				return;
			}
			getCommandArgs(uuid, subcommand, arg, tabCommands);
		}
	}
}
