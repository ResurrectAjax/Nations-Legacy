package listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import main.Main;
import persistency.MappingRepository;

public class JoinListener implements Listener{

	private Main main;
	public JoinListener(Main main) {
		this.main = main;
	}
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		MappingRepository mappingRepo = main.getMappingRepo();
		if(mappingRepo.getPlayerByUUID(event.getPlayer().getUniqueId()) == null) mappingRepo.addPlayer(event.getPlayer());
	}
}
