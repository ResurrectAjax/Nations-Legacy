package commands.admin.reload;

import java.util.List;
import java.util.UUID;

import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;

import general.GeneralMethods;
import interfaces.ChildCommand;
import interfaces.ParentCommand;
import main.Main;

public class Reload extends ChildCommand{
	private ParentCommand parent;
	private Main main;
	public Reload(ParentCommand parent) {
		this.main = parent.getMain();
		this.parent = parent;
	}
	
	@Override
	public void perform(CommandSender sender, String[] args) {
		main.reload();
		
		FileConfiguration language = main.getLanguage();
		sender.sendMessage(GeneralMethods.format(sender, language.getString("Command.Reload.Message"), sender.getName()));
	}

	@Override
	public String[] getArguments(UUID uuid) {
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
		return "reload";
	}

	@Override
	public String getSyntax() {
		// TODO Auto-generated method stub
		return "/nations admin reload";
	}

	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		return "Reloads the configuration files";
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

}
