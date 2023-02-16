package commands.war.truce;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import commands.war.WarCommand;
import enumeration.Rank;
import events.nation.war.RequestTruceEvent;
import main.Main;
import general.GeneralMethods;
import me.resurrectajax.ajaxplugin.interfaces.ChildCommand;
import me.resurrectajax.ajaxplugin.interfaces.ParentCommand;
import persistency.MappingRepository;
import persistency.NationMapping;
import persistency.PlayerMapping;

public class WarTruce extends ChildCommand{

	private WarCommand parent;
	private Main main;
	public WarTruce(WarCommand parent) {
		this.parent = parent;
		this.main = (Main) parent.getMain();
	}
	
	@Override
	public void perform(CommandSender sender, String[] args) {
		FileConfiguration language = main.getLanguage();
		Player player = (Player) sender;
		
		MappingRepository mappingRepo = main.getMappingRepo();
		PlayerMapping playerMap = mappingRepo.getPlayerByUUID(player.getUniqueId());
		NationMapping receivingNation = mappingRepo.getNationByName(args.length > 2 ? args[2] : "");
		NationMapping nation = mappingRepo.getNationByPlayer(playerMap);
		
		super.beforePerform(sender, args.length > 2 ? args[2] : "");
		
		if(args.length != 3) player.sendMessage(GeneralMethods.getBadSyntaxMessage(getSyntax()));
		else if(nation == null) player.sendMessage(GeneralMethods.format(sender, language.getString("Command.Player.NotInNation.Message"), args[2]));
		else if(!playerMap.getRank().equals(Rank.Leader)) sender.sendMessage(GeneralMethods.format(sender, language.getString("Command.Player.NotALeader.Message"), nation.getName()));
		else if(receivingNation == null) player.sendMessage(GeneralMethods.format(sender, language.getString("Command.Nations.NotExist.Message"), args[2]));
		else if(nation == receivingNation) player.sendMessage(GeneralMethods.format(sender, language.getString("Command.Nations.War.Add.Self.Message"), args[2]));
		else if(!mappingRepo.getWarNationsByNationID(nation.getNationID()).contains(receivingNation)) player.sendMessage(GeneralMethods.format(sender, language.getString("Command.Nations.War.Truce.Send.NotAtWar.Message"), args[2]));
		else Bukkit.getPluginManager().callEvent(new RequestTruceEvent(nation, receivingNation, parent, sender));
	}

	@Override
	public String[] getArguments(UUID uuid) {
		MappingRepository mappingRepo = main.getMappingRepo();
		NationMapping nation = mappingRepo.getNationByPlayer(mappingRepo.getPlayerByUUID(uuid));
		if(nation == null) return null;
		List<String> nations = mappingRepo.getWarNationsByNationID(nation.getNationID()).stream().map(el -> el.getName()).collect(Collectors.toList());
		return nations.toArray(new String[nations.size()]);
	}

	@Override
	public String getPermissionNode() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean hasTabCompletion() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "truce";
	}

	@Override
	public String getSyntax() {
		// TODO Auto-generated method stub
		return "/nations war truce <nation>";
	}

	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		return "Request a truce with an enemy nation";
	}

	@Override
	public List<ParentCommand> getSubCommands() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ParentCommand getParentCommand() {
		// TODO Auto-generated method stub
		return parent;
	}

	@Override
	public boolean isConsole() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String[] getSubArguments() {
		// TODO Auto-generated method stub
		return null;
	}

}
