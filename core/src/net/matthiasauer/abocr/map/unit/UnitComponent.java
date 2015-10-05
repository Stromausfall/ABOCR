package net.matthiasauer.abocr.map.unit;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool.Poolable;

public class UnitComponent implements Component, Poolable {
	public int x;
	public int y;
	public UnitStrength strength;
	public UnitType type;
	public int movement;

	public UnitComponent set(
			int x,
			int y,
			UnitStrength strength,
			UnitType type,
			int movement) {
		this.x = x;
		this.y = y;
		this.type = type;
		this.strength = strength;
		this.movement = movement;
		
		return this;
	}
	
	@Override
	public void reset() {
		this.set(0, 0, null, null, 0);
	}

}
