package me.resurrectajax.nationslegacy.commands.alliance.add;

import java.util.List;
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
import me.resurrectajax.nationslegacy.ranking.Rank;
import me.resurrectajax.nationslegacy.events.nation.alliance.RequestAllianceEvent;
import me.resurrectajax.nationslegacy.general.GeneralMethods;
import me.resurrectajax.nationslegacy.main.Nations;
import me.resurrectajax.nationslegacy.persistency.MappingRepository;
import me.resurrectajax.nationslegacy.persistency.NationMapping;
import me.resurrectajax.nationslegacy.persistency.PlayerMapping;

public class AllyAdd extends ChildCommand{
	private Nations main;
	private AllyCommand allyCommand;
	
	public AllyAdd(AllyCommand allyCommand) {
		this.allyCommand = allyCommand;
		this.main = (Nations) allyCommand.getMain();
	}
	
	@Override
	public void perform(CommandSender sender, String[] args) {
		FileConfiguration language = main.getLanguage();
		Player player = (Player) sender;
		
		MappingRepository mappingRepo = main.getMappingRepo();
		PlayerMapping playerMap = mappingRepo.getPlayerByUUID(player.getUniqueId());
		NationMapping receivingNation = mappingRepo.getNationByName(args.length > 2 ? args[2] : "");
		NationMapping nation = mappingRepo.getNationByPlayer(playerMap);
		
		super.setLastArg(main, sender, args.length > 2 ? args[2] : "");
		if(receivingNation != null) {
			PlayerMapping pl = receivingNation.getPlayers().stream().findFirst().orElse(null);
			super.setLastMentioned(main, sender, Bukkit.getOfflinePlayer(pl.getUUID()));
		}
		
		if(args.length != 3) player.sendMessage(GeneralMethods.getBadSyntaxMessage(main, getSyntax()));
		else if(nation == null) player.sendMessage(GeneralMethods.format((OfflinePlayer)sender, language.getString("Command.Player.NotInNation.Message"), args[2]));
		else if(!playerMap.getRank().equals(Rank.getHighest())) sender.sendMessage(GeneralMethods.format((OfflinePlayer)sender, language.getString("Command.Player.NotALeader.Message"), nation.getName()));
		else if(receivingNation == null) player.sendMessage(GeneralMethods.format((OfflinePlayer)sender, language.getString("Command.Nations.NotExist.Message"), args[2]));
		else if(nation == receivingNation) player.sendMessage(GeneralMethods.format((OfflinePlayer)sender, language.getString("Command.Nations.Alliance.Add.Send.Self.Message"), args[2]));
		else if(mappingRepo.getWarNationsByNationID(nation.getNationID()).contains(receivingNation)) player.sendMessage(GeneralMethods.format((OfflinePlayer)sender, language.getString("Command.Nations.Alliance.Add.Send.NationAtWar.Message"), args[2]));
		else if(mappingRepo.getAllianceNationsByNationID(nation.getNationID()).contains(receivingNation)) player.sendMessage(GeneralMethods.format((OfflinePlayer)sender, language.getString("Command.Nations.Alliance.Add.Send.AlreadyAlly.Message"), args[2]));
		else Bukkit.getPluginManager().callEvent(new RequestAllianceEvent(nation, receivingNation, allyCommand, sender));
	}

	@Override
	public String[] getArguments(UUID uuid) {
		MappingRepository mappingRepo = main.getMappingRepo();
		PlayerMapping player = mappingRepo.getPlayerByUUID(uuid);
		
		List<String> nations = mappingRepo.getNations().stream().filter(el -> el != mappingRepo.getNationByPlayer(player)).map(el -> el.getName()).collect(Collectors.toList());
		return nations.toArray(new String[nations.size()]);
	}

	@Override
	public String getPermissionNode() {
		// TODO Auto-generated method stub
		return "nations.player.ally.add";
	}

	@Override
	public boolean hasTabCompletion() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "add";
	}

	@Override
	public String getSyntax() {
		// TODO Auto-generated method stub
		return "/nations ally add <nation>";
	}

	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		return main.getLanguage().getString("HelpList.Ally.Add.Description");
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
