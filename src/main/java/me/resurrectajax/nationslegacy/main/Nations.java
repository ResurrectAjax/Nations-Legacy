package me.resurrectajax.nationslegacy.main;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.yaml.snakeyaml.Yaml;

import me.resurrectajax.ajaxplugin.managers.CommandManager;
import me.resurrectajax.ajaxplugin.managers.FileManager;
import me.resurrectajax.ajaxplugin.plugin.AjaxPlugin;
import me.resurrectajax.nationslegacy.commands.NationsCommand;
import me.resurrectajax.nationslegacy.events.ReloadEvent;
import me.resurrectajax.nationslegacy.general.GeneralMethods;
import me.resurrectajax.nationslegacy.listeners.ClaimListener;
import me.resurrectajax.nationslegacy.listeners.ItemListener;
import me.resurrectajax.nationslegacy.listeners.JoinLeaveListener;
import me.resurrectajax.nationslegacy.listeners.PlayerInteractListener;
import me.resurrectajax.nationslegacy.listeners.PlayerKillListener;
import me.resurrectajax.nationslegacy.listeners.PlayerMoveListener;
import me.resurrectajax.nationslegacy.listeners.PrefixListener;
import me.resurrectajax.nationslegacy.persistency.MappingRepository;
import me.resurrectajax.nationslegacy.placeholderapi.CustomPlaceHolders;
import me.resurrectajax.nationslegacy.placeholderapi.CustomRelationalPlaceholders;

/**
 * Main class
 * 
 * @author ResurrectAjax
 * */
public class Nations extends AjaxPlugin{
	private static Nations INSTANCE;
	
	private List<String> formats = new ArrayList<String>(Arrays.asList(
 			"%nations_player_argument%",
			"%nations_player_name%",
 			"%nations_player_rank%",
 			"%nations_player_killpoints%",
 			"%nations_nation_name%",
 			"%nations_nation_description%",
 			"%nations_remaining_chunkamount%",
 			"%rel_nations_syntax%",
 			"%rel_nations_nation_name%",
 			"%rel_nations_nation1_name%",
 			"%rel_nations_nation2_name%",
 			"%rel_nations_enemy_nation%"
			));
	
	public List<String> getFormats() {
		return formats;
	}

	public void addFormats(List<String> formats) {
		this.formats.addAll(formats);
	}

	/**
	 * Static method to get the {@link Nations} instance
	 * @return {@link Nations} instance
	 * */
	public static Nations getInstance() {
		return INSTANCE;
	}
	
	/**
	 * Enable plugin and load files/commands
	 * */
	@Override
	public void onEnable() {
		super.onEnable();
		
		for(Player player : Bukkit.getOnlinePlayers()) {
			if(getMappingRepo().getPlayerByUUID(player.getUniqueId()) == null) getMappingRepo().addPlayer(player);	
			getMappingRepo().getScoreboardManager().updateScoreboard(player);
			GeneralMethods.updatePlayerTab(player);
		}
		
		hookIntoPlaceholderAPI();
	}
	
	public void loadListeners() {
		getServer().getPluginManager().registerEvents(new JoinLeaveListener(this), this);
		getServer().getPluginManager().registerEvents(new PlayerMoveListener(this), this);
		getServer().getPluginManager().registerEvents(new ClaimListener(this), this);
		getServer().getPluginManager().registerEvents(new PlayerKillListener(this), this);
		getServer().getPluginManager().registerEvents(new PlayerInteractListener(this), this);
		getServer().getPluginManager().registerEvents(new PrefixListener(this), this);
		getServer().getPluginManager().registerEvents(new ItemListener(), this);
	}

	/**
	 * Get the config file
	 * @return {@link FileConfiguration} config
	 * */
	public FileConfiguration getConfig() {
		return files.get("config.yml");
	}

	/**
	 * Get the language file
	 * @return {@link FileConfiguration} language
	 * */
	public FileConfiguration getLanguage() {
		return files.get("language.yml");
	}
	
	/**
	 * Get the Mapping repository
	 * @return {@link MappingRepository} repository
	 * */
	public MappingRepository getMappingRepo() {
		return (MappingRepository) super.getMappingRepo();
	}
	
	/**
	 * Reload the plugin
	 * */
	public void reload() {
		super.files = FileManager.loadFiles( this,
				"config.yml",
				"language.yml"
				);
    }

	/**
	 * Load the {@link Yaml} files and classes
	 * */
	public void loadFiles() {
		Nations.INSTANCE = this;
		
		//load files
		Bukkit.getPluginManager().callEvent(new ReloadEvent());
        //files
        
		super.setMappingRepository(new MappingRepository(this));
		
		//load classes
		super.setCommandManager(new CommandManager(new NationsCommand(this)));
	}
	
	private void hookIntoPlaceholderAPI() {
		Plugin placeHolderAPI = Bukkit.getPluginManager().getPlugin("PlaceholderAPI");
		if(placeHolderAPI == null || !placeHolderAPI.isEnabled()) return;
		new CustomPlaceHolders().register();
		new CustomRelationalPlaceholders().register();	
	}
}
