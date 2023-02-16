package commands.claim;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import enumeration.Rank;
import events.nation.claim.UnclaimAllChunksEvent;
import general.GeneralMethods;
import main.Main;
import me.resurrectajax.ajaxplugin.interfaces.ChildCommand;
import me.resurrectajax.ajaxplugin.interfaces.ParentCommand;
import persistency.MappingRepository;
import persistency.NationMapping;
import persistency.PlayerMapping;

public class UnclaimChunk extends ChildCommand{

	private ParentCommand parent;
	private final Main main;
	public UnclaimChunk(ParentCommand parent) {
		this.main = (Main) parent.getMain();
		this.parent = parent;
	}
	
	@Override
	public void perform(CommandSender sender, String[] args) {
		String arg = args.length < 2 ? "" : args[1];
		super.beforePerform(sender, arg);
		
		FileConfiguration language = main.getLanguage();
		
		Player player = (Player) sender;
		MappingRepository mappingRepo = main.getMappingRepo();
		PlayerMapping playerMap = mappingRepo.getPlayerByUUID(player.getUniqueId());
		NationMapping nation = mappingRepo.getNationByPlayer(playerMap);
		
		if(nation == null) sender.sendMessage(GeneralMethods.format(sender, language.getString("Command.Player.NotInNation.Message"), ""));
		else if(args.length < 2 || !Arrays.asList(getArguments(player.getUniqueId())).contains(arg.toLowerCase())) sender.sendMessage(GeneralMethods.getBadSyntaxMessage(getSyntax()));
		else if(!playerMap.getRank().equals(Rank.Leader) && !playerMap.getRank().equals(Rank.Officer)) sender.sendMessage(GeneralMethods.format(sender, language.getString("Command.Player.NotALeaderOrOfficer.Message"), nation.getName()));
		else {
			switch(args[1]) {
			case "on":
				if(mappingRepo.getUnclaimingSet().contains(player.getUniqueId())) return;
				if(nation.getClaimedChunks().size() == 0) {
					sender.sendMessage(GeneralMethods.format(sender, language.getString("Command.Nations.Unclaim.NoChunks.Message"), nation.getName()));
					return;
				}
				mappingRepo.setIsUnclaiming(player.getUniqueId());
				sender.sendMessage(GeneralMethods.format(sender, language.getString("Command.Nations.Unclaim.TurnedOn.Message"), args[1]));
				break;
			case "off":
				if(!mappingRepo.getUnclaimingSet().contains(player.getUniqueId())) return;
				mappingRepo.getUnclaimingSet().remove(player.getUniqueId());
				sender.sendMessage(GeneralMethods.format(sender, language.getString("Command.Nations.Unclaim.TurnedOff.Message"), args[1]));
				mappingRepo.getNationByPlayer(playerMap).saveChunks();
				break;
			case "all":
				if(nation.getClaimedChunks().size() == 0) {
					sender.sendMessage(GeneralMethods.format(sender, language.getString("Command.Nations.Unclaim.NoChunks.Message"), nation.getName()));
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
		return "Start or stop unclaiming chunks for your nation";
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
	public String[] getSubArguments() {
		// TODO Auto-generated method stub
		return null;
	}

}
