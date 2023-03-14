package commands.ranks;

import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import enumeration.Rank;
import events.nation.ranks.PromoteEvent;
import general.GeneralMethods;
import main.Main;
import me.resurrectajax.ajaxplugin.interfaces.ChildCommand;
import me.resurrectajax.ajaxplugin.interfaces.ParentCommand;
import persistency.MappingRepository;
import persistency.NationMapping;
import persistency.PlayerMapping;

public class PromoteCommand extends ChildCommand{

	private ParentCommand parent;
	private Main main;
	public PromoteCommand(ParentCommand parent) {
		this.parent = parent;
		this.main = (Main)parent.getMain();
	}
	
	@Override
	public void perform(CommandSender sender, String[] args) {
		super.beforePerform(sender, args.length < 2 ? "" : args[1]);
		
		FileConfiguration language = main.getLanguage();
		
		if(args.length != 2) sender.sendMessage(GeneralMethods.getBadSyntaxMessage(getSyntax()));
		if(!(sender instanceof Player)) {
			sender.sendMessage(GeneralMethods.format((OfflinePlayer)sender, language.getString("Command.Error.ByConsole.Message"), args[1]));
			return;
		}
		if(Bukkit.getPlayer(args[1]) == null) {
			sender.sendMessage(GeneralMethods.format((OfflinePlayer)sender, language.getString("Command.Player.NotExist.Message"), args[1]));
			return;
		}
		
		MappingRepository mappingRepo = main.getMappingRepo();
		PlayerMapping player = mappingRepo.getPlayerByName(args[1]), promoter = mappingRepo.getPlayerByUUID(((Player)sender).getUniqueId());
		OfflinePlayer offPlayer = Bukkit.getOfflinePlayer(player.getUUID());
		
		if(promoter.getNationID() == null) {
			sender.sendMessage(GeneralMethods.format((OfflinePlayer)sender, language.getString("Command.Player.NotInNation.Message"), args[1]));
			return;
		}
		NationMapping nation = mappingRepo.getNationByID(promoter.getNationID());
		
		if(player.getNationID() != promoter.getNationID()) {
			sender.sendMessage(GeneralMethods.format((OfflinePlayer)sender, language.getString("Command.Player.NotInSameNation.Message"), args[1]));
			return;
		}
		if(!promoter.getRank().equals(Rank.Leader)) {
			sender.sendMessage(GeneralMethods.format((OfflinePlayer)sender, language.getString("Command.Player.NotALeader.Message"), args[1]));
			return;
		}
		if(player.getRank().equals(Rank.Leader)) {
			sender.sendMessage(GeneralMethods.relFormat(sender, (CommandSender)offPlayer, language.getString("Command.Player.Promote.AlreadyHighestRank.Message"), args[1]));
			return;
		}
		
		main.getServer().getPluginManager().callEvent(new PromoteEvent(nation, sender, player));
	}

	@Override
	public String[] getArguments(UUID uuid) {
		MappingRepository mappingRepo = main.getMappingRepo();
		PlayerMapping playerMap = mappingRepo.getPlayerByUUID(uuid);
		if(playerMap.getNationID() == null || !playerMap.getRank().equals(Rank.Leader)) return null;
		NationMapping nation = mappingRepo.getNationByID(playerMap.getNationID());
		List<String> players = nation.getAllMembers().stream()
						.filter(el -> !el.getRank()
						.equals(Rank.Leader))
						.map(el -> Bukkit.getPlayer(uuid).getName())
						.toList();
		
		return players.toArray(new String[players.size()]);
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
		return "Promote a member or officer to a higher rank";
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
