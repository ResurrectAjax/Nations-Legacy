package commands.invite;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import general.GeneralMethods;
import main.Main;
import me.resurrectajax.ajaxplugin.interfaces.ChildCommand;
import me.resurrectajax.ajaxplugin.interfaces.ParentCommand;
import persistency.MappingRepository;
import persistency.NationMapping;
import persistency.PlayerMapping;

public class NationInviteCancel extends ChildCommand{

	private Main main;
	private ParentCommand parent;
	public NationInviteCancel(ParentCommand parent) {
		this.main = (Main) parent.getMain();
		this.parent = parent;
	}
	
	@Override
	public void perform(CommandSender sender, String[] args) {
		FileConfiguration language = main.getLanguage();
		MappingRepository mappingRepo = main.getMappingRepo();
		
		PlayerMapping receiver = args.length < 2 ? null : mappingRepo.getPlayerByName(args[1]);
		PlayerMapping send = mappingRepo.getPlayerByUUID(((Player) sender).getUniqueId());
		
		NationMapping nation = mappingRepo.getNationByName(args.length < 2 ? "" : args[1]);
		
		super.beforePerform(sender, args.length < 2 ? "" : args[1]);
		
		if(args.length < 2) sender.sendMessage(GeneralMethods.getBadSyntaxMessage(getSyntax()));
		else if(send.getNationID() == null) sender.sendMessage(GeneralMethods.format(sender, language.getString("Command.Player.NotInNation.Message"), ""));
		else if(!mappingRepo.getPlayerInvites().containsKey(receiver.getUUID()) || 
				nation == null || 
				!mappingRepo.getPlayerInvites().get(receiver.getUUID()).contains(nation.getNationID())) sender.sendMessage(GeneralMethods.format(sender, language.getString("Command.Player.Invite.Receive.NoInvite.Message"), args[1]));
		else {
			mappingRepo.removePlayerInvite(nation.getNationID(), receiver.getUUID());
			sender.sendMessage(GeneralMethods.format(sender, language.getString("Command.Player.CancelInvitation.Message"), nation.getName()));
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
		return "cancel";
	}

	@Override
	public String getSyntax() {
		// TODO Auto-generated method stub
		return "/nations cancel <player>";
	}

	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		return "Cancel an invitation sent to a player";
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

}
