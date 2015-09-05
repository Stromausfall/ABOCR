package net.matthiasauer.abocr.map.tile;

public enum TileType {
	Dirt(true),
	Grass(true),
	Sand(false);
	
	public boolean traversable;
	
	private TileType(boolean traversable) {
		this.traversable = traversable;
	}
}
