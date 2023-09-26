package me.resurrectajax.nationslegacy.commands.info;

import java.util.List;
import java.util.Set;
import java.util.TreeMap;
import java.util.UUID;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;

import me.resurrectajax.ajaxplugin.interfaces.ChildCommand;
import me.resurrectajax.ajaxplugin.interfaces.ParentCommand;
import me.resurrectajax.ajaxplugin.plugin.AjaxPlugin;
import me.resurrectajax.nationslegacy.general.GeneralMethods;
import me.resurrectajax.nationslegacy.main.Nations;
import me.resurrectajax.nationslegacy.persistency.MappingRepository;
import me.resurrectajax.nationslegacy.persistency.NationMapping;
import me.resurrectajax.nationslegacy.persistency.PlayerMapping;
import me.resurrectajax.nationslegacy.ranking.Rank;

public class NationInfoCommand extends ChildCommand{
	private final Nations main;
	private ParentCommand parent;
	public NationInfoCommand(ParentCommand parent) {
		this.main = (Nations) parent.getMain();
		this.parent = parent;
	}

	@Override
	public void perform(CommandSender sender, String[] args) {
		FileConfiguration language = main.getLanguage();
		MappingRepository mappingRepo = main.getMappingRepo();
		super.setLastArg(main, sender, args.length < 2 ? "" : args[1]);
		
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
			else sender.sendMessage(GeneralMethods.format((OfflinePlayer)sender, language.getString("Command.Player.NotInNation.Message"), ""));
			return;
		}
		
		if(args.length != 2) sender.sendMessage(GeneralMethods.getBadSyntaxMessage(main, getSyntax()));
		else if(!Pattern.matches("[a-zA-Z]+", args[1])) GeneralMethods.format(sender, language.getString("Command.Error.SpecialCharacters.Message"), args[1]);
		else if(mappingRepo.getNationByName(args[1]) == null) sender.sendMessage(GeneralMethods.format(sender, language.getString("Command.Nations.NotExist.Message"), args[1]));
		else giveInfo(sender, args[1]);
		
	}
	
	private void giveInfo(CommandSender sender, String nation) {
		MappingRepository mappingRepo = main.getMappingRepo();
		FileConfiguration config = main.getConfig();
		int chunkLimit = config.getInt("Nations.Claiming.ChunkGain.Limit");
		
		NationMapping nationMap = mappingRepo.getNationByName(nation);
		if(nationMap != null) {
			PlayerMapping pl = nationMap.getPlayers().stream().findFirst().orElse(null);
			super.setLastMentioned(main, sender, Bukkit.getOfflinePlayer(pl.getUUID()));
		}
		
		TreeMap<Rank, Set<PlayerMapping>> playersByRank = nationMap.getPlayers().stream().collect(Collectors.groupingBy(PlayerMapping::getRank, TreeMap::new, Collectors.toSet()));
		
		sender.sendMessage(ChatColor.GOLD + GeneralMethods.padCenter("Nation Info", '-', 37));
		sender.sendMessage(GeneralMethods.format("&bNation: &a&l" + nationMap.getName()));
		sender.sendMessage(GeneralMethods.format("  &bKill Points: &c" + nationMap.countKillPoints() + "p"));
		if(chunkLimit != -1) sender.sendMessage(GeneralMethods.format("  &bGained chunks: " + String.format("&a%d&b/&a%d", nationMap.getGainedChunks(), chunkLimit)));
		sender.sendMessage(GeneralMethods.format("  &bClaimed chunks: " + String.format("&a%d&b/&a%d", nationMap.getClaimedChunks().size(), nationMap.getMaxChunks())));
		sender.sendMessage(GeneralMethods.format("  &bDescription: &f" + nationMap.getDescription()));
		
		playersByRank.entrySet().forEach(entry -> {
			String mess = String.format("  &b%s: &a%s", entry.getKey(), givePlayerList(entry.getValue()));
			sender.sendMessage(GeneralMethods.format(mess));
		});
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
		return "nations.player.info";
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
		return main.getLanguage().getString("HelpList.Info.Description");
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

	@Override
	public AjaxPlugin getMain() {
		// TODO Auto-generated method stub
		return main;
	}
	
	
}
