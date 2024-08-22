package me.resurrectajax.nationslegacy.commands.transfer.validators;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import me.resurrectajax.ajaxplugin.interfaces.ParentCommand;
import me.resurrectajax.nationslegacy.general.CommandValidator;
import me.resurrectajax.nationslegacy.general.GeneralMethods;
import me.resurrectajax.nationslegacy.main.Nations;
import me.resurrectajax.nationslegacy.persistency.MappingRepository;
import me.resurrectajax.nationslegacy.persistency.NationMapping;
import me.resurrectajax.nationslegacy.persistency.PlayerMapping;
import me.resurrectajax.nationslegacy.ranking.Rank;

public class TransferClaimValidator extends CommandValidator {

	public TransferClaimValidator(CommandSender sender, String[] args, ParentCommand command) {
		super((Nations)command.getMain(), sender, args, command);
	}

	@Override
	public boolean validate() {
		String arg = args.length < 2 ? "" : args[1];
		
		MappingRepository mappingRepo = main.getMappingRepo();
		FileConfiguration language = main.getLanguage();
		
		Player player = (Player) sender;
		PlayerMapping playerMap = mappingRepo.getPlayerByUUID(player.getUniqueId());
		NationMapping receivingNation = mappingRepo.getNationByName(arg);
		NationMapping nation = mappingRepo.getNationByPlayer(playerMap);
		
		if(args.length != 3 || !GeneralMethods.isInteger(args[2])) player.sendMessage(GeneralMethods.getBadSyntaxMessage(main, command.getSyntax()));
		else if(nation == null) player.sendMessage(GeneralMethods.format((OfflinePlayer)sender, language.getString("Command.Player.NotInNation.Message"), args[1]));
		else if(!playerMap.getRank().equals(Rank.getHighest())) sender.sendMessage(GeneralMethods.format((OfflinePlayer)sender, language.getString("Command.Player.NotALeader.Message"), nation.getName()));
		else if(receivingNation == null) player.sendMessage(GeneralMethods.format((OfflinePlayer)sender, language.getString("Command.Nations.NotExist.Message"), args[1]));
		else if(nation == receivingNation) player.sendMessage(GeneralMethods.format((OfflinePlayer)sender, language.getString("Command.Nations.Transfer.Self.Message"), args[1]));
		else {
			Integer transferAmount = GeneralMethods.getIntFromString(args[2])[0];
			if(transferAmount > nation.getMaxChunks()-nation.getBaseChunkLimit()) {
				String message = language.getString("Command.Nations.Transfer.NotEnough.Message");
				message = message.replace("%nations_transfer_amount%", String.format("%d", transferAmount));
				player.sendMessage(GeneralMethods.format((OfflinePlayer)sender, message, args[1]));
				return false;
			}
			return true;
		}
		return false;
	}

}
