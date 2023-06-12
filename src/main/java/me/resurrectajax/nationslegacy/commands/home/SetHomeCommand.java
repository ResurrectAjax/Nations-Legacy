package me.resurrectajax.nationslegacy.commands.home;

import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import me.resurrectajax.ajaxplugin.interfaces.ChildCommand;
import me.resurrectajax.ajaxplugin.interfaces.ParentCommand;
import me.resurrectajax.ajaxplugin.plugin.AjaxPlugin;
import me.resurrectajax.nationslegacy.enumeration.Rank;
import me.resurrectajax.nationslegacy.events.nation.home.SetHomeEvent;
import me.resurrectajax.nationslegacy.general.GeneralMethods;
import me.resurrectajax.nationslegacy.main.Nations;
import me.resurrectajax.nationslegacy.persistency.MappingRepository;
import me.resurrectajax.nationslegacy.persistency.NationMapping;
import me.resurrectajax.nationslegacy.persistency.PlayerMapping;

public class SetHomeCommand extends ChildCommand{

	private Nations main;
	private ParentCommand parent;
	public SetHomeCommand(ParentCommand parent) {
		this.parent = parent;
		this.main = (Nations) parent.getMain();
	}
	
	@Override
	public void perform(CommandSender sender, String[] args) {
		
		if(!(sender instanceof Player)) return;
		Player player = (Player) sender;
		
		super.setLastArg(main, sender, args.length < 2 ? "home" : args[1]);
		
		MappingRepository mappingRepo = main.getMappingRepo();
		PlayerMapping playerMap = mappingRepo.getPlayerByUUID(player.getUniqueId());
		FileConfiguration config = main.getConfig(), language = main.getLanguage();
		int maxHomes = config.getInt("Nations.Home.MaxHomes");
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
		
		if(nation.getHomes().size() >= maxHomes && args.length >= 2) {
			player.sendMessage(GeneralMethods.format((OfflinePlayer)sender, language.getString("Command.Nations.Home.SetHome.MaxHomes.Message"), nation.getName()));
			return;
		}
		
		if(args.length >= 2) Bukkit.getPluginManager().callEvent(new SetHomeEvent(nation, sender, args[1].toLowerCase(), player.getLocation()));
		else Bukkit.getPluginManager().callEvent(new SetHomeEvent(nation, sender, null, player.getLocation()));
	}

	@Override
	public String[] getArguments(UUID uuid) {
		return null;
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
		return "sethome";
	}

	@Override
	public String getSyntax() {
		return "/nations sethome <home>";
	}

	@Override
	public String getDescription() {
		return main.getLanguage().getString("HelpList.Sethome.Description");
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
