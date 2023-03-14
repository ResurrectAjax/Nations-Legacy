package placeholderapi;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import main.Main;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.clip.placeholderapi.expansion.Relational;
import persistency.MappingRepository;
import persistency.NationMapping;
import persistency.PlayerMapping;
import persistency.WarMapping;

public class CustomRelationalPlaceholders extends PlaceholderExpansion implements Relational{
	
	@Override
	public String onPlaceholderRequest(Player one, Player two, String identifier) {
		MappingRepository mappingRepo = Main.getInstance().getMappingRepo();
		PlayerMapping playone = mappingRepo.getPlayerByUUID(one.getUniqueId()),
				playtwo = mappingRepo.getPlayerByUUID(two.getUniqueId());
		
		NationMapping nation, enemy;
		switch(identifier) {
		case "nation1_name":
			nation = mappingRepo.getNationByPlayer(playone);
			return nation.getName();
		case "nation2_name":
			nation = mappingRepo.getNationByPlayer(playtwo);
			return nation.getName();
		case "enemy_nation":
			nation = mappingRepo.getNationByPlayer(playone);
			enemy = mappingRepo.getNationByPlayer(playtwo);
			WarMapping war = mappingRepo.getWarByNationIDs(nation.getNationID(), enemy.getNationID());
			return war.getNation().getName();
		case "player2_rank":
			return playtwo.getRank().toString();
		}
		return null;
	}

	@Override
	public @NotNull String getIdentifier() {
		// TODO Auto-generated method stub
		return "relnations";
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
