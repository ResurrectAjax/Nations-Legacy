package me.resurrectajax.nationslegacy.commands.kick;

import java.util.List;
import java.util.Set;
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
import me.resurrectajax.nationslegacy.enumeration.Rank;
import me.resurrectajax.nationslegacy.events.nation.kick.KickFromNationEvent;
import me.resurrectajax.nationslegacy.general.GeneralMethods;
import me.resurrectajax.nationslegacy.main.Nations;
import me.resurrectajax.nationslegacy.persistency.MappingRepository;
import me.resurrectajax.nationslegacy.persistency.NationMapping;
import me.resurrectajax.nationslegacy.persistency.PlayerMapping;

public class KickCommand extends ChildCommand{

	private ParentCommand parent;
	private Nations main;
	public KickCommand(ParentCommand parent) {
		this.parent = parent;
		this.main = (Nations) parent.getMain();
	}
	
	@Override
	public void perform(CommandSender sender, String[] args) {
		MappingRepository mappingRepo = main.getMappingRepo();
		FileConfiguration language = main.getLanguage();
		
		super.setLastArg(main, sender, args.length == 2 ? args[1] : "");
		PlayerMapping player = mappingRepo.getPlayerByName(args.length == 2 ? args[1] : "");
		if(args.length < 2) sender.sendMessage(GeneralMethods.getBadSyntaxMessage(main, getSyntax()));
		else if(player != null){
			PlayerMapping senderMap = mappingRepo.getPlayerByUUID(((Player)sender).getUniqueId());
			OfflinePlayer offPlayer = Bukkit.getOfflinePlayer(player.getUUID());
			super.setLastMentioned(main, sender, offPlayer);
			
			if(senderMap.getNationID() == null) sender.sendMessage(GeneralMethods.format((OfflinePlayer) sender, language.getString("Command.Player.NotInNation.Message"), offPlayer.getName()));
			else if(player.getNationID() != senderMap.getNationID()) sender.sendMessage(GeneralMethods.format((OfflinePlayer) sender, language.getString("Command.Player.NotInSameNation.Message"), offPlayer.getName()));
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
		return "nations.player.kick";
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
		return main.getLanguage().getString("HelpList.Kick.Description");
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
	public AjaxPlugin getMain() {
		// TODO Auto-generated method stub
		return main;
	}

}
