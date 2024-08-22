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
import me.resurrectajax.nationslegacy.commands.home.validators.DeleteHomeValidator;
import me.resurrectajax.nationslegacy.events.nation.home.DeleteHomeEvent;
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
		NationMapping nation = mappingRepo.getNationByID(playerMap.getNationID());
		
		DeleteHomeValidator validator = new DeleteHomeValidator(sender, args, this);
		if(validator.validate()) Bukkit.getPluginManager().callEvent(new DeleteHomeEvent(nation, sender, home));
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
	public String getPermissionNode() {
		return "nations.player.delhome";
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
		return main.getLanguage().getString("HelpList.Delhome.Description");
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
