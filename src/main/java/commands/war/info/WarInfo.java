package commands.war.info;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.command.CommandSender;

import main.Main;
import me.resurrectajax.ajaxplugin.interfaces.ChildCommand;
import me.resurrectajax.ajaxplugin.interfaces.ParentCommand;
import persistency.MappingRepository;
import persistency.NationMapping;
import persistency.PlayerMapping;

public class WarInfo extends ChildCommand{

	private ParentCommand parent;
	private Main main;
	private MappingRepository mappingRepo;
	public WarInfo(ParentCommand parent) {
		this.parent = parent;
		this.main = (Main) parent.getMain();
		
		this.mappingRepo = main.getMappingRepo();
	}
	
	@Override
	public void perform(CommandSender sender, String[] args) {
		
		
	}

	@Override
	public String[] getArguments(UUID uuid) {
		PlayerMapping player = mappingRepo.getPlayerByUUID(uuid);
		Set<NationMapping> nations = mappingRepo.getNations();
		if(nations.size() == 0) return null;
		if(player != null && player.getNationID() != null) {
			Set<NationMapping> enemies = mappingRepo.getWarNationsByNationID(player.getNationID());
			if(enemies.size() > 0) return enemies.stream().map(NationMapping::getName).toList().toArray(new String[enemies.size()]);
			else {
				Set<NationMapping> filteredNations = nations.stream().filter(el -> el.getNationID() != player.getNationID()).collect(Collectors.toSet());
				return filteredNations.stream().map(NationMapping::getName).toList().toArray(new String[nations.size()]);
			}
		}
		else return nations.stream().map(NationMapping::getName).toList().toArray(new String[nations.size()]);
	}
	
	@Override
	public String[] getSubArguments(String[] args) {
		if(args.length < 2) return null;
		NationMapping nation = mappingRepo.getNationByName(args[1]);
		if(nation == null) return null;
		
		Set<NationMapping> enemies = mappingRepo.getWarNationsByNationID(nation.getNationID());
		return enemies.stream().map(NationMapping::getName).toList().toArray(new String[enemies.size()]);
	}

	@Override
	public String getPermissionNode() {
		return null;
	}

	@Override
	public boolean hasTabCompletion() {
		return true;
	}

	@Override
	public String getName() {
		return "info";
	}

	@Override
	public String getSyntax() {
		return "/nations war info <nation> <enemy>";
	}

	@Override
	public String getDescription() {
		return "Get info about a war between two nations";
	}

	@Override
	public List<ParentCommand> getSubCommands() {
		return null;
	}

	@Override
	public ParentCommand getParentCommand() {
		return parent;
	}

	@Override
	public boolean isConsole() {
		return true;
	}

}
