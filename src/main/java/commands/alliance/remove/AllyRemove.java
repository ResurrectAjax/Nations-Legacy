package commands.alliance.remove;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import commands.alliance.AllyCommand;
import enumeration.Rank;
import events.nation.alliance.RemoveAllianceEvent;
import general.GeneralMethods;
import interfaces.ChildCommand;
import interfaces.ParentCommand;
import main.Main;
import persistency.MappingRepository;
import persistency.NationMapping;
import persistency.PlayerMapping;

public class AllyRemove extends ChildCommand{
	private Main main;
	private AllyCommand allyCommand;
	public AllyRemove(AllyCommand allyCommand) {
		this.main = allyCommand.getMain();
		this.allyCommand = allyCommand;
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
		else if(nation == receivingNation) player.sendMessage(GeneralMethods.format(sender, language.getString("Command.Nations.Alliance.Add.Send.Self.Message"), args[2]));
		else if(!mappingRepo.getAllianceNationsByNationID(nation.getNationID()).contains(receivingNation)) player.sendMessage(GeneralMethods.format(sender, language.getString("Command.Nations.Alliance.Remove.NotAnAlly.Message"), args[2]));
		else Bukkit.getPluginManager().callEvent(new RemoveAllianceEvent(nation, receivingNation, allyCommand));
	}

	@Override
	public String[] getArguments(UUID uuid) {
		MappingRepository mappingRepo = main.getMappingRepo();
		PlayerMapping player = mappingRepo.getPlayerByUUID(uuid);
		
		if(player.getNationID() == null) return null;
		
		Set<String> nations = mappingRepo.getAllianceNationsByNationID(player.getNationID()).stream().map(el -> el.getName()).collect(Collectors.toSet());
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
		return "remove";
	}

	@Override
	public String getSyntax() {
		// TODO Auto-generated method stub
		return "/nations ally remove <nation>";
	}

	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		return "Remove the alliance and become neutral";
	}

	@Override
	public List<ParentCommand> getSubCommands() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isConsole() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public ParentCommand getParentCommand() {
		// TODO Auto-generated method stub
		return allyCommand;
	}

}
