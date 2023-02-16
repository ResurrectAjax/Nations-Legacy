package events.nation;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import persistency.NationMapping;

public class NationEvent extends Event{

	private static final HandlerList handlers = new HandlerList();

	protected NationMapping nation;
	private CommandSender sender;
	protected boolean isCancelled = false;
	
	public NationEvent(NationMapping nation, CommandSender sender) {
		this.nation = nation;
		this.sender = sender;
	}
	
	public NationMapping getNation() {
		return nation;
	}
	
	public CommandSender getSender() {
		return sender;
	}
	
	public void setCancelled(boolean cancelled) {
		this.isCancelled = cancelled;
	}
	
	public boolean isCancelled() {
		return isCancelled;
	}

	public HandlerList getHandlers() {
	    return handlers;
	}

	public static HandlerList getHandlerList() {
	    return handlers;
	}

}
