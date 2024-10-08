package me.resurrectajax.nationslegacy.commands.claim;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import me.resurrectajax.ajaxplugin.interfaces.ChildCommand;
import me.resurrectajax.ajaxplugin.interfaces.ParentCommand;
import me.resurrectajax.ajaxplugin.plugin.AjaxPlugin;
import me.resurrectajax.nationslegacy.commands.claim.validators.ClaimChunkValidator;
import me.resurrectajax.nationslegacy.events.nation.claim.SaveChunksEvent;
import me.resurrectajax.nationslegacy.general.GeneralMethods;
import me.resurrectajax.nationslegacy.main.Nations;
import me.resurrectajax.nationslegacy.persistency.MappingRepository;
import me.resurrectajax.nationslegacy.persistency.NationMapping;
import me.resurrectajax.nationslegacy.persistency.PlayerMapping;

public class ClaimChunkCommand extends ChildCommand{
	private ParentCommand parent;
	private Nations main;
	public ClaimChunkCommand(ParentCommand parent) {
		this.main = (Nations) parent.getMain();
		this.parent = parent;
	}
	@Override
	public String getPermissionNode() {
		// TODO Auto-generated method stub
		return "nations.player.claim";
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
		return main.getLanguage().getString("HelpList.Claim.Description");
	}
	@Override
	public boolean isConsole() {
		// TODO Auto-generated method stub
		return false;
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
		
		ClaimChunkValidator validator = new ClaimChunkValidator(sender, args, this);
		if(validator.validate()) {
			switch(args[1]) {
			case "on":
				if(mappingRepo.getClaimingSet().contains(player.getUniqueId())) return;
				mappingRepo.setIsClaiming(player.getUniqueId());
				sender.sendMessage(GeneralMethods.format((OfflinePlayer)sender, language.getString("Command.Nations.Claim.TurnedOn.Message"), args[1]));
				break;
			case "off":
				if(!mappingRepo.getClaimingSet().contains(player.getUniqueId())) return;
				mappingRepo.getClaimingSet().remove(player.getUniqueId());
				sender.sendMessage(GeneralMethods.format((OfflinePlayer)sender, language.getString("Command.Nations.Claim.TurnedOff.Message"), args[1]));
				Bukkit.getPluginManager().callEvent(new SaveChunksEvent(nation, sender));
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
	public AjaxPlugin getMain() {
		// TODO Auto-generated method stub
		return main;
	}

}
