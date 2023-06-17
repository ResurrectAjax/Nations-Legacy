package me.resurrectajax.nationslegacy.commands.who;
import java.util.List;
import java.util.UUID;
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

public class WhoCommand extends ChildCommand{

	private final Nations main;
	private ParentCommand parent;
	public WhoCommand(ParentCommand parent) {
		this.main = (Nations) parent.getMain();
		this.parent = parent;
	}
	
	@Override
	public void perform(CommandSender sender, String[] args) {
		String name = args.length < 2 ? "" : args[1];
		setLastArg(main, sender, name);
		
		MappingRepository mappingRepo = main.getMappingRepo();
		FileConfiguration language = main.getLanguage();
		
		PlayerMapping playerMap = mappingRepo.getPlayerByName(args.length < 2 ? sender.getName() : name);
		if(args.length < 2 && sender instanceof OfflinePlayer) getInfo(sender, playerMap);
		else if(playerMap == null) sender.sendMessage(GeneralMethods.format((OfflinePlayer)sender, language.getString("Command.Player.NotExist.Message"), name));
		else getInfo(sender, playerMap);
	}
	
	private void getInfo(CommandSender sender, PlayerMapping playerMap) {
		MappingRepository mappingRepo = main.getMappingRepo();
		NationMapping nation = mappingRepo.getNationByID(playerMap.getNationID());
		
		sender.sendMessage(ChatColor.GOLD + GeneralMethods.padCenter("", '-', 35));
		sender.sendMessage(GeneralMethods.format("&bName: &a" + Bukkit.getOfflinePlayer(playerMap.getUUID()).getName()));
		sender.sendMessage(GeneralMethods.format("  &bNation: &a" + (nation != null ? nation.getName() : "")));
		sender.sendMessage(GeneralMethods.format("  &bRank: &a" + playerMap.getRank().toString()));
		sender.sendMessage(GeneralMethods.format("  &bKillpoints: &c" + playerMap.getKillpoints()));
		sender.sendMessage(ChatColor.GOLD + GeneralMethods.padCenter("", '-', 35));
	}

	@Override
	public String[] getArguments(UUID uuid) {
		// TODO Auto-generated method stub
		if(uuid != null) return Bukkit.getOnlinePlayers().stream()
				.filter(el -> !el.getUniqueId().equals(uuid))
				.map(el -> el.getName())
				.collect(Collectors.toList())
				.toArray(new String[Bukkit.getOnlinePlayers().size()-1]);
		return Bukkit.getOnlinePlayers().stream()
				.map(el -> el.getName())
				.collect(Collectors.toList())
				.toArray(new String[Bukkit.getOnlinePlayers().size()]);
	}

	@Override
	public String getPermissionNode() {
		// TODO Auto-generated method stub
		return "nations.player.who";
	}

	@Override
	public boolean hasTabCompletion() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "who";
	}

	@Override
	public String getSyntax() {
		// TODO Auto-generated method stub
		return "/nations who <player>";
	}

	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		return main.getLanguage().getString("HelpList.Who.Description");
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
