package events.nation.war;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import commands.war.WarCommand;
import events.nation.NationEvent;
import main.Main;
import general.GeneralMethods;
import persistency.NationMapping;
import persistency.PlayerMapping;

public class DeclareWarEvent extends NationEvent{

	private NationMapping enemy;
	public DeclareWarEvent(NationMapping nation, NationMapping enemy, WarCommand warCommand, CommandSender sender) {
		super(nation, sender);
		this.enemy = enemy;
		
		Main main = Main.getInstance();
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
					
					playerA.sendMessage(GeneralMethods.relFormat(sender, enemyPlayer, language.getString("Command.Nations.War.Add.DeclarationSent.Message"), enemy.getName()));
				}
				main.getMappingRepo().startWar(nation.getNationID(), enemy.getNationID());
			}
		}, 1L);
	}
	
	public NationMapping getEnemy() {
		return enemy;
	}

}
