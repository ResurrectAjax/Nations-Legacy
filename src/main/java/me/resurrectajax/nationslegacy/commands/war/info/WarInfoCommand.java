package me.resurrectajax.nationslegacy.commands.war.info;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import me.resurrectajax.ajaxplugin.interfaces.ChildCommand;
import me.resurrectajax.ajaxplugin.interfaces.ParentCommand;
import me.resurrectajax.ajaxplugin.interfaces.SubArgumentSource;
import me.resurrectajax.ajaxplugin.plugin.AjaxPlugin;
import me.resurrectajax.nationslegacy.commands.war.info.validators.WarInfoValidator;
import me.resurrectajax.nationslegacy.general.GeneralMethods;
import me.resurrectajax.nationslegacy.main.Nations;
import me.resurrectajax.nationslegacy.persistency.MappingRepository;
import me.resurrectajax.nationslegacy.persistency.NationMapping;
import me.resurrectajax.nationslegacy.persistency.PlayerMapping;
import me.resurrectajax.nationslegacy.persistency.WarMapping;
import me.resurrectajax.nationslegacy.ranking.Rank;

public class WarInfoCommand extends ChildCommand implements SubArgumentSource{

	private ParentCommand parent;
	private Nations main;
	private MappingRepository mappingRepo;
	public WarInfoCommand(ParentCommand parent) {
		this.parent = parent;
		this.main = (Nations) parent.getMain();
		
		this.mappingRepo = main.getMappingRepo();
	}
	
	@Override
	public void perform(CommandSender sender, String[] args) {
		NationMapping nation = null, enemy = null;
		PlayerMapping player = null;
		FileConfiguration language = main.getLanguage();
		if(sender instanceof Player) player = mappingRepo.getPlayerByUUID(((Player)sender).getUniqueId());
		
		switch(args.length) {
			case 3:
				if(!(sender instanceof Player)) {
					sender.sendMessage(GeneralMethods.format(sender, language.getString("Command.Error.ByConsole.Message"), args[2]));
					return;
				}
				
				super.setLastArg(main, sender, args[2]);
				if(player == null || player.getNationID() == null) return;
				nation = mappingRepo.getNationByID(player.getNationID());
				enemy = mappingRepo.getNationByName(args[2]);
				if(enemy != null) {
					PlayerMapping pl = enemy.getPlayers().stream().findFirst().orElse(null);
					super.setLastMentioned(main, sender, Bukkit.getOfflinePlayer(pl.getUUID()));
				}
				break;
			case 4:
				super.setLastArg(main, sender, args[3]);
				nation = mappingRepo.getNationByName(args[2]);
				enemy = mappingRepo.getNationByName(args[3]);
				if(nation != null) {
					PlayerMapping pl = nation.getPlayers().stream().findFirst().orElse(null);
					super.setLastMentioned(main, sender, Bukkit.getOfflinePlayer(pl.getUUID()));
				}
				break;
		}
		if(nation == null) super.setLastArg(main, sender, args[2]);
		if(enemy == null && args.length == 4) super.setLastArg(main, sender, args[3]);
		WarMapping war = mappingRepo.getWarByNationIDs(nation.getNationID(), enemy.getNationID());
		WarInfoValidator validator = new WarInfoValidator(sender, args, this);
		if(validator.validate()) createInfo(sender, war);
	}
	
	private void createInfo(CommandSender sender, WarMapping war) {
		sender.sendMessage(GeneralMethods.format("&a" + GeneralMethods.padCenter("War", '-', 35)));
		sender.sendMessage(GeneralMethods.format(String.format("&bNations: &c%s, %s", war.getNation().getName(), war.getEnemy().getName())));
		sender.sendMessage(GeneralMethods.format(String.format("&bGoal: &c%dp", war.getKillpointGoal())));
		sender.sendMessage(GeneralMethods.format("&bKill Points:"));
		sender.sendMessage(GeneralMethods.format(String.format("  &b%s: &c%dp", war.getNation().getName(), war.getNationKillpoints())));
		sender.sendMessage(GeneralMethods.format(String.format("  &b%s: &c%dp", war.getEnemy().getName(), war.getEnemyKillpoints())));
		;
		sender.sendMessage(GeneralMethods.format("&7" + Rank.getRanks().stream()
				.filter(el -> !el.equals(Rank.getNationless()))
				.map(el -> String.format("%s = %dp", el.toString(), el.getWorth()))
				.collect(Collectors.joining("; "))));
		sender.sendMessage(GeneralMethods.format("&a" + GeneralMethods.padCenter("", '-', 34)));
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
		if(args.length < 3) return null;
		NationMapping nation = mappingRepo.getNationByName(args[2]);
		if(nation == null) return null;
		
		Set<NationMapping> enemies = mappingRepo.getWarNationsByNationID(nation.getNationID());
		return enemies.stream().map(NationMapping::getName).toList().toArray(new String[enemies.size()]);
	}

	@Override
	public String getPermissionNode() {
		return "nations.player.war.info";
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
		return main.getLanguage().getString("HelpList.War.Info.Description");
	}

	@Override
	public ParentCommand getParentCommand() {
		return parent;
	}

	@Override
	public boolean isConsole() {
		return true;
	}

	@Override
	public AjaxPlugin getMain() {
		return main;
	}

}
