package net.matthiasauer.abocr.map.owner;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool.Poolable;

public class MapElementOwnerComponent implements Component, Poolable {
	public Owner owner;

	@Override
	public void reset() {
		this.set(Owner.Neutral);
	}

	public MapElementOwnerComponent set(Owner owner) {
		this.owner = owner;
		
		return this;
	}
}
