package net.matthiasauer.abocr.map.unit.range;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool.Poolable;

public class TargetComponent implements Component, Poolable {
	public int range;
	public boolean inRange;
	public TargetType type;

	@Override
	public void reset() {
		this.set(3, false, null);
	}
	
	public TargetComponent set(int range, boolean inRange, TargetType type) {
		this.inRange = inRange;
		this.range = range;
		this.type = type;
		
		return this;
	}
}
