package net.matthiasauer.abocr.map.tile;

public enum TileType {
	DIRT(true),
	GRASS(true),
	MOUNTAIN(false);
	
	public boolean traversable;
	
	private TileType(boolean traversable) {
		this.traversable = traversable;
	}
}
