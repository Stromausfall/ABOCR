package net.matthiasauer.abocr.map.supply;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool.Poolable;

public class CityComponent implements Component, Poolable {
	public CityType type;
	public int x;
	public int y;
	
	public CityComponent set(CityType cityType, int x, int y) {
		this.x = x;
		this.y = y;
		this.type = cityType;
		
		return this;
	}
	
	@Override
	public void reset() {
		this.set(null, 0, 0);
	}
}
