package net.matthiasauer.abocr.map.tile;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool.Poolable;

public class TileComponent implements Component, Poolable {
	public int x;
	public int y;
	public TileType tileType;
	public boolean receivesInput;
	
	@Override
	public void reset() {
		this.x = -1;
		this.y = -1;
		this.tileType = null;
		this.receivesInput = false;
	}
}
