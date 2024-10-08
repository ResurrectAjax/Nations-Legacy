package me.resurrectajax.nationslegacy.commands.war.truce;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import me.resurrectajax.ajaxplugin.interfaces.ChildCommand;
import me.resurrectajax.ajaxplugin.interfaces.ParentCommand;
import me.resurrectajax.ajaxplugin.plugin.AjaxPlugin;
import me.resurrectajax.nationslegacy.commands.war.WarCommand;
import me.resurrectajax.nationslegacy.commands.war.truce.validators.TruceCancelValidator;
import me.resurrectajax.nationslegacy.general.GeneralMethods;
import me.resurrectajax.nationslegacy.main.Nations;
import me.resurrectajax.nationslegacy.persistency.MappingRepository;
import me.resurrectajax.nationslegacy.persistency.NationMapping;
import me.resurrectajax.nationslegacy.persistency.PlayerMapping;

public class TruceCancelCommand extends ChildCommand{
	private Nations main;
	private WarCommand warCommand;
	
	public TruceCancelCommand(WarCommand warCommand) {
		this.main = (Nations) warCommand.getMain();
		this.warCommand = warCommand;
	}
	
	@Override
	public void perform(CommandSender sender, String[] args) {
		FileConfiguration language = main.getLanguage();
		MappingRepository mappingRepo = main.getMappingRepo();
		
		NationMapping senderNation = mappingRepo.getNationByPlayer(mappingRepo.getPlayerByUUID(((Player) sender).getUniqueId()));
		NationMapping nation = mappingRepo.getNationByName(args.length < 3 ? "" : args[2]);
		
		super.setLastArg(main, sender, args.length < 3 ? "" : args[2]);
		if(nation != null) {
			PlayerMapping pl = nation.getPlayers().stream().findFirst().orElse(null);
			super.setLastMentioned(main, sender, Bukkit.getOfflinePlayer(pl.getUUID()));
		}

		TruceCancelValidator validator = new TruceCancelValidator(sender, args, this);
		if(validator.validate()) {
			warCommand.removeTruceRequest(nation.getNationID(), senderNation.getNationID());
			sender.sendMessage(GeneralMethods.format((OfflinePlayer)sender, language.getString("Command.Nations.War.Truce.Send.CancelRequest.Message"), nation.getName()));
		}
	}

	@Override
	public String[] getArguments(UUID uuid) {
		MappingRepository mappingRepo = main.getMappingRepo();
		NationMapping nation = mappingRepo.getNationByPlayer(mappingRepo.getPlayerByUUID(uuid));
		if(nation == null) return null;
		Set<String> args = mappingRepo.getPlayerInvites().keySet().stream()
				.filter(el -> mappingRepo.getPlayerInvites().get(el).contains(nation.getNationID()))
				.map(el -> el.toString())
				.collect(Collectors.toSet());
		return args.toArray(new String[args.size()]);
	}

	@Override
	public String getPermissionNode() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean hasTabCompletion() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "cancelrequest";
	}

	@Override
	public String getSyntax() {
		// TODO Auto-generated method stub
		return "/nations ally cancelrequest <nation>";
	}

	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		return "Cancel an alliance request";
	}

	@Override
	public boolean isConsole() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public ParentCommand getParentCommand() {
		// TODO Auto-generated method stub
		return warCommand;
	}

	@Override
	public AjaxPlugin getMain() {
		// TODO Auto-generated method stub
		return main;
	}

}
