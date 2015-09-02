package net.matthiasauer.abocr.map.unit.interaction.select;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool.Poolable;

public class UnitSelectionMovementTarget implements Component, Poolable {
	public int range;

	@Override
	public void reset() {
		this.set(3);
	}
	
	public UnitSelectionMovementTarget set(int range) {
		this.range = range;
		
		return this;
	}
	
}
