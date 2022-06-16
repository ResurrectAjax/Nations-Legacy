package gui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import general.GeneralMethods;
import main.Main;

public class GuiManager {
	private Main main;
	private FileConfiguration guiConfig;
	private List<String> MHF_Heads = new ArrayList<String>();
	private HashMap<UUID, Integer> selectedRaid = new HashMap<UUID, Integer>();
	private HashMap<UUID, Gui> currentGui = new HashMap<UUID, Gui>();
	
	public Gui getCurrentGui(UUID uuid) {
		return currentGui.get(uuid);
	}
	
	public void setCurrentGui(UUID uuid, Gui gui) {
		currentGui.put(uuid, gui);
	}

	public GuiManager(Main main) {
		this.main = main;
		
		MHF_Heads.addAll(Arrays.asList(
				"Back",
				"Next"
				));
		
	}
	
	public List<String> getMHFList() {
		return MHF_Heads;
	}
	
	public HashMap<UUID, Integer> getSelectedRaid() {
		return selectedRaid;
	}
	
	public Gui confirmGui(Player player, ItemStack item) {
		guiConfig = main.getGuiConfig();
		Gui gui = new Gui(main, player, guiConfig.getInt("Raid.RaidParty.Confirm.Rows"), GeneralMethods.format(guiConfig.getString("Raid.RaidParty.Confirm.GUIName")), null);
		Inventory inventory = gui.getInventory();
		
		inventory.setItem(4, item);
		gui.openInventory();
		
		return gui;
	}
	
	
	public Gui historySelectGui(Player player) {
		guiConfig = main.getGuiConfig();
		Gui gui = new Gui(main, player, guiConfig.getInt("Raid.RaidHistory.HistorySelect.Rows"), GeneralMethods.format(guiConfig.getString("Raid.RaidHistory.HistorySelect.GUIName")), null);
		gui.openInventory();
		
		return gui;
	}
	

}
