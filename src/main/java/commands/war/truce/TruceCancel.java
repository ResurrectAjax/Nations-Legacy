package commands.war.truce;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import commands.war.WarCommand;
import general.GeneralMethods;
import main.Main;
import me.resurrectajax.ajaxplugin.interfaces.ChildCommand;
import me.resurrectajax.ajaxplugin.interfaces.ParentCommand;
import persistency.MappingRepository;
import persistency.NationMapping;

public class TruceCancel extends ChildCommand{
	private Main main;
	private WarCommand warCommand;
	
	public TruceCancel(WarCommand warCommand) {
		this.main = (Main) warCommand.getMain();
		this.warCommand = warCommand;
	}
	
	@Override
	public void perform(CommandSender sender, String[] args) {
		FileConfiguration language = main.getLanguage();
		MappingRepository mappingRepo = main.getMappingRepo();
		
		NationMapping senderNation = mappingRepo.getNationByPlayer(mappingRepo.getPlayerByUUID(((Player) sender).getUniqueId()));
		NationMapping nation = mappingRepo.getNationByName(args.length < 3 ? "" : args[2]);
		
		super.beforePerform(sender, args.length < 3 ? "" : args[2]);

		if(args.length < 3) sender.sendMessage(GeneralMethods.getBadSyntaxMessage(getSyntax()));
		else if(senderNation == null) sender.sendMessage(GeneralMethods.format((OfflinePlayer)sender, language.getString("Command.Player.NotInNation.Message"), ""));
		else if(nation == null ||
				!warCommand.getTruceRequests().containsKey(nation.getNationID()) ||  
				!warCommand.getTruceRequests().get(nation.getNationID()).contains(senderNation.getNationID())) sender.sendMessage(GeneralMethods.format((OfflinePlayer)sender, language.getString("Command.Nations.War.Truce.Receive.NoRequest.Message"), args[2]));
		else {
			warCommand.removeTruceRequest(nation.getNationID(), senderNation.getNationID());
			sender.sendMessage(GeneralMethods.format((OfflinePlayer)sender, language.getString("Command.Nations.War.Truce.Send.CancelRequest.Message"), nation.getName()));
		}
	}

	@Override
	public String[] getArguments(UUID uuid) {
		MappingRepository mappingRepo = main.getMappingRepo();
		NationMapping nation = mappingRepo.getNationByPlayer(mappingRepo.getPlayerByUUID(uuid));
		if(nation == null) return null;
		Set<String> args = mappingRepo.getPlayerInvites().keySet().stream()
				.filter(el -> mappingRepo.getPlayerInvites().get(el).contains(nation.getNationID()))
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
		return warCommand;
	}

	@Override
	public String[] getSubArguments(String[] args) {
		// TODO Auto-generated method stub
		return null;
	}

}
