package me.resurrectajax.nationslegacy.listeners;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import me.resurrectajax.ajaxplugin.general.GeneralMethods;
import me.resurrectajax.nationslegacy.main.Nations;
import me.resurrectajax.nationslegacy.persistency.MappingRepository;
import me.resurrectajax.nationslegacy.persistency.NationMapping;
import me.resurrectajax.nationslegacy.persistency.PlayerMapping;

public class PrefixListener implements Listener{
	
	private Nations main;
	public PrefixListener(Nations main) {
		this.main = main;
	}

	
	@EventHandler
	public void onPlayerChat(AsyncPlayerChatEvent event) {
		MappingRepository mappingRepo = main.getMappingRepo();
		PlayerMapping playerMap = mappingRepo.getPlayerByUUID(event.getPlayer().getUniqueId());
		NationMapping nation = mappingRepo.getNationByID(playerMap.getNationID());
		FileConfiguration config = main.getConfig();
		
		boolean hasPrefix = config.getBoolean("Nations.Prefix.Enabled");
		if(!hasPrefix) return;
		
		String playerName = event.getPlayer().getDisplayName(), message = event.getMessage();
		String total = String.format(config.getString("Nations.Prefix.Format") + ": %s", nation != null ? nation.getName() : "&2Wilderness", playerName, message);
		event.setFormat(GeneralMethods.format(total));
	}
}
