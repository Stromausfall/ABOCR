package net.matthiasauer.abocr.map.unit;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool.Poolable;

public class UnitComponent implements Component, Poolable {
	public int x;
	public int y;
	public boolean selected;
	public UnitStrength strength;
	public UnitType type;

	@Override
	public void reset() {
		this.type = null;
		this.strength = null;
		this.selected = false;
	}

}
