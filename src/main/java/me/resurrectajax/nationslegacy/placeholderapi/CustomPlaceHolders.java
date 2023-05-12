package me.resurrectajax.nationslegacy.placeholderapi;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.resurrectajax.nationslegacy.main.Nations;
import me.resurrectajax.nationslegacy.persistency.MappingRepository;
import me.resurrectajax.nationslegacy.persistency.NationMapping;
import me.resurrectajax.nationslegacy.persistency.PlayerMapping;

public class CustomPlaceHolders extends PlaceholderExpansion{
	
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

	@Override
	public @Nullable String onRequest(OfflinePlayer player, String identifier) {
		Nations main = Nations.getInstance();
		MappingRepository mappingRepo = main.getMappingRepo();
		PlayerMapping play = mappingRepo.getPlayerByUUID(player.getUniqueId()), play2;
		OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(play.getUUID());
		OfflinePlayer lastMentioned = main.getCommandManager().getLastMentioned(offlinePlayer.getName());
		
		NationMapping nation = mappingRepo.getNationByPlayer(play), nation2;
		switch(identifier) {
		case "player_name":
			return player.getName();
		case "player_argument":
			return main.getCommandManager().getLastArg(offlinePlayer.getName());
		case "player_rank":
			return play.getRank().toString();
		case "player_killpoints":
			return String.valueOf(play.getKillpoints());
		case "nation_name":
			if(nation != null) return nation.getName();
			return "";
		case "nation_description":
			if(nation != null) return nation.getDescription();
			return "";
		case "gained_chunks":
			if(nation == null) return "";
			int value = nation.getGainedChunks();
			return value + "";
		case "remaining_chunkamount":
			if(nation == null) return "";
			int number = nation.getMaxChunks()-nation.getClaimedChunks().size();
			return number + "";
		case "rel_nation_name":
			if(lastMentioned == null) return "";
			play2 = mappingRepo.getPlayerByUUID(lastMentioned.getUniqueId());
			nation2 = mappingRepo.getNationByID(play2.getNationID());
			return nation2.getName();
		case "rel_player_name":
			if(lastMentioned == null) return "";
			return lastMentioned.getName();
		case "rel_player_rank":
			if(lastMentioned == null) return "";
			play2 = mappingRepo.getPlayerByUUID(lastMentioned.getUniqueId());
			return play2.getRank().toString();
		}
		return null;
	}
}
