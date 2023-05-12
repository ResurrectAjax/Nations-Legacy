package me.resurrectajax.nationslegacy.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import me.resurrectajax.nationslegacy.main.Nations;

public class ReloadEvent extends Event{
	private static final HandlerList handlers = new HandlerList();
	protected boolean isCancelled = false;
	
	public ReloadEvent() {
		Nations main = Nations.getInstance();
		
		main.reload();
	}

	public HandlerList getHandlers() {
	    return handlers;
	}

	public static HandlerList getHandlerList() {
	    return handlers;
	}
}
