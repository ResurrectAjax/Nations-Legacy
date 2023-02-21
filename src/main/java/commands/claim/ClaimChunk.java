package commands.claim;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import enumeration.Rank;
import general.GeneralMethods;
import main.Main;
import me.resurrectajax.ajaxplugin.interfaces.ChildCommand;
import me.resurrectajax.ajaxplugin.interfaces.ParentCommand;
import persistency.MappingRepository;
import persistency.NationMapping;
import persistency.PlayerMapping;

public class ClaimChunk extends ChildCommand{
	private ParentCommand parent;
	private Main main;
	public ClaimChunk(ParentCommand parent) {
		this.main = (Main) parent.getMain();
		this.parent = parent;
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
		return "claim";
	}
	@Override
	public String getSyntax() {
		// TODO Auto-generated method stub
		return "/nations claim <on | off>";
	}
	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		return "Start or stop claiming chunks for your nation";
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
				if(mappingRepo.getClaimingSet().contains(player.getUniqueId())) return;
				mappingRepo.setIsClaiming(player.getUniqueId());
				sender.sendMessage(GeneralMethods.format(sender, language.getString("Command.Nations.Claim.TurnedOn.Message"), args[1]));
				break;
			case "off":
				if(!mappingRepo.getClaimingSet().contains(player.getUniqueId())) return;
				mappingRepo.getClaimingSet().remove(player.getUniqueId());
				sender.sendMessage(GeneralMethods.format(sender, language.getString("Command.Nations.Claim.TurnedOff.Message"), args[1]));
				mappingRepo.getNationByPlayer(playerMap).saveChunks();
				break;
			}
		}
	}
	@Override
	public String[] getArguments(UUID uuid) {
		// TODO Auto-generated method stub
		return new String[] {"on", "off"};
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
