package commands.invite;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import enumeration.Rank;
import events.nation.invitePlayer.InviteToNationEvent;
import main.Main;
import general.GeneralMethods;
import me.resurrectajax.ajaxplugin.interfaces.ChildCommand;
import me.resurrectajax.ajaxplugin.interfaces.ParentCommand;
import persistency.MappingRepository;
import persistency.NationMapping;
import persistency.PlayerMapping;

//CHANGE THE MESSAGES, DO SHIT
public class NationInvite extends ChildCommand{
	private Main main;
	private ParentCommand parent;
	public NationInvite(ParentCommand parent) {
		this.main = (Main) parent.getMain();
		this.parent = parent;
	}
	
	@Override
	public void perform(CommandSender sender, String[] args) {
		FileConfiguration language = main.getLanguage();
		Player player = (Player) sender;
		
		MappingRepository mappingRepo = main.getMappingRepo();
		PlayerMapping playerMap = mappingRepo.getPlayerByUUID(player.getUniqueId());
		PlayerMapping receiver = mappingRepo.getPlayerByUUID(Bukkit.getPlayer(args.length > 1 ? args[1] : "").getUniqueId());
		NationMapping nation = mappingRepo.getNationByPlayer(playerMap);
		
		super.beforePerform(sender, args.length > 1 ? args[1] : "");
		
		if(args.length != 2) player.sendMessage(GeneralMethods.getBadSyntaxMessage(getSyntax()));
		else if(nation == null) player.sendMessage(GeneralMethods.format(sender, language.getString("Command.Player.NotInNation.Message"), args[1]));
		else if(!playerMap.getRank().equals(Rank.Leader) && !playerMap.getRank().equals(Rank.Officer)) sender.sendMessage(GeneralMethods.format(sender, language.getString("Command.Player.NotALeaderOrOfficer.Message"), nation.getName()));
		else if(Bukkit.getPlayer(args[1]) == null) player.sendMessage(GeneralMethods.format(sender, language.getString("Command.Player.NotExist.Message"), args[1]));
		else if(player.getUniqueId().equals(Bukkit.getPlayer(args[1]).getUniqueId())) player.sendMessage(GeneralMethods.format(sender, language.getString("Command.Player.Invite.Send.SelfInvite.Message"), args[1]));
		else if(Bukkit.getPlayer(receiver.getUUID()) == null) player.sendMessage(GeneralMethods.format(sender, language.getString("Command.Player.NotExist.Message"), args[1]));
		else if(receiver.getNationID() != null) player.sendMessage(GeneralMethods.format(sender, language.getString("Command.Player.Invite.Send.AlreadyInNation.Message"), args[1]));
		else Bukkit.getPluginManager().callEvent(new InviteToNationEvent(nation, sender, receiver));
	}

	@Override
	public String[] getArguments(UUID uuid) {
		Set<String> players = Bukkit.getOnlinePlayers().stream()
						.filter(el -> main
								.getMappingRepo()
								.getPlayerByUUID(el.getUniqueId()).getNationID() == null)
						.map(el -> el.getName())
						.collect(Collectors.toSet());
		
		return players.toArray(new String[players.size()]);
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
		return "invite";
	}

	@Override
	public String getSyntax() {
		// TODO Auto-generated method stub
		return "/nations invite <player>";
	}

	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		return "Invite a player to your nation";
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
	public String[] getSubArguments() {
		// TODO Auto-generated method stub
		return null;
	}

}
