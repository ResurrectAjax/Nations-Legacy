package me.resurrectajax.nationslegacy.commands.home;

import java.util.List;
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
import me.resurrectajax.nationslegacy.events.nation.home.DeleteHomeEvent;
import me.resurrectajax.nationslegacy.general.GeneralMethods;
import me.resurrectajax.nationslegacy.main.Nations;
import me.resurrectajax.nationslegacy.persistency.MappingRepository;
import me.resurrectajax.nationslegacy.persistency.NationMapping;
import me.resurrectajax.nationslegacy.persistency.PlayerMapping;

public class DeleteHomeCommand extends ChildCommand{

	private Nations main;
	private ParentCommand parent;
	public DeleteHomeCommand(ParentCommand parent) {
		this.parent = parent;
		this.main = (Nations) parent.getMain();
	}
	
	@Override
	public void perform(CommandSender sender, String[] args) {
		
		if(!(sender instanceof Player)) return;
		Player player = (Player) sender;
		
		String home = args.length < 2 ? "home" : args[1];
		super.setLastArg(main, sender, home);
		
		MappingRepository mappingRepo = main.getMappingRepo();
		PlayerMapping playerMap = mappingRepo.getPlayerByUUID(player.getUniqueId());
		FileConfiguration language = main.getLanguage();
		if(playerMap == null) return;
		
		NationMapping nation = mappingRepo.getNationByID(playerMap.getNationID());
		if(nation == null) {
			player.sendMessage(GeneralMethods.format((OfflinePlayer)sender, language.getString("Command.Player.NotInNation.Message"), player.getName()));
			return;
		}
		if(!playerMap.getRank().equals(Rank.Leader)) {
			player.sendMessage(GeneralMethods.format((OfflinePlayer)sender, language.getString("Command.Player.NotALeader.Message"), nation.getName()));
			return;
		}
		
		if(!nation.getHomes().containsKey(home)) {
			player.sendMessage(GeneralMethods.format((OfflinePlayer)sender, language.getString("Command.Nations.Home.DelHome.NotFound.Message"), nation.getName()));
			return;
		}
		
		Bukkit.getPluginManager().callEvent(new DeleteHomeEvent(nation, sender, home));
	}

	@Override
	public String[] getArguments(UUID uuid) {
		MappingRepository mappingRepo = main.getMappingRepo();
		PlayerMapping playerMap = mappingRepo.getPlayerByUUID(uuid);
		NationMapping nation = mappingRepo.getNationByID(playerMap.getNationID());
		
		List<String> nationHomes = nation.getHomes().entrySet().stream().map(el -> el.getKey()).collect(Collectors.toList());
		return nationHomes.toArray(new String[nationHomes.size()]);
	}

	@Override
	public String[] getSubArguments(String[] args) {
		return null;
	}

	@Override
	public String getPermissionNode() {
		return null;
	}

	@Override
	public boolean hasTabCompletion() {
		return true;
	}

	@Override
	public String getName() {
		return "delhome";
	}

	@Override
	public String getSyntax() {
		return "/nations delhome <home>";
	}

	@Override
	public String getDescription() {
		return "Deletes a nation home";
	}

	@Override
	public List<ParentCommand> getSubCommands() {
		return null;
	}

	@Override
	public boolean isConsole() {
		return false;
	}

	@Override
	public ParentCommand getParentCommand() {
		return parent;
	}

	@Override
	public AjaxPlugin getMain() {
		// TODO Auto-generated method stub
		return main;
	}

}
