package me.resurrectajax.nationslegacy.events.nation.war;

import java.util.HashMap;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import me.resurrectajax.nationslegacy.commands.war.WarCommand;
import me.resurrectajax.nationslegacy.general.GeneralMethods;
import me.resurrectajax.nationslegacy.main.Nations;
import me.resurrectajax.nationslegacy.persistency.NationMapping;
import me.resurrectajax.nationslegacy.persistency.PlayerMapping;
import me.resurrectajax.nationslegacy.ranking.Rank;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;

public class RequestTruceEvent extends WarEvent{

	public RequestTruceEvent(NationMapping nation, NationMapping enemy, WarCommand warCommand, CommandSender sender) {
		super(nation, enemy, sender);
		
		Nations main = Nations.getInstance();
		
		Bukkit.getScheduler().scheduleSyncDelayedTask(main, new Runnable() {
			
			@Override
			public void run() {
				if(isCancelled) return;
				
				Player player = (Player) sender;
				
				FileConfiguration language = main.getLanguage();
				if(warCommand.getTruceRequests().containsKey(enemy.getNationID()) && warCommand.getTruceRequests().get(enemy.getNationID()).contains(nation.getNationID())) player.sendMessage(GeneralMethods.format((OfflinePlayer)sender, language.getString("Command.Nations.War.Truce.Send.AlreadySent.Message"), enemy.getName()));
				else {
					warCommand.addTruceRequest(enemy.getNationID(), nation.getNationID());
					
					TextComponent accept = GeneralMethods.createHoverText("Accept", "Click to accept", "/nations war accept " + nation.getName(), ChatColor.GREEN), 
							deny = GeneralMethods.createHoverText("Deny", "Click to deny", "/nations war deny " + nation.getName(), ChatColor.RED), 
							cancel = GeneralMethods.createHoverText("Cancel", "Click to cancel", "/nations war cancelrequest " + enemy.getName(), ChatColor.RED);
					
					TextComponent text = new TextComponent(GeneralMethods.format((OfflinePlayer)player, language.getString("Command.Nations.War.Truce.Receive.RequestReceived.Message"), nation.getName()));
					text.addExtra(accept);
					text.addExtra(" | ");
					text.addExtra(deny);
					
					TextComponent senderText = new TextComponent(GeneralMethods.format((OfflinePlayer)sender, language.getString("Command.Nations.War.Truce.Send.RequestSent.Message"), enemy.getName()));
					senderText.addExtra(cancel);
					
					for(PlayerMapping playerMap : nation.getPlayersByRank(Rank.getHighest())) {
						Player senderPlay = Bukkit.getPlayer(playerMap.getUUID());
						senderPlay.spigot().sendMessage(senderText);
					}
					
					for(PlayerMapping playerMap : enemy.getPlayersByRank(Rank.getHighest())) {
						Player receiverPlay = Bukkit.getPlayer(playerMap.getUUID());
						if(receiverPlay == null) continue;
						
						main.getCommandManager().setLastArg(sender.getName(), enemy.getName().toLowerCase());
						receiverPlay.spigot().sendMessage(text);
					}
					
					//create runnable that runs code after 5min
					new BukkitRunnable() {
					    public void run() {
					    	//if player hasn't accepted, expire the invite
					    	HashMap<Integer, Set<Integer>> allianceRequests = warCommand.getTruceRequests();
					        if(!allianceRequests.containsKey(enemy.getNationID()) || !allianceRequests.get(enemy.getNationID()).contains(nation.getNationID())) return;
					        for(PlayerMapping players : nation.getPlayersByRank(Rank.getHighest())) {
					        	Player play = Bukkit.getPlayer(players.getUUID());
					        	play.sendMessage(GeneralMethods.format((OfflinePlayer)sender, language.getString("Command.Nations.War.Truce.Send.Expired.Message"), play.getName()));	
					        }
					        warCommand.removeTruceRequest(enemy.getNationID(), nation.getNationID());
					    }
					}.runTaskLater(main, 20*60);	
				}
			}
		}, 1L);
	}
	

}
