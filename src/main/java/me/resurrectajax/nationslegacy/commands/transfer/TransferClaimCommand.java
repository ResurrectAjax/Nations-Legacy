package me.resurrectajax.nationslegacy.commands.transfer;

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
import me.resurrectajax.nationslegacy.enumeration.Rank;
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
		FileConfiguration language = main.getLanguage();
		
		Player player = (Player) sender;
		PlayerMapping playerMap = mappingRepo.getPlayerByUUID(player.getUniqueId());
		NationMapping receivingNation = mappingRepo.getNationByName(arg);
		NationMapping nation = mappingRepo.getNationByPlayer(playerMap);

		if(receivingNation != null) {
			PlayerMapping pl = receivingNation.getAllMembers().stream().findFirst().orElse(null);
			super.setLastMentioned(main, sender, Bukkit.getOfflinePlayer(pl.getUUID()));
		}
		
		if(args.length != 3 || !GeneralMethods.isInteger(args[2])) player.sendMessage(GeneralMethods.getBadSyntaxMessage(main, getSyntax()));
		else if(nation == null) player.sendMessage(GeneralMethods.format((OfflinePlayer)sender, language.getString("Command.Player.NotInNation.Message"), args[1]));
		else if(!playerMap.getRank().equals(Rank.Leader)) sender.sendMessage(GeneralMethods.format((OfflinePlayer)sender, language.getString("Command.Player.NotALeader.Message"), nation.getName()));
		else if(receivingNation == null) player.sendMessage(GeneralMethods.format((OfflinePlayer)sender, language.getString("Command.Nations.NotExist.Message"), args[1]));
		else if(nation == receivingNation) player.sendMessage(GeneralMethods.format((OfflinePlayer)sender, language.getString("Command.Nations.Transfer.Self.Message"), args[1]));
		else {
			Integer transferAmount = GeneralMethods.getIntFromString(args[2])[0];
			if(transferAmount > nation.getMaxChunks()-nation.getBaseChunkLimit()) {
				String message = language.getString("Command.Nations.Transfer.NotEnough.Message");
				message = message.replace("%nations_transfer_amount%", String.format("%d", transferAmount));
				player.sendMessage(GeneralMethods.format((OfflinePlayer)sender, message, args[1]));
				return;
			}
			
			main.getServer().getPluginManager().callEvent(new TransferChunksEvent(nation, receivingNation, sender, transferAmount));
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
	public String[] getSubArguments(String[] args) {
		// TODO Auto-generated method stub
		return null;
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
		return "Transfer the chunks your nation gained over time to another nation";
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
