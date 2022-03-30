package PlaceholderAPI;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import Main.Main;
import Persistency.MappingRepository;
import Persistency.NationMapping;
import Persistency.PlayerMapping;
import Persistency.WarMapping;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.clip.placeholderapi.expansion.Relational;

public class CustomRelationalPlaceholders extends PlaceholderExpansion implements Relational{

	@Override
	public String onPlaceholderRequest(Player one, Player two, String identifier) {
		Main main = Main.getInstance();
		MappingRepository mappingRepo = main.getMappingRepo();
		PlayerMapping playone = mappingRepo.getPlayerByUUID(one.getUniqueId()),
				playtwo = mappingRepo.getPlayerByUUID(two.getUniqueId());
		
		NationMapping nation, enemy;
		switch(identifier) {
		case "nation_name":
			nation = mappingRepo.getNationByPlayer(playtwo);
			return nation.getName();
		case "enemy_nation":
			nation = mappingRepo.getNationByPlayer(playone);
			enemy = mappingRepo.getNationByPlayer(playtwo);
			WarMapping war = mappingRepo.getWarByNationIDs(nation.getNationID(), enemy.getNationID());
			return war.getNation().getName();
		}
		return null;
	}

	@Override
	public @NotNull String getIdentifier() {
		// TODO Auto-generated method stub
		return "nations";
	}

	@Override
	public @NotNull String getAuthor() {
		// TODO Auto-generated method stub
		return "ResurrectAjax";
	}

	@Override
	public @NotNull String getVersion() {
		// TODO Auto-generated method stub
		return "1.0";
	}
	
	@Override
	public boolean canRegister() {
		return true;
	}

	@Override
	public boolean persist() {
		return true;
	}

}
