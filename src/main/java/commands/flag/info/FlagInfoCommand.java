package commands.flag.info;

import java.util.List;
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

public class FlagInfoCommand extends ChildCommand{

	private Main main;
	private ParentCommand parent;
	public FlagInfoCommand(ParentCommand parent) {
		this.parent = parent;
		this.main = (Main) parent.getMain();
	}
	
	@Override
	public void perform(CommandSender sender, String[] args) {
		FileConfiguration language = main.getLanguage();
		MappingRepository mappingRepo = main.getMappingRepo();
		
		
		super.setLastArg(sender, args.length < 3 ? "" : args[2]);
		
		if(args.length == 2) {
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
			else sender.sendMessage(GeneralMethods.format((OfflinePlayer)sender, language.getString("Command.Player.NotInNation.Message"), ""));
			return;
		}
		
		if(args.length > 3) sender.sendMessage(GeneralMethods.getBadSyntaxMessage(getSyntax()));
		else if(!Pattern.matches("[a-zA-Z]+", args[2])) GeneralMethods.format((OfflinePlayer)sender, language.getString("Command.Error.SpecialCharacters.Message"), args[2]);
		else if(mappingRepo.getNationByName(args[2]) == null) sender.sendMessage(GeneralMethods.format((OfflinePlayer)sender, language.getString("Command.Nations.NotExist.Message"), args[2]));
		else giveInfo(sender, args[2]);
		
	}
	
	private void giveInfo(CommandSender sender, String nation) {
		MappingRepository mappingRepo = main.getMappingRepo();
		NationMapping nationMap = mappingRepo.getNationByName(nation);
		if(nationMap != null) {
			PlayerMapping pl = nationMap.getAllMembers().stream().findFirst().orElse(null);
			super.setLastMentioned(sender, Bukkit.getOfflinePlayer(pl.getUUID()));
		}
		
		sender.sendMessage(ChatColor.GOLD + GeneralMethods.padCenter("Nation Flags", '-', 37));
		sender.sendMessage(GeneralMethods.format("&bNation: &a&l" + nationMap.getName()));
		nationMap.getFlags().entrySet().forEach(entry -> {
			String allow = entry.getValue() ? "&a&lALLOW" : "&c&lDENY";
			sender.sendMessage(GeneralMethods.format(String.format("  &b%s: %s", entry.getKey().toString(), allow)));
		});
		sender.sendMessage(ChatColor.GOLD + GeneralMethods.padCenter("", '-', 35));
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
		return "info";
	}

	@Override
	public String getSyntax() {
		// TODO Auto-generated method stub
		return "/nations flag info <nation>";
	}

	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		return "Get the flag info of a nation";
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

}
