package me.resurrectajax.nationslegacy.commands.transfer;

import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.resurrectajax.ajaxplugin.interfaces.ChildCommand;
import me.resurrectajax.ajaxplugin.interfaces.ParentCommand;
import me.resurrectajax.ajaxplugin.plugin.AjaxPlugin;
import me.resurrectajax.nationslegacy.commands.transfer.validators.TransferClaimValidator;
import me.resurrectajax.nationslegacy.events.nation.transfer.TransferChunksEvent;
import me.resurrectajax.nationslegacy.general.GeneralMethods;
import me.resurrectajax.nationslegacy.main.Nations;
import me.resurrectajax.nationslegacy.persistency.MappingRepository;
import me.resurrectajax.nationslegacy.persistency.NationMapping;
import me.resurrectajax.nationslegacy.persistency.PlayerMapping;

public class TransferClaimCommand extends ChildCommand{

	private ParentCommand parent;
	private Nations main;
	
	public TransferClaimCommand(ParentCommand parent) {
		this.parent = parent;
		this.main = (Nations) parent.getMain();
	}
	
	@Override
	public void perform(CommandSender sender, String[] args) {
		String arg = args.length < 2 ? "" : args[1];
		super.setLastArg(main, sender, arg);
		
		MappingRepository mappingRepo = main.getMappingRepo();
		
		Player player = (Player) sender;
		PlayerMapping playerMap = mappingRepo.getPlayerByUUID(player.getUniqueId());
		NationMapping receivingNation = mappingRepo.getNationByName(arg);
		NationMapping nation = mappingRepo.getNationByPlayer(playerMap);

		if(receivingNation != null) {
			PlayerMapping pl = receivingNation.getPlayers().stream().findFirst().orElse(null);
			super.setLastMentioned(main, sender, Bukkit.getOfflinePlayer(pl.getUUID()));
		}
		
		TransferClaimValidator validator = new TransferClaimValidator(sender, args, this);
		if(validator.validate()) {
			Integer transferAmount = GeneralMethods.getIntFromString(args[2])[0];
			Bukkit.getPluginManager().callEvent(new TransferChunksEvent(nation, receivingNation, sender, transferAmount));
		}
	}

	@Override
	public String[] getArguments(UUID uuid) {
		MappingRepository mappingRepo = main.getMappingRepo();
		PlayerMapping player = mappingRepo.getPlayerByUUID(uuid);
		
		List<String> nations = mappingRepo.getNations().stream()
				.filter(el -> el.getNationID() != player.getNationID())
				.map(el -> el.getName())
				.toList();
		return nations.toArray(new String[nations.size()]);
	}

	@Override
	public String getPermissionNode() {
		// TODO Auto-generated method stub
		return "nations.player.transfer";
	}

	@Override
	public boolean hasTabCompletion() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "transfer";
	}

	@Override
	public String getSyntax() {
		// TODO Auto-generated method stub
		return "/nations transfer <nation> <amount>";
	}

	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		return main.getLanguage().getString("HelpList.Transfer.Description");
	}

	@Override
	public boolean isConsole() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public AjaxPlugin getMain() {
		// TODO Auto-generated method stub
		return main;
	}

	@Override
	public ParentCommand getParentCommand() {
		// TODO Auto-generated method stub
		return parent;
	}

}
