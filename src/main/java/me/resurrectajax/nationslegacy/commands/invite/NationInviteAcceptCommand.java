package me.resurrectajax.nationslegacy.commands.invite;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import me.resurrectajax.ajaxplugin.interfaces.ChildCommand;
import me.resurrectajax.ajaxplugin.interfaces.ParentCommand;
import me.resurrectajax.ajaxplugin.plugin.AjaxPlugin;
import me.resurrectajax.nationslegacy.ranking.Rank;
import me.resurrectajax.nationslegacy.events.nation.join.JoinNationEvent;
import me.resurrectajax.nationslegacy.general.GeneralMethods;
import me.resurrectajax.nationslegacy.main.Nations;
import me.resurrectajax.nationslegacy.persistency.MappingRepository;
import me.resurrectajax.nationslegacy.persistency.NationMapping;
import me.resurrectajax.nationslegacy.persistency.PlayerMapping;

public class NationInviteAcceptCommand extends ChildCommand{
	private Nations main;
	private ParentCommand parent;
	public NationInviteAcceptCommand(ParentCommand parent) {
		this.main = (Nations) parent.getMain();
		this.parent = parent;
	}
	
	@Override
	public void perform(CommandSender sender, String[] args) {
		FileConfiguration language = main.getLanguage();
		Player player = (Player) sender;
		
		MappingRepository mappingRepo = main.getMappingRepo();
		NationMapping nation = mappingRepo.getNationByName(args.length < 2 ? "" : args[1]);
		
		super.setLastArg(main, sender, args.length < 2 ? "" : args[1]);
		if(nation != null) {
			PlayerMapping pl = nation.getPlayers().stream().findFirst().orElse(null);
			super.setLastMentioned(main, sender, Bukkit.getOfflinePlayer(pl.getUUID()));
		}
		
		if(args.length < 2) sender.sendMessage(GeneralMethods.getBadSyntaxMessage(main, getSyntax()));
		else if(!mappingRepo.getPlayerInvites().containsKey(player.getUniqueId()) || 
				nation == null || 
				!mappingRepo.getPlayerInvites().get(player.getUniqueId()).contains(nation.getNationID())) sender.sendMessage(GeneralMethods.format((OfflinePlayer)sender, language.getString("Command.Player.Invite.Receive.NoInvite.Message"), args[1]));
		else Bukkit.getPluginManager().callEvent(new JoinNationEvent(nation, sender, Rank.getLowest()));
	}

	@Override
	public String[] getArguments(UUID uuid) {
		MappingRepository mappingRepo = main.getMappingRepo();
		Set<String> invites = !mappingRepo
				.getPlayerInvites()
				.containsKey(uuid) ? new HashSet<String>() : mappingRepo.getPlayerInvites().get(uuid).stream()
						.map(el -> mappingRepo.getNationByID(el).getName())
						.collect(Collectors.toSet());
		return invites.toArray(new String[invites.size()]);
	}

	@Override
	public String getPermissionNode() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean hasTabCompletion() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "accept";
	}

	@Override
	public String getSyntax() {
		// TODO Auto-generated method stub
		return "/nations accept <nation>";
	}

	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		return "Accept a nation invite";
	}

	@Override
	public List<ParentCommand> getSubCommands() {
		// TODO Auto-generated method stub
		return null;
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
	public String[] getSubArguments(String[] args) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AjaxPlugin getMain() {
		// TODO Auto-generated method stub
		return main;
	}

}
