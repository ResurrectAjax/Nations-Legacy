package commands.alliance.add;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import commands.alliance.AllyCommand;
import general.GeneralMethods;
import interfaces.ChildCommand;
import interfaces.ParentCommand;
import main.Main;
import managers.CommandManager;
import persistency.MappingRepository;
import persistency.NationMapping;

public class AllyCancel extends ChildCommand{
	private Main main;
	private AllyCommand allyCommand;
	
	public AllyCancel(AllyCommand allyCommand) {
		this.main = allyCommand.getMain();
		this.allyCommand = allyCommand;
	}
	
	@Override
	public void perform(CommandSender sender, String[] args) {
		FileConfiguration language = main.getLanguage();
		MappingRepository mappingRepo = main.getMappingRepo();
		
		NationMapping senderNation = mappingRepo.getNationByPlayer(mappingRepo.getPlayerByUUID(((Player) sender).getUniqueId()));
		NationMapping nation = mappingRepo.getNationByName(args.length < 3 ? "" : args[2]);
		
		super.beforePerform(sender, args.length < 3 ? "" : args[2]);

		if(args.length < 3) sender.sendMessage(GeneralMethods.getBadSyntaxMessage(getSyntax()));
		else if(senderNation == null) sender.sendMessage(GeneralMethods.format(sender, language.getString("Command.Player.NotInNation.Message"), ""));
		else if(nation == null ||
				!allyCommand.getAllianceRequests().containsKey(nation.getNationID()) ||  
				!allyCommand.getAllianceRequests().get(nation.getNationID()).contains(senderNation.getNationID())) sender.sendMessage(GeneralMethods.format(sender, language.getString("Command.Nations.Alliance.Add.Receive.NoRequest.Message"), args[2]));
		else {
			allyCommand.removeAllianceRequest(nation.getNationID(), senderNation.getNationID());
			sender.sendMessage(GeneralMethods.format(sender, language.getString("Command.Nations.Alliance.Add.Send.CancelRequest.Message"), nation.getName()));
		}
	}

	@Override
	public String[] getArguments(UUID uuid) {
		MappingRepository mappingRepo = main.getMappingRepo();
		CommandManager manager = main.getCommandManager();
		NationMapping nation = mappingRepo.getNationByPlayer(mappingRepo.getPlayerByUUID(uuid));
		
		Set<String> args = manager.getPlayerInvites().keySet().stream()
				.filter(el -> manager.getPlayerInvites().get(el).contains(nation.getNationID()))
				.map(el -> el.toString())
				.collect(Collectors.toSet());
		return args.toArray(new String[args.size()]);
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
		return "cancelrequest";
	}

	@Override
	public String getSyntax() {
		// TODO Auto-generated method stub
		return "/nations ally cancelrequest <nation>";
	}

	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		return "Cancel an alliance request";
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
