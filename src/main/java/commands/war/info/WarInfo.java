package commands.war.info;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import general.GeneralMethods;
import main.Main;
import me.resurrectajax.ajaxplugin.interfaces.ChildCommand;
import me.resurrectajax.ajaxplugin.interfaces.ParentCommand;
import persistency.MappingRepository;
import persistency.NationMapping;
import persistency.PlayerMapping;
import persistency.WarMapping;

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
		if(args.length < 1) return;
		
		NationMapping nation, enemy;
		PlayerMapping player = null;
		if(sender instanceof Player) player = mappingRepo.getPlayerByUUID(((Player)sender).getUniqueId());
		
		switch(args.length) {
			case 2:
				if(player == null || player.getNationID() == null) return;
				nation = mappingRepo.getNationByID(player.getNationID());
				enemy = mappingRepo.getNationByName(args[1]);
				break;
			case 3:
				nation = mappingRepo.getNationByName(args[1]);
				enemy = mappingRepo.getNationByName(args[2]);
				break;
			default:
				return;
		}
		if(nation == null || enemy == null) return;
		createInfo(sender, mappingRepo.getWarByNationIDs(nation.getNationID(), enemy.getNationID()));
	}
	
	private void createInfo(CommandSender sender, WarMapping war) {
		sender.sendMessage(GeneralMethods.padCenter("War", '-', 35));
		sender.sendMessage(String.format("Nations: %s, %s", war.getNation().getName(), war.getEnemy().getName()));
		sender.sendMessage(String.format("Goal: %dp", war.getKillpointGoal()));
		sender.sendMessage("Kill Points:");
		sender.sendMessage(String.format("  %s: %dp", war.getNation().getName(), war.getNation().countKillPoints()));
		sender.sendMessage(String.format("  %s: %dp", war.getEnemy().getName(), war.getEnemy().countKillPoints()));
		sender.sendMessage("Nationless/Member = 1p; Officer = 2p; Leader = 3p");
		sender.sendMessage(GeneralMethods.padCenter("", '-', 34));
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
