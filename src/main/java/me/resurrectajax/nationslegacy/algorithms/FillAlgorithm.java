package me.resurrectajax.nationslegacy.algorithms;

import java.awt.geom.Path2D;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Chunk;
import org.bukkit.World;

import me.resurrectajax.nationslegacy.main.Nations;
import me.resurrectajax.nationslegacy.persistency.MappingRepository;
import me.resurrectajax.nationslegacy.persistency.NationMapping;

/**
 * Algorithm class for filling an area with claimed chunks when forming a box-shape
 * */
public class FillAlgorithm {
	private MappingRepository mappingRepo;
	private NationMapping nation;
	private Chunk start;
	private World world;
	
	private int top;
	private int bottom;
	private int left;
	private int right;
	
	/**
	 * Constructor
	 * @param main - {@link Nations} this plugin
	 * @param nation - {@link NationMapping} claiming nation
	 * @param start - {@link Chunk} chunk to start from
	 * */
	public FillAlgorithm(Nations main, NationMapping nation, Chunk start) {
		this.mappingRepo = main.getMappingRepo();
		this.nation = nation;
		this.start = start;
		this.world = start.getWorld();
	}
	
	private List<Integer[]> getConnected(List<Integer[]> connected, int xcoord, int zcoord) {
		Integer[] coordinates = new Integer[] {xcoord, zcoord};
		
		Chunk chunk = world.getChunkAt(xcoord, zcoord);
		NationMapping chunkNation = mappingRepo.getNationByChunk(chunk);
		if((chunkNation != null && chunkNation.equals(nation) || chunk.equals(start)) && !connected.stream().anyMatch(el -> el[0] == xcoord && el[1] == zcoord)) {
			connected.add(coordinates);
			
			getConnected(connected, xcoord+1, zcoord);
			getConnected(connected, xcoord-1, zcoord);
			getConnected(connected, xcoord, zcoord+1);
			getConnected(connected, xcoord, zcoord-1);
		}
		
		return connected;
	}
	
	private Integer[] getSmallest(List<Integer[]> list) {
		Integer smallestx = null, smallestz = null;
		for(Integer[] coord : list) {
			if(smallestx == null || smallestx > coord[0]) smallestx = coord[0];
			if(smallestz == null || smallestz > coord[1]) smallestz = coord[1];
		}
		return new Integer[] {smallestx, smallestz};
	}
	private Integer[] getLargest(List<Integer[]> list) {
		Integer largestx = null, largestz = null;
		for(Integer[] coord : list) {
			if(largestx == null || largestx < coord[0]) largestx = coord[0];
			if(largestz == null || largestz < coord[1]) largestz = coord[1];
		}
		return new Integer[] {largestx, largestz};
	}
	
	/**
	 * Method to flood fill a square outline
	 * @return {@link List} of chunks to claim
	 * */
	public List<Chunk> fillSquareOutline() {
		
	    // Initialize variables for the bounding box
	    top = start.getX();
	    bottom = start.getX();
	    left = start.getZ();
	    right = start.getZ();

	    // Start the flood-fill process
	    List<Integer[]> connected = getConnected(new ArrayList<>(), start.getX(), start.getZ());
	    Integer[] smallest = getSmallest(connected), largest = getLargest(connected);
	    List<Integer[]> markings = floodFill(new ArrayList<>(), smallest, largest, start.getX(), start.getZ());
	    return markings.stream().map(el -> world.getChunkAt(el[0], el[1])).collect(Collectors.toList());
	}

	private List<Integer[]> floodFill(List<Integer[]> markings, Integer[] lowerbound, Integer[] upperbound, int row, int column) {
		
	    // Check if the current cell is within the grid bounds and is not an 'x'
		NationMapping chunkNation = mappingRepo.getNationByChunk(world.getChunkAt(row, column));
		if (chunkNation != null || markings.stream().anyMatch(el -> el[0] == row && el[1] == column) || row < lowerbound[0] || column < lowerbound[1] || row > upperbound[0] || column > upperbound[1]) {
	        return markings;
	    }
	    
	    // Update the boundaries of the square outline
	    this.top = Math.min(top, row);
	    this.bottom = Math.max(bottom, row);
	    this.left = Math.min(left, column);
	    this.right = Math.max(right, column);

	    // Mark the current cell as 'x'
	    markings.add(new Integer[] {row, column});
	    
	    int addRow = row + 1;
	    int subtractRow = row - 1;
	    int addCol = column + 1;
	    int subtractCol = column - 1;
	    floodFill(markings, lowerbound, upperbound, subtractRow, column); // Up
	    floodFill(markings, lowerbound, upperbound, addRow, column); // Down
	    floodFill(markings, lowerbound, upperbound, row, subtractCol); // Left
	    floodFill(markings, lowerbound, upperbound, row, addCol); // Right
	    floodFill(markings, lowerbound, upperbound, subtractRow, subtractCol); // UpLeft
	    floodFill(markings, lowerbound, upperbound, addRow, subtractCol); // DownLeft
	    floodFill(markings, lowerbound, upperbound, subtractRow, addCol); // UpRight
	    floodFill(markings, lowerbound, upperbound, addRow, addCol); // DownRight
	    
	    return markings;
	}
	
	
	public static boolean isPointInPolygon(double x, double y, int[] polygonX, int[] polygonY) {
        Path2D path = new Path2D.Double();
        path.moveTo(polygonX[0], polygonY[0]);

        for (int i = 1; i < polygonX.length; i++) {
            path.lineTo(polygonX[i], polygonY[i]);
        }
        path.closePath();

        return path.contains(x, y);
    }
	
	
}
