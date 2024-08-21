package me.resurrectajax.nationslegacy.commands.home;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.resurrectajax.ajaxplugin.interfaces.ChildCommand;
import me.resurrectajax.ajaxplugin.interfaces.ParentCommand;
import me.resurrectajax.ajaxplugin.plugin.AjaxPlugin;
import me.resurrectajax.nationslegacy.commands.home.validators.HomeValidator;
import me.resurrectajax.nationslegacy.events.nation.home.HomeTeleportEvent;
import me.resurrectajax.nationslegacy.main.Nations;
import me.resurrectajax.nationslegacy.persistency.MappingRepository;
import me.resurrectajax.nationslegacy.persistency.NationMapping;
import me.resurrectajax.nationslegacy.persistency.PlayerMapping;

public class HomeCommand extends ChildCommand{

	private ParentCommand parent;
	private Nations main;
	public HomeCommand(ParentCommand parent) {
		this.parent = parent;
		this.main = (Nations) parent.getMain();
	}
	
	@Override
	public void perform(CommandSender sender, String[] args) {
		String home = "home";
		
		if(args.length > 1) home = args[1];
		super.setLastArg(main, sender, home);
		
		if(!(sender instanceof Player)) return;
		Player player = (Player) sender;
		MappingRepository mappingRepo = main.getMappingRepo();
		PlayerMapping playerMap = mappingRepo.getPlayerByUUID(player.getUniqueId());
		NationMapping nation = mappingRepo.getNationByID(playerMap.getNationID());
		
		HomeValidator validator = new HomeValidator(sender, args, this);
		if(validator.validate()) Bukkit.getPluginManager().callEvent(new HomeTeleportEvent(nation, sender, home, nation.getHomes().get(home)));
		
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
		return "nations.player.home";
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
		return main.getLanguage().getString("HelpList.Home.Description");
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
