package me.resurrectajax.nationslegacy.commands.ranks;

import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.resurrectajax.ajaxplugin.interfaces.ChildCommand;
import me.resurrectajax.ajaxplugin.interfaces.ParentCommand;
import me.resurrectajax.ajaxplugin.plugin.AjaxPlugin;
import me.resurrectajax.nationslegacy.commands.ranks.validators.PromoteValidator;
import me.resurrectajax.nationslegacy.events.nation.ranks.PromoteEvent;
import me.resurrectajax.nationslegacy.general.GeneralMethods;
import me.resurrectajax.nationslegacy.main.Nations;
import me.resurrectajax.nationslegacy.persistency.MappingRepository;
import me.resurrectajax.nationslegacy.persistency.NationMapping;
import me.resurrectajax.nationslegacy.persistency.PlayerMapping;
import me.resurrectajax.nationslegacy.ranking.Rank;

public class PromoteCommand extends ChildCommand{

	private ParentCommand parent;
	private Nations main;
	public PromoteCommand(ParentCommand parent) {
		this.parent = parent;
		this.main = (Nations)parent.getMain();
	}
	
	@Override
	public void perform(CommandSender sender, String[] args) {
		super.setLastArg(main, sender, args.length < 2 ? "" : args[1]);
		
		if(args.length != 2) sender.sendMessage(GeneralMethods.getBadSyntaxMessage(main, getSyntax()));
		
		MappingRepository mappingRepo = main.getMappingRepo();
		PlayerMapping player = mappingRepo.getPlayerByName(args[1]), promoter = mappingRepo.getPlayerByUUID(((Player)sender).getUniqueId());
		if(player != null) super.setLastMentioned(main, sender, Bukkit.getOfflinePlayer(player.getUUID()));
		NationMapping nation = mappingRepo.getNationByID(promoter.getNationID());
		PromoteValidator validator = new PromoteValidator(sender, args, this);
		if(validator.validate()) Bukkit.getPluginManager().callEvent(new PromoteEvent(nation, sender, player));
	}

	@Override
	public String[] getArguments(UUID uuid) {
		MappingRepository mappingRepo = main.getMappingRepo();
		PlayerMapping playerMap = mappingRepo.getPlayerByUUID(uuid);
		if(playerMap.getNationID() == null || !playerMap.getRank().equals(Rank.getHighest())) return null;
		NationMapping nation = mappingRepo.getNationByID(playerMap.getNationID());
		List<String> players = nation.getPlayers().stream()
						.filter(el -> !el.getRank().equals(Rank.getHighest()))
						.map(el -> Bukkit.getPlayer(el.getUUID()).getName())
						.toList();
		
		return players.toArray(new String[players.size()]);
	}

	@Override
	public String getPermissionNode() {
		// TODO Auto-generated method stub
		return "nations.player.promote";
	}

	@Override
	public boolean hasTabCompletion() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "promote";
	}

	@Override
	public String getSyntax() {
		// TODO Auto-generated method stub
		return "/nations promote <player>";
	}

	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		return main.getLanguage().getString("HelpList.Promote.Description");
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
