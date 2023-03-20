package events.nation.kick;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;

import events.nation.NationEvent;
import general.GeneralMethods;
import main.Main;
import persistency.NationMapping;
import persistency.PlayerMapping;

public class KickFromNationEvent extends NationEvent{

	public KickFromNationEvent(NationMapping nation, CommandSender sender, PlayerMapping player) {
		super(nation, sender);
		
		Main main = Main.getInstance();
		FileConfiguration language = main.getLanguage();
		Bukkit.getScheduler().scheduleSyncDelayedTask(main, new Runnable() {
			@Override
			public void run() {
				if(isCancelled) return;
				
				OfflinePlayer play = (OfflinePlayer) sender;
				
				String message = GeneralMethods.format(play, language.getString("Command.Nations.Player.Kick.Message"), Bukkit.getOfflinePlayer(player.getUUID()).getName());
				nation.getAllMembers().forEach(el -> {
					if(Bukkit.getPlayer(el.getUUID()) != null) Bukkit.getPlayer(el.getUUID()).sendMessage(message);
				});
				
				nation.kickPlayer(player);
			}
		}, 1L);
	}
	
}
