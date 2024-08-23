package me.resurrectajax.nationslegacy.commands.war.truce;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.resurrectajax.ajaxplugin.interfaces.ChildCommand;
import me.resurrectajax.ajaxplugin.interfaces.ParentCommand;
import me.resurrectajax.ajaxplugin.plugin.AjaxPlugin;
import me.resurrectajax.nationslegacy.commands.war.WarCommand;
import me.resurrectajax.nationslegacy.commands.war.truce.validators.TruceAcceptValidator;
import me.resurrectajax.nationslegacy.events.nation.war.AcceptTruceEvent;
import me.resurrectajax.nationslegacy.main.Nations;
import me.resurrectajax.nationslegacy.persistency.MappingRepository;
import me.resurrectajax.nationslegacy.persistency.NationMapping;
import me.resurrectajax.nationslegacy.persistency.PlayerMapping;

public class TruceAcceptCommand extends ChildCommand{
	private Nations main;
	private WarCommand warCommand;
	
	public TruceAcceptCommand(WarCommand warCommand) {
		this.main = (Nations) warCommand.getMain();
		this.warCommand = warCommand;
	}
	
	@Override
	public void perform(CommandSender sender, String[] args) {
		Player player = (Player) sender;
		
		MappingRepository mappingRepo = main.getMappingRepo();
		PlayerMapping playerMap = mappingRepo.getPlayerByUUID(player.getUniqueId());
		NationMapping nation = mappingRepo.getNationByPlayer(playerMap);
		NationMapping senderNation = mappingRepo.getNationByName(args.length < 3 ? "" : args[2]);
		
		super.setLastArg(main, sender, args.length < 3 ? "" : args[2]);
		if(senderNation != null) {
			PlayerMapping pl = senderNation.getPlayers().stream().findFirst().orElse(null);
			super.setLastMentioned(main, sender, Bukkit.getOfflinePlayer(pl.getUUID()));
		}
		
		TruceAcceptValidator validator = new TruceAcceptValidator(sender, args, this);
		if(validator.validate()) Bukkit.getPluginManager().callEvent(new AcceptTruceEvent(nation, senderNation, warCommand, sender));
	}

	@Override
	public String[] getArguments(UUID uuid) {
		MappingRepository mappingRepo = main.getMappingRepo();
		NationMapping nation = mappingRepo.getNationByPlayer(mappingRepo.getPlayerByUUID(uuid));
		if(nation == null) return null;
		Set<String> invites = !warCommand.getTruceRequests().containsKey(nation.getNationID()) ? new HashSet<String>() : warCommand.getTruceRequests()
				.get(nation.getNationID())
				.stream()
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
		return "/nations war accept <nation>";
	}

	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		return "Accept a truce request";
	}

	@Override
	public boolean isConsole() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public ParentCommand getParentCommand() {
		// TODO Auto-generated method stub
		return warCommand;
	}

	@Override
	public AjaxPlugin getMain() {
		// TODO Auto-generated method stub
		return main;
	}

}
