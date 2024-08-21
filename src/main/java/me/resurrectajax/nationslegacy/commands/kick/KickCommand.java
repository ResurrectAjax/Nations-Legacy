package me.resurrectajax.nationslegacy.commands.kick;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.resurrectajax.ajaxplugin.interfaces.ChildCommand;
import me.resurrectajax.ajaxplugin.interfaces.ParentCommand;
import me.resurrectajax.ajaxplugin.plugin.AjaxPlugin;
import me.resurrectajax.nationslegacy.commands.kick.validators.KickValidator;
import me.resurrectajax.nationslegacy.events.nation.kick.KickFromNationEvent;
import me.resurrectajax.nationslegacy.main.Nations;
import me.resurrectajax.nationslegacy.persistency.MappingRepository;
import me.resurrectajax.nationslegacy.persistency.NationMapping;
import me.resurrectajax.nationslegacy.persistency.PlayerMapping;
import me.resurrectajax.nationslegacy.ranking.Rank;

public class KickCommand extends ChildCommand{

	private ParentCommand parent;
	private Nations main;
	public KickCommand(ParentCommand parent) {
		this.parent = parent;
		this.main = (Nations) parent.getMain();
	}
	
	@Override
	public void perform(CommandSender sender, String[] args) {
		MappingRepository mappingRepo = main.getMappingRepo();
		
		super.setLastArg(main, sender, args.length == 2 ? args[1] : "");
		PlayerMapping player = mappingRepo.getPlayerByName(args.length == 2 ? args[1] : "");
		
		KickValidator validator = new KickValidator(sender, args, this);
		if(validator.validate()) {
			PlayerMapping senderMap = mappingRepo.getPlayerByUUID(((Player)sender).getUniqueId());
			OfflinePlayer offPlayer = Bukkit.getOfflinePlayer(player.getUUID());
			super.setLastMentioned(main, sender, offPlayer);
			
			Bukkit.getPluginManager().callEvent(new KickFromNationEvent(mappingRepo.getNationByID(senderMap.getNationID()), sender, player));
		}
	}

	@Override
	public String[] getArguments(UUID uuid) {
		MappingRepository mappingRepo = main.getMappingRepo();
		PlayerMapping player = mappingRepo.getPlayerByUUID(uuid);
		if(player.getNationID() == null) return null;
		
		NationMapping nation = mappingRepo.getNationByID(player.getNationID());
		Set<String> members = nation.getPlayers().stream()
			.filter(el -> !el.getRank().equals(Rank.getHighest()))
			.map(el -> Bukkit.getOfflinePlayer(el.getUUID()).getName())
			.collect(Collectors.toSet());
		
		return members.toArray(new String[members.size()]);
	}

	@Override
	public String[] getSubArguments(String[] args) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getPermissionNode() {
		// TODO Auto-generated method stub
		return "nations.player.kick";
	}

	@Override
	public boolean hasTabCompletion() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "kick";
	}

	@Override
	public String getSyntax() {
		// TODO Auto-generated method stub
		return "/nations kick <player>";
	}

	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		return main.getLanguage().getString("HelpList.Kick.Description");
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
