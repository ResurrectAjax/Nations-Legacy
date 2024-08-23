package me.resurrectajax.nationslegacy.commands.invite;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import me.resurrectajax.ajaxplugin.interfaces.ChildCommand;
import me.resurrectajax.ajaxplugin.interfaces.ParentCommand;
import me.resurrectajax.ajaxplugin.plugin.AjaxPlugin;
import me.resurrectajax.nationslegacy.commands.invite.validators.NationInviteAcceptValidator;
import me.resurrectajax.nationslegacy.events.nation.join.JoinNationEvent;
import me.resurrectajax.nationslegacy.main.Nations;
import me.resurrectajax.nationslegacy.persistency.MappingRepository;
import me.resurrectajax.nationslegacy.persistency.NationMapping;
import me.resurrectajax.nationslegacy.persistency.PlayerMapping;
import me.resurrectajax.nationslegacy.ranking.Rank;

public class NationInviteAcceptCommand extends ChildCommand{
	private Nations main;
	private ParentCommand parent;
	public NationInviteAcceptCommand(ParentCommand parent) {
		this.main = (Nations) parent.getMain();
		this.parent = parent;
	}
	
	@Override
	public void perform(CommandSender sender, String[] args) {
		MappingRepository mappingRepo = main.getMappingRepo();
		NationMapping nation = mappingRepo.getNationByName(args.length < 2 ? "" : args[1]);
		
		super.setLastArg(main, sender, args.length < 2 ? "" : args[1]);
		if(nation != null) {
			PlayerMapping pl = nation.getPlayers().stream().findFirst().orElse(null);
			super.setLastMentioned(main, sender, Bukkit.getOfflinePlayer(pl.getUUID()));
		}
		
		NationInviteAcceptValidator validator = new NationInviteAcceptValidator(sender, args, this);
		if(validator.validate()) Bukkit.getPluginManager().callEvent(new JoinNationEvent(nation, sender, Rank.getLowest()));
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
