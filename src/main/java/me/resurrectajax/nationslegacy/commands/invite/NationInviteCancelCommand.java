package me.resurrectajax.nationslegacy.commands.invite;

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
import me.resurrectajax.nationslegacy.commands.invite.validators.NationInviteCancelValidator;
import me.resurrectajax.nationslegacy.general.GeneralMethods;
import me.resurrectajax.nationslegacy.main.Nations;
import me.resurrectajax.nationslegacy.persistency.MappingRepository;
import me.resurrectajax.nationslegacy.persistency.NationMapping;
import me.resurrectajax.nationslegacy.persistency.PlayerMapping;

public class NationInviteCancelCommand extends ChildCommand {

	private Nations main;
	private ParentCommand parent;

	public NationInviteCancelCommand(ParentCommand parent) {
		this.main = (Nations) parent.getMain();
		this.parent = parent;
	}

	@Override
	public void perform(CommandSender sender, String[] args) {
		FileConfiguration language = main.getLanguage();
		MappingRepository mappingRepo = main.getMappingRepo();

		PlayerMapping receiver = args.length < 2 ? null : mappingRepo.getPlayerByName(args[1]);
		PlayerMapping send = mappingRepo.getPlayerByUUID(((Player) sender).getUniqueId());
		
		Integer nationID = send.getNationID();
		NationMapping nation = mappingRepo.getNationByID(nationID);

		super.setLastArg(main, sender, args.length < 2 ? "" : args[1]);
		if(receiver != null) super.setLastMentioned(main, sender, Bukkit.getOfflinePlayer(receiver.getUUID()));

		NationInviteCancelValidator validator = new NationInviteCancelValidator(sender, args, this);
		if(validator.validate()) {
			mappingRepo.removePlayerInvite(nationID, receiver.getUUID());
			sender.sendMessage(GeneralMethods.format((OfflinePlayer)sender,
					language.getString("Command.Player.Invite.Cancel.CancelInvitation.Message"), nation.getName()));
		}
	}

	@Override
	public String[] getArguments(UUID uuid) {
		MappingRepository mappingRepo = main.getMappingRepo();
		NationMapping nation = mappingRepo.getNationByPlayer(mappingRepo.getPlayerByUUID(uuid));
		if (nation == null)
			return null;
		Set<String> args = mappingRepo.getPlayerInvites().keySet().stream()
				.filter(el -> mappingRepo.getPlayerInvites().get(el).contains(nation.getNationID()))
				.map(el -> el.toString()).collect(Collectors.toSet());
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
		return "cancel";
	}

	@Override
	public String getSyntax() {
		// TODO Auto-generated method stub
		return "/nations cancel <player>";
	}

	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		return "Cancel an invitation sent to a player";
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
	public AjaxPlugin getMain() {
		// TODO Auto-generated method stub
		return main;
	}

}
