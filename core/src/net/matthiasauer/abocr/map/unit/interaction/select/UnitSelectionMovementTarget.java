package net.matthiasauer.abocr.map.unit.interaction.select;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool.Poolable;

public class UnitSelectionMovementTarget implements Component, Poolable {
	public int range;
	public UnitSelectionMovementTargetType type;

	@Override
	public void reset() {
		this.set(3, null);
	}
	
	public UnitSelectionMovementTarget set(int range, UnitSelectionMovementTargetType type) {
		this.range = range;
		this.type = type;
		
		return this;
	}
	
}
