package me.resurrectajax.nationslegacy.commands.alliance.add;

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
import me.resurrectajax.nationslegacy.commands.alliance.AllyCommand;
import me.resurrectajax.nationslegacy.commands.alliance.add.validators.AllyDenyValidator;
import me.resurrectajax.nationslegacy.general.GeneralMethods;
import me.resurrectajax.nationslegacy.main.Nations;
import me.resurrectajax.nationslegacy.persistency.MappingRepository;
import me.resurrectajax.nationslegacy.persistency.NationMapping;
import me.resurrectajax.nationslegacy.persistency.PlayerMapping;

public class AllyDeny extends ChildCommand{
	private Nations main;
	private AllyCommand allyCommand;
	
	public AllyDeny(AllyCommand allyCommand) {
		this.main = (Nations) allyCommand.getMain();
		this.allyCommand = allyCommand;
	}
	
	@Override
	public void perform(CommandSender sender, String[] args) {
		FileConfiguration language = main.getLanguage();
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
		
		AllyDenyValidator validator = new AllyDenyValidator(main, sender, args, this);
		if(validator.validate()) {
			allyCommand.removeAllianceRequest(nation.getNationID(), senderNation.getNationID());
			
			Set<PlayerMapping> players = new HashSet<PlayerMapping>();
			players.addAll(nation.getPlayers());
			players.addAll(senderNation.getPlayers());
			
			Player allyPlayer = Bukkit.getOnlinePlayers().stream().filter(el -> senderNation.getPlayers().contains(mappingRepo.getPlayerByUUID(el.getUniqueId()))).findFirst().orElse(null);
			Bukkit.getOnlinePlayers().stream()
				.filter(el -> players.contains(mappingRepo.getPlayerByUUID(el.getUniqueId())))
				.forEach(el -> {
					if(allyPlayer == null) el.sendMessage(GeneralMethods.format((OfflinePlayer)el, language.getString("Command.Nations.Alliance.Add.Receive.Denied.Message"), nation.getName()));
					el.sendMessage(GeneralMethods.relFormat(player, allyPlayer, language.getString("Command.Nations.Alliance.Add.Receive.Denied.Message"), nation.getName()));
				});
		}
	}

	@Override
	public String[] getArguments(UUID uuid) {
		MappingRepository mappingRepo = main.getMappingRepo();
		NationMapping nation = mappingRepo.getNationByPlayer(mappingRepo.getPlayerByUUID(uuid));
		if(nation == null) return null;
		Set<String> invites = !allyCommand.getAllianceRequests().containsKey(nation.getNationID()) ? new HashSet<String>() : allyCommand.getAllianceRequests()
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
		return "deny";
	}

	@Override
	public String getSyntax() {
		// TODO Auto-generated method stub
		return "/nations ally deny <nation>";
	}

	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		return "Deny an alliance request";
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
		return allyCommand;
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
