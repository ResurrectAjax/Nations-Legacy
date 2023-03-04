package commands.info;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;

import general.GeneralMethods;
import main.Main;
import me.resurrectajax.ajaxplugin.interfaces.ChildCommand;
import me.resurrectajax.ajaxplugin.interfaces.ParentCommand;
import persistency.MappingRepository;
import persistency.NationMapping;
import persistency.PlayerMapping;

public class NationInfoCommand extends ChildCommand{
	private final Main main;
	private ParentCommand parent;
	public NationInfoCommand(ParentCommand parent) {
		this.main = (Main) parent.getMain();
		this.parent = parent;
	}

	@Override
	public void perform(CommandSender sender, String[] args) {
		FileConfiguration language = main.getLanguage();
		MappingRepository mappingRepo = main.getMappingRepo();
		
		
		super.beforePerform(sender, args.length < 2 ? "" : args[1]);
		
		if(args.length == 1) {
			if(!(sender instanceof OfflinePlayer)) {
				sender.sendMessage(GeneralMethods.format(language.getString("Command.Error.ByConsole.Message")));
				return;
			}
			
			String nation = "";
			NationMapping nationMap = mappingRepo
					.getNationByPlayer(mappingRepo
					.getPlayerByUUID(((OfflinePlayer) sender)
					.getUniqueId()));
			nation = nationMap == null ? null : nationMap.getName();
			if(nation != null) giveInfo(sender, nation);
			else sender.sendMessage(GeneralMethods.format(sender, language.getString("Command.Player.NotInNation.Message"), ""));
			return;
		}
		
		if(args.length > 2) sender.sendMessage(GeneralMethods.getBadSyntaxMessage(getSyntax()));
		else if(!Pattern.matches("[a-zA-Z]+", args[1])) GeneralMethods.format(sender, language.getString("Command.Error.SpecialCharacters.Message"), args[1]);
		else if(mappingRepo.getNationByName(args[1]) == null) sender.sendMessage(GeneralMethods.format(sender, language.getString("Command.Nations.NotExist.Message"), args[1]));
		else giveInfo(sender, args[1]);
		
	}
	
	private void giveInfo(CommandSender sender, String nation) {
		MappingRepository mappingRepo = main.getMappingRepo();
		NationMapping nationMap = mappingRepo.getNationByName(nation);
		
		sender.sendMessage(ChatColor.GOLD + GeneralMethods.padCenter("", '-', 35));
		sender.sendMessage(GeneralMethods.format("&bNation: &a&l" + nationMap.getName()));
		sender.sendMessage(GeneralMethods.format("  &bKill Points: &c" + nationMap.countKillPoints() + "p"));
		sender.sendMessage(GeneralMethods.format("  &bDescription: &f" + nationMap.getDescription()));
		sender.sendMessage(GeneralMethods.format("  &bLeaders: &a" + givePlayerList(nationMap.getLeaders())));
		sender.sendMessage(GeneralMethods.format("  &bOfficers: &a" + givePlayerList(nationMap.getOfficers())));
		sender.sendMessage(GeneralMethods.format("  &bMembers: &a" + givePlayerList(nationMap.getMembers())));
		sender.sendMessage(GeneralMethods.format("  &bAllies: " + giveNationList(mappingRepo.getAllianceNationsByNationID(nationMap.getNationID()), ChatColor.GOLD, ChatColor.GREEN)));
		sender.sendMessage(GeneralMethods.format("  &bEnemies: " + giveNationList(mappingRepo.getWarNationsByNationID(nationMap.getNationID()), ChatColor.RED, ChatColor.GREEN)));
		sender.sendMessage(ChatColor.GOLD + GeneralMethods.padCenter("", '-', 35));
	}
	private String givePlayerList(Set<PlayerMapping> players) {
		return players.stream().map(el -> Bukkit.getOfflinePlayer(el.getUUID()).getName()).collect(Collectors.joining(", "));
	}
	private String giveNationList(Set<NationMapping> nations, ChatColor nameColor, ChatColor joiningColor) {
		return nations.stream().map(el -> nameColor + el.getName()).collect(Collectors.joining(joiningColor + ", "));
	}

	@Override
	public String[] getArguments(UUID uuid) {
		MappingRepository mappingRepo = main.getMappingRepo();
		PlayerMapping playerMap = mappingRepo.getPlayerByUUID(uuid);
		NationMapping pNation = playerMap != null ? mappingRepo.getNationByID(playerMap.getNationID()) : null;
		List<String> nations;
		
		if(pNation != null) {
			nations = mappingRepo.getNations().stream()
					.filter(el -> el.getNationID() != pNation.getNationID())
					.map(NationMapping::getName)
					.collect(Collectors.toList());
			
			return nations.toArray(new String[nations.size()]);
		}					
		else {
			nations = mappingRepo.getNations().stream()
					.map(NationMapping::getName)
					.collect(Collectors.toList());
			return nations.toArray(new String[nations.size()]);
		}
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
		return "info";
	}

	@Override
	public String getSyntax() {
		// TODO Auto-generated method stub
		return "/nations info <nation>";
	}

	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		return "Get the info of a specified nation";
	}

	@Override
	public List<ParentCommand> getSubCommands() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isConsole() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public ParentCommand getParentCommand() {
		// TODO Auto-generated method stub
		return parent;
	}

	@Override
	public String[] getSubArguments(String[] args) {
		// TODO Auto-generated method stub
		return null;
	}
	
	
}
