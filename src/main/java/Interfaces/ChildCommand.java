package Interfaces;

import java.util.UUID;

import org.bukkit.entity.Player;

public abstract class ChildCommand extends ParentCommand{
	@Override
	public abstract void perform(Player player, String[] args);
	
	@Override
	public abstract String[] getArguments(UUID uuid);
}
