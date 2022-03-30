package Listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import Main.Main;

public class JoinListener implements Listener{

	private Main main;
	public JoinListener(Main main) {
		this.main = main;
	}
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		
	}
}
