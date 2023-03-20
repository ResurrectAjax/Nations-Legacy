package events.nation.war;

import org.bukkit.command.CommandSender;

import events.nation.NationEvent;
import persistency.NationMapping;

public class WarEvent extends NationEvent{

	protected NationMapping enemy;
	public WarEvent(NationMapping nation, NationMapping enemy, CommandSender sender) {
		super(nation, sender);
		this.enemy = enemy;
	}
	
	public NationMapping getEnemy() {
		return enemy;
	}

}
