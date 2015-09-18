package net.matthiasauer.abocr.map.player;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool.Poolable;

public class MapElementOwnerComponent implements Component, Poolable {
	public Player owner;
	public boolean active;

	@Override
	public void reset() {
		this.set(Player.Neutral, false);
	}

	public MapElementOwnerComponent set(Player owner, boolean active) {
		this.owner = owner;
		this.active = active;
		
		return this;
	}
	
	public MapElementOwnerComponent set(MapElementOwnerComponent toCopy) {
		return
				this.set(
						toCopy.owner,
						toCopy.active);
	}
}
