package net.matthiasauer.abocr.map.unit;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool.Poolable;

public class UnitComponent implements Component, Poolable {
	public int x;
	public int y;
	public UnitStrength strength;
	public UnitType type;
	public int movement;

	@Override
	public void reset() {
		this.x = 0;
		this.y = 0;
		this.type = null;
		this.strength = null;
		this.movement = 0;
	}

}
