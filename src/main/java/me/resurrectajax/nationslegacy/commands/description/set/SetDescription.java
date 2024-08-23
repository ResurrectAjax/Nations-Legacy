package me.resurrectajax.nationslegacy.commands.description.set;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.resurrectajax.ajaxplugin.interfaces.ChildCommand;
import me.resurrectajax.ajaxplugin.interfaces.ParentCommand;
import me.resurrectajax.ajaxplugin.plugin.AjaxPlugin;
import me.resurrectajax.nationslegacy.commands.description.set.validators.SetDescriptionValidator;
import me.resurrectajax.nationslegacy.events.nation.description.SetNationDescriptionEvent;
import me.resurrectajax.nationslegacy.main.Nations;
import me.resurrectajax.nationslegacy.persistency.MappingRepository;
import me.resurrectajax.nationslegacy.persistency.NationMapping;
import me.resurrectajax.nationslegacy.persistency.PlayerMapping;

public class SetDescription extends ChildCommand{
	private Nations main;
	private ParentCommand parent;
	public SetDescription(ParentCommand parent) {
		this.main = (Nations) parent.getMain();
		this.parent = parent;
	}
	
	@Override
	public void perform(CommandSender sender, String[] args) {
		MappingRepository mappingRepo = main.getMappingRepo();
		
		PlayerMapping playerMap = mappingRepo.getPlayerByUUID(((Player) sender).getUniqueId());
		NationMapping nation = mappingRepo.getNationByID(playerMap.getNationID());
		
		String desc = "";
		for(int i = 2; i < args.length; i++) {
			desc += (i == args.length-1 ? args[i] : args[i] + " ");
		}
		
		super.setLastArg(main, sender, args.length < 3 ? "" : args[2]);
		
		SetDescriptionValidator validator = new SetDescriptionValidator(sender, args, this);
		if(validator.validate()) Bukkit.getPluginManager().callEvent(new SetNationDescriptionEvent(nation, sender, desc));
	}

	@Override
	public String[] getArguments(UUID uuid) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getPermissionNode() {
		// TODO Auto-generated method stub
		return "nations.player.description.set";
	}

	@Override
	public boolean hasTabCompletion() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "set";
	}

	@Override
	public String getSyntax() {
		// TODO Auto-generated method stub
		return "/nations description set <description>";
	}

	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		return main.getLanguage().getString("HelpList.Description.Set.Description");
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
