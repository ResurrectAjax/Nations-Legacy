package me.resurrectajax.nationslegacy.commands.flag.set;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import me.resurrectajax.ajaxplugin.interfaces.ChildCommand;
import me.resurrectajax.ajaxplugin.interfaces.ParentCommand;
import me.resurrectajax.ajaxplugin.plugin.AjaxPlugin;
import me.resurrectajax.nationslegacy.commands.flag.set.validators.FlagSetValidator;
import me.resurrectajax.nationslegacy.enumeration.Flag;
import me.resurrectajax.nationslegacy.general.GeneralMethods;
import me.resurrectajax.nationslegacy.main.Nations;
import me.resurrectajax.nationslegacy.persistency.MappingRepository;
import me.resurrectajax.nationslegacy.persistency.NationMapping;
import me.resurrectajax.nationslegacy.persistency.PlayerMapping;

public class FlagSetCommand extends ChildCommand{

	private Nations main;
	private ParentCommand parent;
	public FlagSetCommand(ParentCommand parent) {
		this.parent = parent;
		this.main = (Nations) parent.getMain();
	}
	
	@Override
	public void perform(CommandSender sender, String[] args) {
		FileConfiguration language = main.getLanguage();
		MappingRepository mappingRepo = main.getMappingRepo();
		
		PlayerMapping playerMap = mappingRepo.getPlayerByUUID(((Player) sender).getUniqueId());
		NationMapping nation = mappingRepo.getNationByID(playerMap.getNationID());
		
		super.setLastArg(main, sender, args.length < 3 ? "" : args[2]);
		
		FlagSetValidator validator = new FlagSetValidator(sender, args, this);
		if(validator.validate()) {
			boolean isAllow = args[3].equalsIgnoreCase("allow");
			String langPath = isAllow ? "Command.Nations.Flag.Set.Allow.Message" : "Command.Nations.Flag.Set.Deny.Message";
			sender.sendMessage(GeneralMethods.format((OfflinePlayer)sender, language.getString(langPath), args[2]));
			nation.setFlag(Flag.getFromString(args[2]), isAllow);
		}
	}

	@Override
	public String[] getArguments(UUID uuid) {
		List<String> flags = Arrays.asList(Flag.values()).stream().map(el -> el.toString()).toList();
		return flags.toArray(new String[flags.size()]);
	}

	@Override
	public String[] getSubArguments(String[] args) {
		return new String[]{"ALLOW", "DENY"};
	}

	@Override
	public String getPermissionNode() {
		// TODO Auto-generated method stub
		return "nations.player.flag.set";
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
		return "/nations flag set <flag> <ALLOW | DENY>";
	}

	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		return main.getLanguage().getString("HelpList.Flag.Set.Description");
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
