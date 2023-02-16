package events.nation.description;

import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;

import events.nation.NationEvent;
import main.Main;
import general.GeneralMethods;
import persistency.NationMapping;

public class SetNationDescriptionEvent extends NationEvent{
	private String description;
	
	public SetNationDescriptionEvent(NationMapping nation, CommandSender sender, String description) {
		super(nation, sender);
		setDescription(description);
		
		if(super.isCancelled) return;
		
		FileConfiguration language = Main.getInstance().getLanguage();
		
		if(description.isBlank()) sender.sendMessage(GeneralMethods.format(sender, language.getString("Command.Nations.Description.Remove.Message"), nation.getDescription()));
		else sender.sendMessage(GeneralMethods.format(sender, language.getString("Command.Nations.Description.Set.Message"), description));
			
		nation.setDescription(description);
		nation.update();
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}
