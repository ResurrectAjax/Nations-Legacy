package me.resurrectajax.nationslegacy.nationmaps;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.map.MapView;

public class NationMap {
	public static ItemStack getMap(Player player) {
		MapView mapView = Bukkit.createMap(player.getWorld());
		
		ItemStack map = new ItemStack(Material.FILLED_MAP);
		applyToMap(mapView, map, player);
		
		MapMeta meta = (MapMeta) map.getItemMeta();
		meta.setMapView(mapView);
		meta.setDisplayName("Nation Map");
		map.setItemMeta(meta);
		return map;
	}
	
	
	private static void applyToMap(MapView map, ItemStack item, Player player) {
		if(map != null){
            map.getRenderers().clear();
    		map.setCenterX(player.getLocation().getBlockX());
    		map.setCenterZ(player.getLocation().getBlockZ());
    		map.setScale(MapView.Scale.CLOSEST);
    		
    		map.setTrackingPosition(true);
    		map.addRenderer(new NationRenderer());
        }
	}
}
