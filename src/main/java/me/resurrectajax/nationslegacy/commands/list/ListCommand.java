package me.resurrectajax.nationslegacy.commands.list;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;

import me.resurrectajax.ajaxplugin.interfaces.ChildCommand;
import me.resurrectajax.ajaxplugin.interfaces.ParentCommand;
import me.resurrectajax.ajaxplugin.plugin.AjaxPlugin;
import me.resurrectajax.nationslegacy.general.GeneralMethods;
import me.resurrectajax.nationslegacy.main.Nations;
import me.resurrectajax.nationslegacy.persistency.MappingRepository;
import me.resurrectajax.nationslegacy.persistency.NationMapping;

public class ListCommand extends ChildCommand{

	private ParentCommand parent;
	private Nations main;
	private int listSize = 10;
	private int pageAmount;
	public ListCommand(ParentCommand parent) {
		this.parent = parent;
		this.main = (Nations) parent.getMain();
	}
	
	@Override
	public void perform(CommandSender sender, String[] args) {
		
		MappingRepository mappingRepo = main.getMappingRepo();
		List<NationMapping> nations = mappingRepo.getNations().stream().sorted(new Comparator<NationMapping>() {
			@Override
			public int compare(NationMapping o1, NationMapping o2) {
				return Integer.compare(o2.countKillPoints(), o1.countKillPoints());
			}
			
		}).toList();
		
		if(nations.size() % listSize != 0) {
			pageAmount = (nations.size() / listSize) + 1;
		}
		else {
			pageAmount = (nations.size() / listSize);
		}
		
		if(args.length <= 1) generateList(sender, nations, 1);
		else if(GeneralMethods.isInteger(args[1])) generateList(sender, nations, Integer.parseInt(args[1]));
	}
	
	private void generateList(CommandSender sender, List<NationMapping> nations, int page) {
		FileConfiguration language = main.getLanguage();
		if(page > pageAmount) {
			sender.sendMessage(GeneralMethods.format(sender, language.getString("Command.Nations.List.EndOfList.Message"), page+""));
			return;
		}
		
		sender.sendMessage(ChatColor.GREEN + GeneralMethods.padCenter("Nations", '-', 35));
		for(int i = (page * listSize)-listSize; i < page * listSize; i++) {
			if(i >= nations.size()) break;
			NationMapping nation = nations.get(i);
			sender.sendMessage(GeneralMethods.format(String.format("&b%d. &6%s &8- &c%sp", (i+1), nation.getName(), nation.countKillPoints())));
		}
		sender.sendMessage(ChatColor.GREEN + GeneralMethods.padCenter("", '-', 34));
	}

	@Override
	public String[] getArguments(UUID uuid) {
		List<String> args = new ArrayList<>();
		for(int i = 1; i <= pageAmount; i++) {
			args.add(i+"");
		}
		return args.toArray(new String[args.size()]);
	}

	@Override
	public String[] getSubArguments(String[] args) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getPermissionNode() {
		// TODO Auto-generated method stub
		return "nations.player.list";
	}

	@Override
	public boolean hasTabCompletion() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "list";
	}

	@Override
	public String getSyntax() {
		// TODO Auto-generated method stub
		return "/nations list";
	}

	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		return main.getLanguage().getString("HelpList.List.Description");
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
	public AjaxPlugin getMain() {
		// TODO Auto-generated method stub
		return main;
	}

}
