package events;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import persistency.NationMapping;

public class NationEvent extends Event{

	private static final HandlerList handlers = new HandlerList();

	private NationMapping nation;
	protected boolean isCancelled = false;
	
	public NationEvent(NationMapping nation) {
		this.nation = nation;
	}
	
	public NationMapping getNation() {
		return nation;
	}
	
	public void setCancelled(boolean cancelled) {
		this.isCancelled = cancelled;
	}
	
	public HandlerList getHandlers() {
	    return handlers;
	}

	public static HandlerList getHandlerList() {
	    return handlers;
	}

}
