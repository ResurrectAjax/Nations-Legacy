package events.nation.ranks;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import events.nation.NationEvent;
import general.GeneralMethods;
import main.Main;
import persistency.NationMapping;
import persistency.PlayerMapping;

public class DemoteEvent extends NationEvent{
	private PlayerMapping demotedPlayer;
	
	public DemoteEvent(NationMapping nation, CommandSender sender, PlayerMapping player) {
		super(nation, sender);
		Main main = Main.getInstance();
		FileConfiguration language = main.getLanguage();
		
		setDemotedPlayer(player);
		Bukkit.getScheduler().scheduleSyncDelayedTask(main, new Runnable() {
			
			@Override
			public void run() {
				if(isCancelled) return;
				
				nation.demotePlayer(player);
				
				OfflinePlayer playerO = Bukkit.getOfflinePlayer(player.getUUID());
				for(PlayerMapping playerA : nation.getAllMembers()) {
					if(Bukkit.getPlayer(playerA.getUUID()) == null) continue;
					Player playerAB = Bukkit.getPlayer(playerA.getUUID());
					playerAB.sendMessage(GeneralMethods.relFormat(sender, (CommandSender)playerO, language.getString("Command.Player.Demote.Demoted.Message"), playerO.getName()));
				}
			}
		}, 1L);
	}

	public PlayerMapping getDemotedPlayer() {
		return demotedPlayer;
	}

	private void setDemotedPlayer(PlayerMapping promotedPlayer) {
		this.demotedPlayer = promotedPlayer;
	}

}
