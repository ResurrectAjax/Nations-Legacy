package events.nation;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import main.Main;

public class ReloadEvent extends Event{
	private static final HandlerList handlers = new HandlerList();
	protected boolean isCancelled = false;
	
	public ReloadEvent() {
		Main main = Main.getInstance();
		
		main.reload();
	}

	public HandlerList getHandlers() {
	    return handlers;
	}

	public static HandlerList getHandlerList() {
	    return handlers;
	}
}
