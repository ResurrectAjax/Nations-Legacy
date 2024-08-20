package me.resurrectajax.nationslegacy.commands.flag.info;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import me.resurrectajax.ajaxplugin.interfaces.ChildCommand;
import me.resurrectajax.ajaxplugin.interfaces.ParentCommand;
import me.resurrectajax.ajaxplugin.plugin.AjaxPlugin;
import me.resurrectajax.nationslegacy.commands.flag.info.validators.FlagInfoValidator;
import me.resurrectajax.nationslegacy.general.GeneralMethods;
import me.resurrectajax.nationslegacy.main.Nations;
import me.resurrectajax.nationslegacy.persistency.MappingRepository;
import me.resurrectajax.nationslegacy.persistency.NationMapping;
import me.resurrectajax.nationslegacy.persistency.PlayerMapping;

public class FlagInfoCommand extends ChildCommand{

	private Nations main;
	private ParentCommand parent;
	public FlagInfoCommand(ParentCommand parent) {
		this.parent = parent;
		this.main = (Nations) parent.getMain();
	}
	
	@Override
	public void perform(CommandSender sender, String[] args) {
		MappingRepository mappingRepo = main.getMappingRepo();
		
		super.setLastArg(main, sender, args.length < 3 ? "" : args[2]);
		
		FlagInfoValidator validator = new FlagInfoValidator(sender, args, this);
		if(validator.validate()) {
			if(args.length == 2) {
				NationMapping nationMap = mappingRepo
						.getNationByPlayer(mappingRepo
						.getPlayerByUUID(((OfflinePlayer) sender)
						.getUniqueId()));
				giveInfo(sender, nationMap.getName());
			}
			else giveInfo(sender, args[2]);
		}
	}
	
	private void giveInfo(CommandSender sender, String nation) {
		MappingRepository mappingRepo = main.getMappingRepo();
		NationMapping nationMap = mappingRepo.getNationByName(nation);
		if(nationMap != null) {
			PlayerMapping pl = nationMap.getPlayers().stream().findFirst().orElse(null);
			super.setLastMentioned(main, sender, Bukkit.getOfflinePlayer(pl.getUUID()));
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
		return "nations.player.flag.info";
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
		return main.getLanguage().getString("HelpList.Flag.Info.Description");
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
