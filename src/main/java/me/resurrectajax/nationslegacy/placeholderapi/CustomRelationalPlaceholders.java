package me.resurrectajax.nationslegacy.placeholderapi;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.clip.placeholderapi.expansion.Relational;
import me.resurrectajax.nationslegacy.main.Nations;
import me.resurrectajax.nationslegacy.persistency.MappingRepository;
import me.resurrectajax.nationslegacy.persistency.NationMapping;
import me.resurrectajax.nationslegacy.persistency.PlayerMapping;
import me.resurrectajax.nationslegacy.persistency.WarMapping;

public class CustomRelationalPlaceholders extends PlaceholderExpansion implements Relational{
	
	@Override
	public String onPlaceholderRequest(Player one, Player two, String identifier) {
		MappingRepository mappingRepo = Nations.getInstance().getMappingRepo();
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
		case "player_name":
			return two.getName();
		case "player_rank":
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
