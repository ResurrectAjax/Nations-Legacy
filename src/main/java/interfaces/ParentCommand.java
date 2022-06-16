package interfaces;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import commands.NationsCommand;
import commands.admin.AdminCommand;
import general.GeneralMethods;
import help.HelpCommand;
import main.Main;

/**
 * Abstract class for commands that don't have any specific code
 * This is the frame for all the parent command classes
 * @author ResurrectAjax
 * */
public abstract class ParentCommand {
	/**
	 * Gets the permission node of the command
	 * @return permission node of the command
	 * */
	public abstract String getPermissionNode();
	
	/**
	 * Checks if this command has an inventory GUI
	 * @return true/false
	 * */
	public abstract boolean hasTabCompletion();
	
	/**
	 * Gets the name of the command
	 * @return name of the command
	 * */
	public abstract String getName();
	
	/**
	 * Gets the syntax of the command
	 * @return syntax of how the command should be used
	 * */
	public abstract String getSyntax();
	
	/**
	 * Gets the description of the command
	 * @return description of the command
	 * */
	public abstract String getDescription();
	
	/**
	 * Gets the arguments of the command
	 * @param uuid uuid of the player who sent the command
	 * @return array of the commands arguments
	 * */
	public String[] getArguments(UUID uuid) {
		List<String> arguments = new ArrayList<String>();
		
		Player player = Bukkit.getPlayer(uuid);
		for(int i = 0; i < getSubCommands().size(); i++) {
			String permission = getSubCommands().get(i).getPermissionNode();
			if(uuid != null) if(permission != null && !player.hasPermission(permission)) continue;
			arguments.add(getSubCommands().get(i).getName());
		}
		return arguments.toArray(new String[arguments.size()]);	
	}
	
	
	/**
	 * Gets all the subcommands of the command
	 * @return list of the CommandInterface class which only get called by this class
	 * */
	public abstract List<ParentCommand> getSubCommands();
	
	/**
	 * Standard method for executing code
	 * @param player player who sent the command
	 * @param args array of arguments the player sent
	 * */
	public void perform(CommandSender sender, String[] args) {
		if(this instanceof NationsCommand) perform(sender, args, 1);
		else if(this instanceof AdminCommand) perform(sender, args, 2);
	}
	
	private void perform(CommandSender sender, String[] args, int neededArgs) {
		if(args.length == neededArgs) {
			UUID uuid = null;
			if(sender instanceof Player) uuid = ((Player)sender).getUniqueId();
			if(!Arrays.asList(getArguments(uuid)).contains(args[0].toLowerCase())) sender.sendMessage(GeneralMethods.getBadSyntaxMessage(getSyntax()));
		}
		else {
			Main main = Main.getInstance();
			ParentCommand baseCommand = main.getCommandManager().getCommandByName(this.getName());
			new HelpCommand(main).createList(sender, baseCommand, 1);
		}
	}
	
	/**
	 * Check if the command can be executed by console
	 * @return true|false
	 * */
	public abstract boolean isConsole();
}
