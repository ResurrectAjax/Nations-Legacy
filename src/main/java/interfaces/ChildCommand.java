package interfaces;

import java.util.UUID;

import org.bukkit.command.CommandSender;

import main.Main;

public abstract class ChildCommand extends ParentCommand{
	@Override
	public abstract void perform(CommandSender sender, String[] args);
	
	public void beforePerform(CommandSender sender, String arg) {
		Main.getInstance().getCommandManager().setLastArg(sender.getName(), arg);
	}
	
	@Override
	public abstract String[] getArguments(UUID uuid);
}
