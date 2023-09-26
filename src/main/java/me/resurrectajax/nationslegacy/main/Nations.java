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
import me.resurrectajax.ajaxplugin.managers.PermissionManager;
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
import me.resurrectajax.nationslegacy.persistency.PlayerMapping;
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
			"nations_rel_nation_name",
			"nations_nation_name",
			"nations_transfer_amount",
			"nations_player_argument",
			"nations_player_name",
			"nations_rel_player_name",
			"nations_remaining_chunkamount",
			"nations_gained_chunks",
			"nations_rel_player_rank"
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
		
		loadPermissions();
		me.resurrectajax.nationslegacy.ranking.Rank.reloadRanks();
    }

	public void loadPermissions() {
		super.setPermissionManager(new PermissionManager(this));
		
		for(Player player : Bukkit.getOnlinePlayers()) {
			reloadPermissions(player);
		}
	}
	
	public void reloadPermissions(Player player) {
		PermissionManager manager = getPermissionManager();
		manager.clearStartingWith(player, "nations.player");
		
		MappingRepository mappingRepo = getMappingRepo();
		PlayerMapping playMap = mappingRepo.getPlayerByUUID(player.getUniqueId());
		
		List<String> permissions = playMap.getRank().getPermissions();
		permissions.forEach(el -> {
			if(el.contains("-")) manager.denyPermission(player, el);
			else manager.grantPermission(player, el);
		});
	}
	
	/**
	 * Load the {@link Yaml} files and classes
	 * */
	public void loadFiles() {
		Nations.INSTANCE = this;
        
		super.files = FileManager.loadFiles( this,
				"config.yml",
				"language.yml"
				);
		
		super.setMappingRepository(new MappingRepository(this));
		
		//load classes
		super.setCommandManager(new CommandManager(new NationsCommand(this)));
		
		//load files
		Bukkit.getPluginManager().callEvent(new ReloadEvent());
        //files
		
	}
	
	private void hookIntoPlaceholderAPI() {
		Plugin placeHolderAPI = Bukkit.getPluginManager().getPlugin("PlaceholderAPI");
		if(placeHolderAPI == null || !placeHolderAPI.isEnabled()) return;
		new CustomPlaceHolders().register();
		new CustomRelationalPlaceholders().register();	
	}
}
