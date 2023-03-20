package commands.home;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import events.nation.home.HomeTeleportEvent;
import general.GeneralMethods;
import main.Main;
import me.resurrectajax.ajaxplugin.interfaces.ChildCommand;
import me.resurrectajax.ajaxplugin.interfaces.ParentCommand;
import persistency.MappingRepository;
import persistency.NationMapping;
import persistency.PlayerMapping;

public class HomeCommand extends ChildCommand{

	private ParentCommand parent;
	private Main main;
	public HomeCommand(ParentCommand parent) {
		this.parent = parent;
		this.main = (Main) parent.getMain();
	}
	
	@Override
	public void perform(CommandSender sender, String[] args) {
		String home = "home";
		
		if(args.length > 1) home = args[1];
		super.setLastArg(sender, home);
		
		if(!(sender instanceof Player)) return;
		Player player = (Player) sender;
		MappingRepository mappingRepo = main.getMappingRepo();
		PlayerMapping playerMap = mappingRepo.getPlayerByUUID(player.getUniqueId());
		FileConfiguration language = main.getLanguage();
		
		if(playerMap == null || playerMap.getNationID() == null) player.sendMessage(GeneralMethods.format((OfflinePlayer)sender, language.getString("Command.Player.NotInNation.Message"), player.getName()));
		NationMapping nation = mappingRepo.getNationByID(playerMap.getNationID());
		
		if(!nation.getHomes().containsKey(home)) {
			player.sendMessage(GeneralMethods.format((OfflinePlayer)sender, language.getString("Command.Nations.Home.DelHome.NotFound.Message"), nation.getName()));
			return;
		}
		
		main.getServer().getPluginManager().callEvent(new HomeTeleportEvent(nation, sender, home, nation.getHomes().get(home)));
		
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
		return "home";
	}

	@Override
	public String getSyntax() {
		// TODO Auto-generated method stub
		return "/nations home <home>";
	}

	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		return "Teleport to your nation home";
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
