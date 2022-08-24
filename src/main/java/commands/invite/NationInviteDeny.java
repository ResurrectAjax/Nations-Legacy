package commands.invite;

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

import general.GeneralMethods;
import interfaces.ChildCommand;
import interfaces.ParentCommand;
import main.Main;
import managers.CommandManager;
import persistency.MappingRepository;
import persistency.NationMapping;

public class NationInviteDeny extends ChildCommand{
	private Main main;
	private ParentCommand parent;
	public NationInviteDeny(ParentCommand parent) {
		this.main = parent.getMain();
		this.parent = parent;
	}
	
	@Override
	public void perform(CommandSender sender, String[] args) {
		CommandManager manager = main.getCommandManager();
		FileConfiguration language = main.getLanguage();
		Player player = (Player) sender;
		
		MappingRepository mappingRepo = main.getMappingRepo();
		NationMapping nation = mappingRepo.getNationByName(args.length < 2 ? "" : args[1]);
		
		super.beforePerform(sender, args.length < 2 ? "" : args[1]);
		
		if(args.length < 2) sender.sendMessage(GeneralMethods.getBadSyntaxMessage(getSyntax()));
		else if(!manager.getPlayerInvites().containsKey(player.getUniqueId()) || 
				nation == null || 
				!manager.getPlayerInvites().get(player.getUniqueId()).contains(nation.getNationID())) sender.sendMessage(GeneralMethods.format(sender, language.getString("Command.Player.Invite.Receive.NoInvite.Message"), args[1]));
		else {
			main.getCommandManager().removePlayerInvite(nation.getNationID(), player.getUniqueId());
			Bukkit.getOnlinePlayers().stream()
				.filter(el -> (nation.getLeaders().contains(mappingRepo.getPlayerByUUID(el.getUniqueId())) || nation.getOfficers().contains(mappingRepo.getPlayerByUUID(el.getUniqueId())) || nation.getMembers().contains(mappingRepo.getPlayerByUUID(el.getUniqueId()))))
				.forEach(el -> el.sendMessage(GeneralMethods.format((OfflinePlayer)el, language.getString("Command.Player.Invite.Received.Denied.Message"), nation.getName())));
			sender.sendMessage(GeneralMethods.format(sender, language.getString("Command.Player.DeniedNation.Message"), nation.getName()));
		}
	}

	@Override
	public String[] getArguments(UUID uuid) {
		MappingRepository mappingRepo = main.getMappingRepo();
		Set<String> invites = !main.getCommandManager().getPlayerInvites()
				.containsKey(uuid) ? new HashSet<String>() : main.getCommandManager().getPlayerInvites().get(uuid).stream()
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
		return "/nations deny <nation>";
	}

	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		return "Deny a nation invite";
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

}
