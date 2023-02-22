package commands.list;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;

import general.GeneralMethods;
import main.Main;
import me.resurrectajax.ajaxplugin.interfaces.ChildCommand;
import me.resurrectajax.ajaxplugin.interfaces.ParentCommand;
import persistency.MappingRepository;
import persistency.NationMapping;

public class ListCommand extends ChildCommand{

	private ParentCommand parent;
	private Main main;
	private int listSize = 10;
	private int pageAmount;
	private List<NationMapping> nations;
	public ListCommand(ParentCommand parent) {
		this.parent = parent;
		this.main = (Main) parent.getMain();
		
		MappingRepository mappingRepo = main.getMappingRepo();
		nations = mappingRepo.getNations().stream().sorted(new Comparator<NationMapping>() {
			@Override
			public int compare(NationMapping o1, NationMapping o2) {
				return Integer.compare(o1.countKillPoints(), o2.countKillPoints());
			}
			
		}).toList();
		
		if(nations.size() % listSize != 0) {
			pageAmount = (nations.size() / listSize) + 1;
		}
		else {
			pageAmount = (nations.size() / listSize);
		}
	}
	
	@Override
	public void perform(CommandSender sender, String[] args) {
		if(args.length <= 1) generateList(sender, 1);
		else if(GeneralMethods.isInteger(args[1])) generateList(sender, Integer.parseInt(args[1]));
	}
	
	private void generateList(CommandSender sender, int page) {
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
		return "Returns a list of nations ordered by killpoints";
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
