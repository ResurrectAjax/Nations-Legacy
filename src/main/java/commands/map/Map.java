package commands.map;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.resurrectajax.ajaxplugin.interfaces.ChildCommand;
import me.resurrectajax.ajaxplugin.interfaces.ParentCommand;
import nationmaps.NationMap;

public class Map extends ChildCommand{

	private ParentCommand parent;
	public Map(ParentCommand parent) {
		this.parent = parent;
	}
	
	@Override
	public void perform(CommandSender sender, String[] args) {
		Player player = (Player) sender;
		
		ItemStack map = NationMap.getMap(player);
		ArrayList<ItemStack> inv = new ArrayList<ItemStack>(Arrays.asList(player.getInventory().getContents()));
		
		if(inv.stream().filter(el -> el != null).anyMatch(el -> el.getItemMeta().getDisplayName().equals(map.getItemMeta().getDisplayName())
																							&& el.getItemMeta().getLore() == map.getItemMeta().getLore())) {
			ItemStack element = inv.stream().filter(el -> el != null && el.getItemMeta().getDisplayName().equals(map.getItemMeta().getDisplayName()) && el.getItemMeta().getLore() == map.getItemMeta().getLore()).findFirst().orElse(null);
			Integer index = element == null ? null : inv.indexOf(element);
			
			if(index != null) player.getInventory().setItem(index, map);
		}
		else player.getInventory().addItem(map);
	}

	@Override
	public String[] getArguments(UUID uuid) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getPermissionNode() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean hasTabCompletion() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "map";
	}

	@Override
	public String getSyntax() {
		// TODO Auto-generated method stub
		return "/nations map";
	}

	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		return "Gives a nations map";
	}

	@Override
	public List<ParentCommand> getSubCommands() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isConsole() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public ParentCommand getParentCommand() {
		// TODO Auto-generated method stub
		return parent;
	}

	@Override
	public String[] getSubArguments() {
		// TODO Auto-generated method stub
		return null;
	}
	
}
