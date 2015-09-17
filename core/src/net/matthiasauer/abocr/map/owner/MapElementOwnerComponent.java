package net.matthiasauer.abocr.map.owner;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool.Poolable;

public class MapElementOwnerComponent implements Component, Poolable {
	public Owner owner;
	public boolean active;

	@Override
	public void reset() {
		this.set(Owner.Neutral, false);
	}

	public MapElementOwnerComponent set(Owner owner, boolean active) {
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
