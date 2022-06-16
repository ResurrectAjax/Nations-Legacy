package placeholderapi;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import main.Main;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import persistency.MappingRepository;
import persistency.NationMapping;
import persistency.PlayerMapping;

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
		OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(play.getUUID());
		
		switch(params) {
		case "player_argument":
			return main.getCommandManager().getLastArg(offlinePlayer.getName());
		case "player_rank":
			return play.getRank().toString();
		case "player_killpoints":
			return String.valueOf(play.getKillpoints());
		case "nation_name":
			NationMapping nation = mappingRepo.getNationByPlayer(play);
			if(nation != null) return nation.getName();
			return "";
		}
		return null;
	}
}
