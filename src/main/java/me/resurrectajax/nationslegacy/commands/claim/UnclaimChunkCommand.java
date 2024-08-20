package me.resurrectajax.nationslegacy.commands.claim;

import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import me.resurrectajax.ajaxplugin.interfaces.ChildCommand;
import me.resurrectajax.ajaxplugin.interfaces.ParentCommand;
import me.resurrectajax.ajaxplugin.plugin.AjaxPlugin;
import me.resurrectajax.nationslegacy.commands.claim.validators.UnclaimChunkValidator;
import me.resurrectajax.nationslegacy.events.nation.claim.SaveChunksEvent;
import me.resurrectajax.nationslegacy.events.nation.claim.UnclaimAllChunksEvent;
import me.resurrectajax.nationslegacy.general.GeneralMethods;
import me.resurrectajax.nationslegacy.main.Nations;
import me.resurrectajax.nationslegacy.persistency.MappingRepository;
import me.resurrectajax.nationslegacy.persistency.NationMapping;
import me.resurrectajax.nationslegacy.persistency.PlayerMapping;

public class UnclaimChunkCommand extends ChildCommand{

	private ParentCommand parent;
	private final Nations main;
	public UnclaimChunkCommand(ParentCommand parent) {
		this.main = (Nations) parent.getMain();
		this.parent = parent;
	}
	
	@Override
	public void perform(CommandSender sender, String[] args) {
		String arg = args.length < 2 ? "" : args[1];
		super.setLastArg(main, sender, arg);
		
		FileConfiguration language = main.getLanguage();
		
		Player player = (Player) sender;
		MappingRepository mappingRepo = main.getMappingRepo();
		PlayerMapping playerMap = mappingRepo.getPlayerByUUID(player.getUniqueId());
		NationMapping nation = mappingRepo.getNationByPlayer(playerMap);
		
		UnclaimChunkValidator validator = new UnclaimChunkValidator(sender, args, this);
		if(validator.validate()) {
			switch(args[1].toLowerCase()) {
			case "on":
				NationMapping chunkNation = mappingRepo.getNationByChunk(player.getLocation().getChunk());
				if(chunkNation == null || chunkNation.getNationID() != playerMap.getNationID()) {
					mappingRepo.getUnclaimingSet().remove(player.getUniqueId());
					player.sendMessage(GeneralMethods.format((OfflinePlayer) player, main.getLanguage().getString("Command.Nations.Unclaim.NotInClaim.Message"), player.getName()));
					return;
				}
				
				if(mappingRepo.getUnclaimingSet().contains(player.getUniqueId())) return;
				if(nation.getClaimedChunks().size() == 0) {
					sender.sendMessage(GeneralMethods.format(sender, language.getString("Command.Nations.Unclaim.NoChunks.Message"), nation.getName()));
					return;
				}
				mappingRepo.setIsUnclaiming(player.getUniqueId());
				sender.sendMessage(GeneralMethods.format((OfflinePlayer)sender, language.getString("Command.Nations.Unclaim.TurnedOn.Message"), args[1]));
				break;
			case "off":
				if(!mappingRepo.getUnclaimingSet().contains(player.getUniqueId())) return;
				mappingRepo.getUnclaimingSet().remove(player.getUniqueId());
				sender.sendMessage(GeneralMethods.format((OfflinePlayer)sender, language.getString("Command.Nations.Unclaim.TurnedOff.Message"), args[1]));
				Bukkit.getPluginManager().callEvent(new SaveChunksEvent(nation, sender));
				break;
			case "all":
				if(nation.getClaimedChunks().size() == 0) {
					sender.sendMessage(GeneralMethods.format((OfflinePlayer)sender, language.getString("Command.Nations.Unclaim.NoChunks.Message"), nation.getName()));
					return;
				}
				Bukkit.getPluginManager().callEvent(new UnclaimAllChunksEvent(nation, sender));
				break;
			}
		}
	}

	@Override
	public String[] getArguments(UUID uuid) {
		// TODO Auto-generated method stub
		return new String[] {"on","off", "all"};
	}

	@Override
	public String getPermissionNode() {
		// TODO Auto-generated method stub
		return "nations.player.unclaim";
	}

	@Override
	public boolean hasTabCompletion() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "unclaim";
	}

	@Override
	public String getSyntax() {
		// TODO Auto-generated method stub
		return "/nations unclaim <on | off | all>";
	}

	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		return main.getLanguage().getString("HelpList.Unclaim.Description");
	}

	@Override
	public List<ParentCommand> getSubCommands() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isConsole() {
		// TODO Auto-generated method stub
		return false;
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
