package me.resurrectajax.nationslegacy.commands.invite;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.resurrectajax.ajaxplugin.interfaces.ChildCommand;
import me.resurrectajax.ajaxplugin.interfaces.ParentCommand;
import me.resurrectajax.ajaxplugin.plugin.AjaxPlugin;
import me.resurrectajax.nationslegacy.commands.invite.validators.NationInviteValidator;
import me.resurrectajax.nationslegacy.events.nation.invitePlayer.InviteToNationEvent;
import me.resurrectajax.nationslegacy.main.Nations;
import me.resurrectajax.nationslegacy.persistency.MappingRepository;
import me.resurrectajax.nationslegacy.persistency.NationMapping;
import me.resurrectajax.nationslegacy.persistency.PlayerMapping;

public class NationInviteCommand extends ChildCommand{
	private Nations main;
	private ParentCommand parent;
	public NationInviteCommand(ParentCommand parent) {
		this.main = (Nations) parent.getMain();
		this.parent = parent;
	}
	
	@Override
	public void perform(CommandSender sender, String[] args) {
		Player player = (Player) sender;
		
		MappingRepository mappingRepo = main.getMappingRepo();
		PlayerMapping playerMap = mappingRepo.getPlayerByUUID(player.getUniqueId());
		PlayerMapping receiver = mappingRepo.getPlayerByName(args.length > 1 ? args[1] : "");
		
		super.setLastArg(main, sender, args.length > 1 ? args[1] : "");
		if(receiver != null) super.setLastMentioned(main, sender, Bukkit.getOfflinePlayer(receiver.getUUID()));
		NationMapping nation = mappingRepo.getNationByPlayer(playerMap);
		
		NationInviteValidator validator = new NationInviteValidator(sender, args, this);
		if(validator.validate()) Bukkit.getPluginManager().callEvent(new InviteToNationEvent(nation, sender, receiver));
	}

	@Override
	public String[] getArguments(UUID uuid) {
		Set<String> players = Bukkit.getOnlinePlayers().stream()
						.filter(el -> main
								.getMappingRepo()
								.getPlayerByUUID(el.getUniqueId()).getNationID() == null)
						.map(el -> el.getName())
						.collect(Collectors.toSet());
		
		return players.toArray(new String[players.size()]);
	}

	@Override
	public String getPermissionNode() {
		// TODO Auto-generated method stub
		return "nations.player.invite";
	}

	@Override
	public boolean hasTabCompletion() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "invite";
	}

	@Override
	public String getSyntax() {
		// TODO Auto-generated method stub
		return "/nations invite <player>";
	}

	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		return main.getLanguage().getString("HelpList.Invite.Description");
	}

	@Override
	public boolean isConsole() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public ParentCommand getParentCommand() {
		// TODO Auto-generated method stub
		return parent;
	}

	@Override
	public AjaxPlugin getMain() {
		// TODO Auto-generated method stub
		return main;
	}

}
