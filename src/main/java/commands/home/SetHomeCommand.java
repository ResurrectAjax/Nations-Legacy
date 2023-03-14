package commands.home;

import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import enumeration.Rank;
import events.nation.home.SetHomeEvent;
import general.GeneralMethods;
import main.Main;
import me.resurrectajax.ajaxplugin.interfaces.ChildCommand;
import me.resurrectajax.ajaxplugin.interfaces.ParentCommand;
import persistency.MappingRepository;
import persistency.NationMapping;
import persistency.PlayerMapping;

public class SetHomeCommand extends ChildCommand{

	private Main main;
	private ParentCommand parent;
	public SetHomeCommand(ParentCommand parent) {
		this.parent = parent;
		this.main = (Main) parent.getMain();
	}
	
	@Override
	public void perform(CommandSender sender, String[] args) {
		
		if(!(sender instanceof Player)) return;
		Player player = (Player) sender;
		
		super.beforePerform(sender, args.length < 2 ? "home" : args[1]);
		
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
		return "Sets a nation home at the player's current location";
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

}
