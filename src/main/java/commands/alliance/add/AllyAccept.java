package commands.alliance.add;

import java.util.HashSet;
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
import events.nation.alliance.CreateAllianceEvent;
import general.GeneralMethods;
import interfaces.ChildCommand;
import interfaces.ParentCommand;
import main.Main;
import persistency.MappingRepository;
import persistency.NationMapping;
import persistency.PlayerMapping;

public class AllyAccept extends ChildCommand{
	private Main main;
	private AllyCommand allyCommand;
	
	public AllyAccept(AllyCommand allyCommand) {
		this.main = allyCommand.getMain();
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
		
		super.beforePerform(sender, args.length < 3 ? "" : args[2]);
		
		if(args.length < 3) sender.sendMessage(GeneralMethods.getBadSyntaxMessage(getSyntax()));
		else if(nation == null) player.sendMessage(GeneralMethods.format(sender, language.getString("Command.Player.NotInNation.Message"), args[2]));
		else if(!playerMap.getRank().equals(Rank.Leader)) sender.sendMessage(GeneralMethods.format(sender, language.getString("Command.Player.NotALeader.Message"), nation.getName()));
		else if(senderNation == null) player.sendMessage(GeneralMethods.format(sender, language.getString("Command.Nations.NotExist.Message"), args[2]));
		else if(nation == senderNation) player.sendMessage(GeneralMethods.format(sender, language.getString("Command.Nations.Alliance.Add.Send.Self.Message"), args[2]));
		else if(mappingRepo.getWarNationsByNationID(nation.getNationID()).contains(senderNation)) player.sendMessage(GeneralMethods.format(sender, language.getString("Command.Nations.Alliance.Add.Send.NationAtWar.Message"), args[2]));
		else if(mappingRepo.getAllianceNationsByNationID(nation.getNationID()).contains(senderNation)) player.sendMessage(GeneralMethods.format(sender, language.getString("Command.Nations.Alliance.Add.Send.AlreadyAlly.Message"), args[2]));
		else if(!allyCommand.getAllianceRequests().containsKey(nation.getNationID()) || 
				!allyCommand.getAllianceRequests().get(nation.getNationID()).contains(senderNation.getNationID())) sender.sendMessage(GeneralMethods.format(sender, language.getString("Command.Nations.Alliance.Add.Receive.NoRequest.Message"), args[1]));
		else Bukkit.getPluginManager().callEvent(new CreateAllianceEvent(nation, senderNation, allyCommand));
	}

	@Override
	public String[] getArguments(UUID uuid) {
		MappingRepository mappingRepo = main.getMappingRepo();
		NationMapping nation = mappingRepo.getNationByPlayer(mappingRepo.getPlayerByUUID(uuid));
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
		return "accept";
	}

	@Override
	public String getSyntax() {
		// TODO Auto-generated method stub
		return "/nations ally accept <nation>";
	}

	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		return "Accept an alliance request";
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

}
