package me.resurrectajax.nationslegacy.events.nation.war;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import me.resurrectajax.nationslegacy.general.GeneralMethods;
import me.resurrectajax.nationslegacy.main.Nations;
import me.resurrectajax.nationslegacy.persistency.NationMapping;
import me.resurrectajax.nationslegacy.persistency.PlayerMapping;

public class DeclareWarEvent extends WarEvent{

	public DeclareWarEvent(NationMapping nation, NationMapping enemy, CommandSender sender) {
		super(nation, enemy, sender);
		
		Nations main = Nations.getInstance();
		Bukkit.getScheduler().scheduleSyncDelayedTask(main, new Runnable() {
			
			@Override
			public void run() {
				if(isCancelled) return;
				
				FileConfiguration language = main.getLanguage();
				
				Player enemyPlayer = enemy.getAllMembers().stream().map(el -> Bukkit.getPlayer(el.getUUID())).filter(el -> el != null).findFirst().orElse(null);
				
				Set<PlayerMapping> players = new HashSet<PlayerMapping>();
				players.addAll(nation.getAllMembers());
				players.addAll(enemy.getAllMembers());
				for(PlayerMapping player : players) {
					if(Bukkit.getPlayer(player.getUUID()) == null) continue;
					Player playerA = Bukkit.getPlayer(player.getUUID());
					
					if(enemyPlayer == null) playerA.sendMessage(GeneralMethods.format((OfflinePlayer)sender, language.getString("Command.Nations.War.Add.DeclarationSent.Message"), nation.getName(), enemy.getName()));
					else playerA.sendMessage(GeneralMethods.relFormat(sender, enemyPlayer, language.getString("Command.Nations.War.Add.DeclarationSent.Message"), nation.getName(), enemy.getName()));
				}
				main.getMappingRepo().startWar(nation.getNationID(), enemy.getNationID());
			}
		}, 1L);
	}

}
