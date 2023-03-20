package commands.kick;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import enumeration.Rank;
import events.nation.kick.KickFromNationEvent;
import general.GeneralMethods;
import main.Main;
import me.resurrectajax.ajaxplugin.interfaces.ChildCommand;
import me.resurrectajax.ajaxplugin.interfaces.ParentCommand;
import persistency.MappingRepository;
import persistency.NationMapping;
import persistency.PlayerMapping;

public class KickCommand extends ChildCommand{

	private ParentCommand parent;
	private Main main;
	public KickCommand(ParentCommand parent) {
		this.parent = parent;
		this.main = (Main) parent.getMain();
	}
	
	@Override
	public void perform(CommandSender sender, String[] args) {
		MappingRepository mappingRepo = main.getMappingRepo();
		FileConfiguration language = main.getLanguage();
		
		super.setLastArg(sender, args.length == 2 ? args[1] : "");
		PlayerMapping player = mappingRepo.getPlayerByName(args.length == 2 ? args[1] : "");
		if(args.length < 2) sender.sendMessage(GeneralMethods.getBadSyntaxMessage(getSyntax()));
		else if(player != null){
			PlayerMapping senderMap = mappingRepo.getPlayerByUUID(((Player)sender).getUniqueId());
			OfflinePlayer offPlayer = Bukkit.getOfflinePlayer(player.getUUID());
			super.setLastMentioned(sender, offPlayer);
			
			if(senderMap.getNationID() == null) sender.sendMessage(GeneralMethods.format((OfflinePlayer) sender, language.getString("Command.Player.NotInNation.Message"), offPlayer.getName()));
			else if(!senderMap.getRank().equals(Rank.Leader)) sender.sendMessage(GeneralMethods.format((OfflinePlayer) sender, language.getString("Command.Player.NotALeader.Message"), offPlayer.getName()));
			else if(player.getRank().equals(Rank.Leader)) sender.sendMessage(GeneralMethods.format((OfflinePlayer) sender, language.getString("Command.Player.Kick.Leader.Message"), offPlayer.getName()));
			else Bukkit.getPluginManager().callEvent(new KickFromNationEvent(mappingRepo.getNationByID(senderMap.getNationID()), sender, player));
		}
		else sender.sendMessage(GeneralMethods.format((OfflinePlayer) sender, language.getString("Command.Player.NotExist.Message"), args[1]));
	}

	@Override
	public String[] getArguments(UUID uuid) {
		MappingRepository mappingRepo = main.getMappingRepo();
		PlayerMapping player = mappingRepo.getPlayerByUUID(uuid);
		if(player.getNationID() == null) return null;
		
		NationMapping nation = mappingRepo.getNationByID(player.getNationID());
		Set<String> members = nation.getAllMembers().stream()
			.filter(el -> !el.getRank().equals(Rank.Leader))
			.map(el -> Bukkit.getOfflinePlayer(el.getUUID()).getName())
			.collect(Collectors.toSet());
		
		return members.toArray(new String[members.size()]);
	}

	@Override
	public String[] getSubArguments(String[] args) {
		// TODO Auto-generated method stub
		return null;
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
		return "kick";
	}

	@Override
	public String getSyntax() {
		// TODO Auto-generated method stub
		return "/nations kick <player>";
	}

	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		return "Kick a player from your nation";
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
