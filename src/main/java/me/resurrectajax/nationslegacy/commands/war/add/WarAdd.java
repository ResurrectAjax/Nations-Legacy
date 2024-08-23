package me.resurrectajax.nationslegacy.commands.war.add;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.resurrectajax.ajaxplugin.interfaces.ChildCommand;
import me.resurrectajax.ajaxplugin.interfaces.ParentCommand;
import me.resurrectajax.ajaxplugin.plugin.AjaxPlugin;
import me.resurrectajax.nationslegacy.commands.war.WarCommand;
import me.resurrectajax.nationslegacy.commands.war.add.validators.WarAddValidator;
import me.resurrectajax.nationslegacy.events.nation.war.DeclareWarEvent;
import me.resurrectajax.nationslegacy.main.Nations;
import me.resurrectajax.nationslegacy.persistency.MappingRepository;
import me.resurrectajax.nationslegacy.persistency.NationMapping;
import me.resurrectajax.nationslegacy.persistency.PlayerMapping;

public class WarAdd extends ChildCommand{
	private WarCommand parent;
	private Nations main;
	public WarAdd(WarCommand parent) {
		this.parent = parent;
		this.main = (Nations) parent.getMain();
	}
	
	@Override
	public void perform(CommandSender sender, String[] args) {
		Player player = (Player) sender;
		
		MappingRepository mappingRepo = main.getMappingRepo();
		PlayerMapping playerMap = mappingRepo.getPlayerByUUID(player.getUniqueId());
		NationMapping receivingNation = mappingRepo.getNationByName(args.length > 2 ? args[2] : "");
		NationMapping nation = mappingRepo.getNationByPlayer(playerMap);
		
		super.setLastArg(main, sender, args.length > 2 ? args[2] : "");
		if(receivingNation != null) {
			PlayerMapping pl = receivingNation.getPlayers().stream().findFirst().orElse(null);
			super.setLastMentioned(main, sender, Bukkit.getOfflinePlayer(pl.getUUID()));
		}
		
		WarAddValidator validator = new WarAddValidator(sender, args, this);
		if(validator.validate()) Bukkit.getPluginManager().callEvent(new DeclareWarEvent(nation, receivingNation, sender));
	}

	@Override
	public String[] getArguments(UUID uuid) {
		MappingRepository mappingRepo = main.getMappingRepo();
		NationMapping pNation = mappingRepo.getNationByPlayer(mappingRepo.getPlayerByUUID(uuid));
		
		if(pNation == null) return null;
		Set<String> nations = mappingRepo.getNations().stream()
				.filter(el -> el.getNationID() != pNation.getNationID() && !mappingRepo.getAllianceNationsByNationID(pNation.getNationID()).contains(el))
				.map(el -> el.getName())
				.collect(Collectors.toSet());
		return nations.toArray(new String[nations.size()]);
	}

	@Override
	public String getPermissionNode() {
		// TODO Auto-generated method stub
		return "nations.player.war.add";
	}

	@Override
	public boolean hasTabCompletion() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "add";
	}

	@Override
	public String getSyntax() {
		// TODO Auto-generated method stub
		return "/nations war add <nation>";
	}

	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		return main.getLanguage().getString("HelpList.War.Add.Description");
	}

	@Override
	public ParentCommand getParentCommand() {
		// TODO Auto-generated method stub
		return parent;
	}

	@Override
	public boolean isConsole() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public AjaxPlugin getMain() {
		// TODO Auto-generated method stub
		return main;
	}

}
