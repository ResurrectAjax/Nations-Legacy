package me.resurrectajax.nationslegacy.general;

import org.bukkit.command.CommandSender;

import lombok.AllArgsConstructor;
import me.resurrectajax.ajaxplugin.interfaces.ParentCommand;
import me.resurrectajax.nationslegacy.main.Nations;

@AllArgsConstructor
public abstract class CommandValidator {
	protected Nations main;
	protected CommandSender sender;
	protected String[] args;
	protected ParentCommand command;
	public abstract boolean validate();
}
