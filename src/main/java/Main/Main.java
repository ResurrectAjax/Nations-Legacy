package Main;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.yaml.snakeyaml.Yaml;

import GUI.GuiManager;
import General.GeneralMethods;
import Interfaces.ParentCommand;
import Listeners.JoinListener;
import Managers.CommandManager;
import Managers.FileManager;
import Persistency.MappingRepository;

/**
 * Main class
 * 
 * @author ResurrectAjax
 * */
public class Main extends JavaPlugin{
	private static Main INSTANCE;
	
	private MappingRepository mappingRepo;
	
	private CommandManager commandManager;
	private GuiManager guiManager;
	private FileManager fileManager;
	private FileConfiguration config, language, gui;
	
	private List<String> formats = new ArrayList<String>(Arrays.asList(
			"%nations_player_name%",
 			"%nations_player_rank%",
 			"%nations_player_killpoints%",
 			"%nations_nation_name%",
 			"%rel_nations_nation_name%",
 			"%rel_nations_enemy_nation%"
			));
	
	public List<String> getFormats() {
		return formats;
	}

	public void addFormats(List<String> formats) {
		this.formats.addAll(formats);
	}

	/**
	 * Static method to get the {@link Main} instance
	 * @return {@link Main} instance
	 * */
	public static Main getInstance() {
		return INSTANCE;
	}
	
	/**
	 * Enable plugin and load files/commands
	 * */
	public void onEnable() {
		
		loadFiles();
		loadListeners();
		
		
		TabCompletion tabCompleter = new TabCompletion(this);
		//set the tabCompleter
		for(ParentCommand command : commandManager.getCommands()) {
			getCommand(command.getName()).setTabCompleter(tabCompleter);
		}
	}
	
	/**
	 * Load all the classes that implement {@link Listener}
	 * */
	private void loadListeners() {
		getServer().getPluginManager().registerEvents(new JoinListener(this), this);
	}
	
	/**
	 * Handle command execution
	 * @param sender {@link CommandSender} who sent the command
	 * @param cmd {@link Command} sent command
	 * @param label {@link String} label of the command
	 * @param args {@link String}[] arguments
	 * */
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(sender instanceof Player) {
			Player player = (Player)sender;
			
			//check all the base commands in this plugin
			if(commandManager.getCommandByName(cmd.getName()) != null) {
				ParentCommand command = commandManager.getCommandByName(cmd.getName());
				runCommand(command, player, args);
			}
		}
		else {
			sender.sendMessage(GeneralMethods.format(language.getString("Command.Error.ByConsole.Message")));
		}
		return true;
	}
	
	/**
	 * Iterate over all commands and subcommands to find the right command to execute
	 * @param command {@link ParentCommand} where the method runs from
	 * @param player {@link Player} who sent the command
	 * @param args {@link String}[] arguments given with the command
	 * */
	private void runCommand(ParentCommand command, Player player, String[] args) {
		for(String arg : args) {
			String permissionNode = command.getPermissionNode();
			String noPermission = GeneralMethods.format(language.getString("Command.Error.NoPermission.Message"));
			
			if(permissionNode != null && !player.hasPermission(permissionNode)) {
				player.sendMessage(noPermission);
				return;
			}
			if(command.getSubCommands() == null || command.getSubCommands().isEmpty()) {
				command.perform(player, args);
				return;
			}
			
			for(ParentCommand subcommand : command.getSubCommands()) {
				if(subcommand.getName().equalsIgnoreCase(arg)) {
					runCommand(subcommand, player, args);
					return;
				}
			}
		}
		command.perform(player, args);
	}
	
	/**
	 * Get the command manager
	 * @return {@link CommandManager} manager
	 * */
	public CommandManager getCommandManager() {
		return commandManager;
	}
	
	/**
	 * Get the gui manager
	 * @return {@link CommandManager} manager
	 * */
	public GuiManager getGuiManager() {
		return guiManager;
	}

	/**
	 * Get the file manager
	 * @return {@link FileManager} manager
	 * */
	public FileManager getFileManager() {
		return fileManager;
	}

	/**
	 * Get the config file
	 * @return {@link FileConfiguration} config
	 * */
	public FileConfiguration getConfig() {
		return config;
	}
	
	/**
	 * Get the gui file
	 * @return {@link FileConfiguration} gui
	 * */
	public FileConfiguration getGuiConfig() {
		return gui;
	}

	/**
	 * Get the language file
	 * @return {@link FileConfiguration} language
	 * */
	public FileConfiguration getLanguage() {
		return language;
	}
	
	/**
	 * Get the Mapping repository
	 * @return {@link MappingRepository} repository
	 * */
	public MappingRepository getMappingRepo() {
		return mappingRepo;
	}
	
	/**
	 * Reload the {@link Yaml} files
	 * */
	public void reload() {
        fileManager.loadFiles();
        config = fileManager.getConfig("config.yml");
        language = fileManager.getConfig("language.yml");
        gui = fileManager.getConfig("gui.yml");
    }

	/**
	 * Load the {@link Yaml} files and classes
	 * */
	private void loadFiles() {
		INSTANCE = this;
		
		//load files
		fileManager = new FileManager(this);
        fileManager.loadFiles();
        config = fileManager.getConfig("config.yml");
        language = fileManager.getConfig("language.yml");
        gui = fileManager.getConfig("gui.yml");
        //files
        
        //load MappingRepository
        mappingRepo = new MappingRepository(this);
        //MappingRepository
		
		//load classes
		commandManager = new CommandManager(this);
		guiManager = new GuiManager(this);
		//classes
	}
}
