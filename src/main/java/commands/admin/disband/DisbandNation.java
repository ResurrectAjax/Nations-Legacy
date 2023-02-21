package commands.admin.disband;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;

import events.nation.disband.DisbandNationEvent;
import general.GeneralMethods;
import main.Main;
import me.resurrectajax.ajaxplugin.interfaces.ChildCommand;
import me.resurrectajax.ajaxplugin.interfaces.ParentCommand;
import persistency.MappingRepository;
import persistency.NationMapping;

public class DisbandNation extends ChildCommand{

	private ParentCommand parent;
	private Main main;
	public DisbandNation(ParentCommand parent) {
		this.main = (Main) parent.getMain();
		this.parent = parent;
	}
	
	@Override
	public void perform(CommandSender sender, String[] args) {
		FileConfiguration language = main.getLanguage();
		MappingRepository mappingRepo = main.getMappingRepo();
		
		String nationName = args.length < 3 ? null : args[2];
		super.beforePerform(sender, nationName);
		
		NationMapping nation = mappingRepo.getNationByName(nationName);
		if(args.length < 3) sender.sendMessage(GeneralMethods.getBadSyntaxMessage(getSyntax()));
		else if(nation == null) sender.sendMessage(GeneralMethods.format(sender, language.getString("Command.Nations.NotExist.Message"), args[2]));
		else Bukkit.getServer().getPluginManager().callEvent(new DisbandNationEvent(nation, sender));
	}

	@Override
	public String[] getArguments(UUID uuid) {
		MappingRepository mappingRepo = main.getMappingRepo();
		return mappingRepo.getNations().stream()
				.map(el -> el.getName())
				.collect(Collectors.toList())
				.toArray(new String[mappingRepo.getNations().size()]);
	}

	@Override
	public String getPermissionNode() {
		// TODO Auto-generated method stub
		return "nations.admin.disband";
	}

	@Override
	public boolean hasTabCompletion() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "disband";
	}

	@Override
	public String getSyntax() {
		// TODO Auto-generated method stub
		return "/nations admin disband <nation>";
	}

	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		return "Disband a nation";
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
