package me.resurrectajax.nationslegacy.events.nation.invitePlayer;

import java.util.HashMap;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import me.resurrectajax.nationslegacy.events.nation.NationEvent;
import me.resurrectajax.nationslegacy.general.GeneralMethods;
import me.resurrectajax.nationslegacy.main.Nations;
import me.resurrectajax.nationslegacy.persistency.MappingRepository;
import me.resurrectajax.nationslegacy.persistency.NationMapping;
import me.resurrectajax.nationslegacy.persistency.PlayerMapping;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;

public class InviteToNationEvent extends NationEvent{

	private PlayerMapping receiver;
	
	public InviteToNationEvent(NationMapping nation, CommandSender sender, PlayerMapping receive) {
		super(nation, sender);
		
		this.receiver = receive;

		Nations main = Nations.getInstance();
		Bukkit.getScheduler().scheduleSyncDelayedTask(main, new Runnable() {
			
			@Override
			public void run() {
				if(isCancelled) return;
				
				Player player = (Player) sender;
				
				FileConfiguration language = main.getLanguage();
				MappingRepository mappingRepo = main.getMappingRepo();
				HashMap<UUID, Set<Integer>> playerInvites = mappingRepo.getPlayerInvites();
				
				main.getCommandManager().setLastArg(sender.getName(), Bukkit.getOfflinePlayer(receiver.getUUID()).getName());
				if(playerInvites.containsKey(receiver.getUUID()) && playerInvites.get(receiver.getUUID()).contains(nation.getNationID())) player.sendMessage(GeneralMethods.format((OfflinePlayer)sender, language.getString("Command.Player.Invite.Send.AlreadySent.Message"), Bukkit.getOfflinePlayer(receiver.getUUID()).getName()));
				else {
					Player receiverPlay = Bukkit.getPlayer(receiver.getUUID());
					mappingRepo.addPlayerInvite(nation.getNationID(), receiver.getUUID());
					
					//send invite message with hovertext
					TextComponent accept = GeneralMethods.createHoverText("Accept", "Click to accept", "/nations accept " + nation.getName(), ChatColor.GREEN), 
							deny = GeneralMethods.createHoverText("Deny", "Click to deny", "/nations deny " + nation.getName(), ChatColor.RED), 
							cancel = GeneralMethods.createHoverText("Cancel", "Click to cancel", "/nations cancel " + receiverPlay.getName(), ChatColor.RED);
					
					TextComponent text = new TextComponent(GeneralMethods.relFormat(sender, receiverPlay, language.getString("Command.Player.Invite.Receive.InviteReceived.Message"), player.getName()));
					text.addExtra(accept);
					text.addExtra(" | ");
					text.addExtra(deny);
					
					receiverPlay.spigot().sendMessage(text);
					
					main.getCommandManager().setLastArg(sender.getName(), receiverPlay.getName());
					text = new TextComponent(GeneralMethods.relFormat(sender, receiverPlay, language.getString("Command.Player.Invite.Send.InviteSent.Message"), receiverPlay.getName()));
					text.addExtra(cancel);
					
					player.spigot().sendMessage(text);
					
					//create runnable that runs code after 5min
					new BukkitRunnable() {
					    public void run() {
					    	//if player hasn't accepted, expire the invite
					    	HashMap<UUID, Set<Integer>> partyInvites = mappingRepo.getPlayerInvites();
					        if(!partyInvites.containsKey(receiver.getUUID()) || !partyInvites.get(receiver.getUUID()).contains(nation.getNationID())) return;
					        player.sendMessage(GeneralMethods.relFormat(sender, Bukkit.getPlayer(receiver.getUUID()), language.getString("Command.Player.Invite.Send.Expired.Message"), player.getName()));
					        mappingRepo.removePlayerInvite(nation.getNationID(), receiver.getUUID());
					    }
					}.runTaskTimer(main, 20*300, 20*300);
				}
			}
		}, 1L);
	}
	
	public PlayerMapping getReceiver() {
		return this.receiver;
	}

}
