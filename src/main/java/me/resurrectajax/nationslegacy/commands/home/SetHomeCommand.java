package me.resurrectajax.nationslegacy.commands.home;

import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.resurrectajax.ajaxplugin.interfaces.ChildCommand;
import me.resurrectajax.ajaxplugin.interfaces.ParentCommand;
import me.resurrectajax.ajaxplugin.plugin.AjaxPlugin;
import me.resurrectajax.nationslegacy.commands.home.validators.SetHomeValidator;
import me.resurrectajax.nationslegacy.events.nation.home.SetHomeEvent;
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
		NationMapping nation = mappingRepo.getNationByID(playerMap.getNationID());
		SetHomeValidator validator = new SetHomeValidator(sender, args, this);
		
		if(validator.validate()) {
			if(args.length >= 2) Bukkit.getPluginManager().callEvent(new SetHomeEvent(nation, sender, args[1].toLowerCase(), player.getLocation()));
			else Bukkit.getPluginManager().callEvent(new SetHomeEvent(nation, sender, null, player.getLocation()));	
		}
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
		return "nations.player.sethome";
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
