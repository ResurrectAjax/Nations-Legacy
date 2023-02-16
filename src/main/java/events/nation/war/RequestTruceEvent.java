package events.nation.war;

import java.util.HashMap;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import commands.war.WarCommand;
import events.nation.NationEvent;
import main.Main;
import general.GeneralMethods;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import persistency.NationMapping;
import persistency.PlayerMapping;

public class RequestTruceEvent extends NationEvent{

	private NationMapping receiverNation;
	
	public RequestTruceEvent(NationMapping nation, NationMapping enemy, WarCommand warCommand, CommandSender sender) {
		super(nation, sender);
		this.receiverNation = enemy;

		if(super.isCancelled) return;
		
		Main main = Main.getInstance();
		Player player = (Player) sender;
		
		FileConfiguration language = main.getLanguage();
		if(warCommand.getTruceRequests().containsKey(enemy.getNationID()) && warCommand.getTruceRequests().get(enemy.getNationID()).contains(nation.getNationID())) player.sendMessage(GeneralMethods.format(sender, language.getString("Command.Nations.War.Truce.Send.AlreadySent.Message"), enemy.getName()));
		else {
			warCommand.addTruceRequest(enemy.getNationID(), nation.getNationID());
			
			TextComponent accept = GeneralMethods.createHoverText("Accept", "Click to accept", "/nations war accept " + nation.getName(), ChatColor.GREEN), 
					deny = GeneralMethods.createHoverText("Deny", "Click to deny", "/nations war deny " + nation.getName(), ChatColor.RED), 
					cancel = GeneralMethods.createHoverText("Cancel", "Click to cancel", "/nations war cancelrequest " + enemy.getName(), ChatColor.RED);
			
			TextComponent text = new TextComponent(GeneralMethods.format((OfflinePlayer)player, language.getString("Command.Nations.War.Truce.Receive.RequestReceived.Message"), nation.getName()));
			text.addExtra(accept);
			text.addExtra(" | ");
			text.addExtra(deny);
			
			TextComponent senderText = new TextComponent(GeneralMethods.format(sender, language.getString("Command.Nations.War.Truce.Send.RequestSent.Message"), enemy.getName()));
			senderText.addExtra(cancel);
			
			for(PlayerMapping playerMap : nation.getLeaders()) {
				Player senderPlay = Bukkit.getPlayer(playerMap.getUUID());
				senderPlay.spigot().sendMessage(senderText);
			}
			
			for(PlayerMapping playerMap : enemy.getLeaders()) {
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
			        for(PlayerMapping players : nation.getLeaders()) {
			        	Player play = Bukkit.getPlayer(players.getUUID());
			        	play.sendMessage(GeneralMethods.format(sender, language.getString("Command.Nations.War.Truce.Send.Expired.Message"), play.getName()));	
			        }
			        warCommand.removeTruceRequest(enemy.getNationID(), nation.getNationID());
			    }
			}.runTaskLater(main, 20*60);	
		}
	}
	
	public NationMapping getReceiverNation() {
		return receiverNation;
	}
	

}
