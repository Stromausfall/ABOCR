package net.matthiasauer.abocr.map.unit.range;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool.Poolable;

public class TargetComponent implements Component, Poolable {
	public int range;
	public TargetType type;

	@Override
	public void reset() {
		this.set(3, null);
	}
	
	public TargetComponent set(int range, TargetType type) {
		this.range = range;
		this.type = type;
		
		return this;
	}
}
