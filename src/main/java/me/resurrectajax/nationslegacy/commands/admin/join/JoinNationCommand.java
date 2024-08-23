package me.resurrectajax.nationslegacy.commands.admin.join;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import me.resurrectajax.ajaxplugin.interfaces.ChildCommand;
import me.resurrectajax.ajaxplugin.interfaces.ParentCommand;
import me.resurrectajax.ajaxplugin.plugin.AjaxPlugin;
import me.resurrectajax.nationslegacy.ranking.Rank;
import me.resurrectajax.nationslegacy.events.nation.join.JoinNationEvent;
import me.resurrectajax.nationslegacy.general.GeneralMethods;
import me.resurrectajax.nationslegacy.main.Nations;
import me.resurrectajax.nationslegacy.persistency.MappingRepository;
import me.resurrectajax.nationslegacy.persistency.NationMapping;
import me.resurrectajax.nationslegacy.persistency.PlayerMapping;

public class JoinNationCommand extends ChildCommand{

	private Nations main;
	private ParentCommand parent;
	public JoinNationCommand(ParentCommand parent) {
		this.parent = parent;
		this.main = (Nations) parent.getMain();
	}
	
	@Override
	public void perform(CommandSender sender, String[] args) {
		MappingRepository mappingRepo = main.getMappingRepo();
		NationMapping nation = mappingRepo.getNationByName(args.length < 3 ? "" : args[2]);
		
		super.setLastArg(main, sender, args.length < 3 ? "" : args[2]);
		if(nation != null) {
			PlayerMapping pl = nation.getPlayers().stream().findFirst().orElse(null);
			super.setLastMentioned(main, sender, Bukkit.getOfflinePlayer(pl.getUUID()));
		}
		
		if(args.length < 3) sender.sendMessage(GeneralMethods.getBadSyntaxMessage(main, getSyntax()));
		else Bukkit.getPluginManager().callEvent(new JoinNationEvent(nation, sender, Rank.getHighest()));
	}

	@Override
	public String[] getArguments(UUID uuid) {
		MappingRepository mappingRepo = main.getMappingRepo();
		Set<String> nations = mappingRepo.getNations().stream().map(el -> el.getName()).collect(Collectors.toSet());
		return nations.toArray(new String[nations.size()]);
	}

	@Override
	public String getPermissionNode() {
		// TODO Auto-generated method stub
		return "nations.admin.join";
	}

	@Override
	public boolean hasTabCompletion() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "join";
	}

	@Override
	public String getSyntax() {
		// TODO Auto-generated method stub
		return "/nations admin join <nation>";
	}

	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		return main.getLanguage().getString("HelpList.Admin.Join.Description");
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
		return parent.getMain();
	}
	
}
