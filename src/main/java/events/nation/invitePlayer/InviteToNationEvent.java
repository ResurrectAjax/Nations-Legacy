package events.nation.invitePlayer;

import java.util.HashMap;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import events.nation.NationEvent;
import general.GeneralMethods;
import main.Main;
import managers.CommandManager;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import persistency.NationMapping;
import persistency.PlayerMapping;

public class InviteToNationEvent extends NationEvent{

	public InviteToNationEvent(NationMapping nation, CommandSender sender, PlayerMapping receiver) {
		super(nation);

		if(super.isCancelled) return;
		
		Main main = Main.getInstance();
		Player player = (Player) sender;
		
		FileConfiguration language = main.getLanguage();
		CommandManager manager = main.getCommandManager();
		HashMap<UUID, Set<Integer>> playerInvites = manager.getPlayerInvites();
		if(playerInvites.containsKey(receiver.getUUID()) && playerInvites.get(receiver.getUUID()).contains(nation.getNationID())) player.sendMessage(GeneralMethods.format(sender, language.getString("Command.Player.Invite.Send.AlreadySent.Message"), Bukkit.getPlayer(receiver.getUUID()).getName()));
		else {
			Player receiverPlay = Bukkit.getPlayer(receiver.getUUID());
			manager.addPlayerInvite(nation.getNationID(), receiver.getUUID());
			
			//send invite message with hovertext
			TextComponent accept = GeneralMethods.createHoverText("Accept", "Click to accept", "/nations accept " + nation.getName(), ChatColor.GREEN), 
					deny = GeneralMethods.createHoverText("Deny", "Click to deny", "/nations deny " + nation.getName(), ChatColor.RED), 
					cancel = GeneralMethods.createHoverText("Cancel", "Click to cancel", "/nations cancelinvite " + receiverPlay.getName(), ChatColor.RED);
			
			manager.setLastArg(sender.getName(), nation.getName().toLowerCase());
			TextComponent text = new TextComponent(GeneralMethods.format((OfflinePlayer) receiverPlay, language.getString("Command.Player.Invite.Receive.InviteReceived.Message"), player.getName()));
			text.addExtra(accept);
			text.addExtra(" | ");
			text.addExtra(deny);
			
			receiverPlay.spigot().sendMessage(text);
			
			text = new TextComponent(GeneralMethods.format(sender, language.getString("Command.Player.Invite.Send.InviteSent.Message"), receiverPlay.getName()));
			text.addExtra(cancel);
			
			player.spigot().sendMessage(text);
			
			//create runnable that runs code after 5min
			new BukkitRunnable() {
			    public void run() {
			    	//if player hasn't accepted, expire the invite
			    	HashMap<UUID, Set<Integer>> partyInvites = manager.getPlayerInvites();
			        if(!partyInvites.containsKey(receiver.getUUID()) || !partyInvites.get(receiver.getUUID()).contains(nation.getNationID())) return;
			        player.sendMessage(GeneralMethods.format(sender, language.getString("Command.Player.Invite.Send.Expired.Message"), player.getName()));
			        manager.removePlayerInvite(nation.getNationID(), receiver.getUUID());
			    }
			}.runTaskTimer(main, 20*300, 20*300);
		}
	}

}
