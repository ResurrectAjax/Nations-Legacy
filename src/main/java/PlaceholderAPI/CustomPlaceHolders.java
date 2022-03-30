package PlaceholderAPI;

import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import Main.Main;
import Persistency.MappingRepository;
import Persistency.NationMapping;
import Persistency.PlayerMapping;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;

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
	public @Nullable String onRequest(OfflinePlayer player, String params) {
		Main main = Main.getInstance();
		MappingRepository mappingRepo = main.getMappingRepo();
		PlayerMapping play = mappingRepo.getPlayerByUUID(player.getUniqueId());
		
		
		switch(params) {
		case "nation_name":
			NationMapping nation = mappingRepo.getNationByPlayer(play);
			return nation.getName();
		case "player_rank":
			return play.getRank().toString();
		case "player_killpoints":
			return String.valueOf(play.getKillpoints());
		}
		return null;
	}
}
