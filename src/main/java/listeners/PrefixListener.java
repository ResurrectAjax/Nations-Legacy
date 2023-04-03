package listeners;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import main.Main;
import me.resurrectajax.ajaxplugin.general.GeneralMethods;
import persistency.MappingRepository;
import persistency.NationMapping;
import persistency.PlayerMapping;

public class PrefixListener implements Listener{
	
	private Main main;
	public PrefixListener(Main main) {
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
