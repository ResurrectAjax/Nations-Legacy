package help;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import general.GeneralMethods;
import interfaces.ChildCommand;
import interfaces.ParentCommand;
import main.Main;

/**
 * Class for running a help command
 * 
 * @author ResurrectAjax
 * */
public class HelpCommand extends ParentCommand{
	protected Main main;
	private int helpSize = 0;
	
	/**
	 * Constructor of RaidHelpParent class<br>
	 * @param main instance of the {@link Main.Main} class
	 * */
	public HelpCommand(Main main) {
		this.main = main;
	}


	public String getName() {
		// TODO Auto-generated method stub
		return "help";
	}


	public String getSyntax() {
		// TODO Auto-generated method stub
		return null;
	}


	public String getDescription() {
		// TODO Auto-generated method stub
		return null;
	}


	public String[] getArguments(UUID uuid) {
		String[] arguments = new String[helpSize+1];
		for(int i = 0; i < helpSize; i++) {
			arguments[i] = i+"";
		}
		return null;
	}


	public List<ParentCommand> getSubCommands() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public void perform(CommandSender sender, String[] args) {

		ParentCommand command, mainCommand = main.getCommandManager().getCommandByName("nations");
		
		switch(args.length) {
		case 1:
			createList(sender, mainCommand, 1);
			break;
		case 2:
			if(GeneralMethods.isInteger(args[1])) {
				if(Integer.parseInt(args[1]) == 0) createList(sender, mainCommand, 1);
				else createList(sender, mainCommand, Integer.parseInt(args[1]));
				return;
			}
			command = getCommand(mainCommand, sender, args);
			createList(sender, command, 1);
			break;
		case 3:
			command = getCommand(mainCommand, sender, args);
			if(GeneralMethods.isInteger(args[2])) {
				if(Integer.parseInt(args[2]) == 0) createList(sender, command, 1);
				else createList(sender, command, Integer.parseInt(args[2]));
			}
			else createList(sender, command, 1);
			break;
		}	
	}
	
	/**
	 * Search for the command you need
	 * @param command {@link ParentCommand} command to start from
	 * @param player {@link Player} who sent the command
	 * @param args {@link String}[] arguments
	 * */
	private ParentCommand getCommand(ParentCommand command, CommandSender sender, String[] args) {
		for(String arg : args) {
			if(command.getSubCommands() == null || command.getSubCommands().isEmpty()) {
				return command;
			}
			
			for(ParentCommand subcommand : command.getSubCommands()) {
				if(subcommand.getName().equalsIgnoreCase(arg)) {
					getCommand(subcommand, sender, args);
					return subcommand;
				}
			}
		}
		return command;
	}
	
	/**
	 * Function for creating a help list
	 * @param player {@link Player} who sent the command
	 * @param command name of the sent command
	 * @param page list page number
	 * */
	public void createList(CommandSender sender, ParentCommand command, int page) {
		List<String> commandList = new ArrayList<String>();
		FileConfiguration language = main.getLanguage();
		int nr = 8;
		
		if(command.getSubCommands() != null) {
			List<ParentCommand> subcommands = new ArrayList<ParentCommand>(command.getSubCommands());
			for(ParentCommand subcommand : subcommands) {
				if(!(subcommand instanceof ChildCommand)) continue;
				if(subcommand.getName().equals(this.getName())) continue;
				
				String message;
				message = ChatColor.DARK_GRAY + "" + ChatColor.BOLD + "* " + ChatColor.AQUA + subcommand.getSyntax() + ChatColor.GRAY + " - " + ChatColor.WHITE + ChatColor.ITALIC + subcommand.getDescription();
				
				String permission = subcommand.getPermissionNode();
				if(permission != null && !sender.hasPermission(permission)) continue;
				
				commandList.add(message);
			}
		}
		
		if(commandList.size() % nr != 0) {
			helpSize = (commandList.size() / nr) + 1;
		}
		else {
			helpSize = (commandList.size() / nr);
		}
		
		sender.sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "Nations" + ChatColor.WHITE + " - " + ChatColor.YELLOW + command.getName() + ChatColor.RED + " " + (page <= 1 ? "" : page));
		for(int i = (page * nr)-nr; i < page * nr; i++) {
			if(commandList.size() > i) sender.sendMessage(commandList.get(i));	
		}
		
		if(page > helpSize) {
			sender.sendMessage(GeneralMethods.format(language.getString("Command.Help.EndOfHelp.Message")));
		}
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
	public boolean isConsole() {
		// TODO Auto-generated method stub
		return true;
	}
}
