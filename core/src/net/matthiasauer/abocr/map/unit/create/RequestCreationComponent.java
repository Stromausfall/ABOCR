package net.matthiasauer.abocr.map.unit.create;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool.Poolable;

public class RequestCreationComponent implements Component, Poolable {
	public final Vector2 targetUnitPosition = new Vector2(0, 0);
	public int reinforcements;

	public RequestCreationComponent set(int targetUnitX, int targetUnitY, int reinforcements) {
		this.targetUnitPosition.x = targetUnitX;
		this.targetUnitPosition.y = targetUnitY;
		this.reinforcements = reinforcements;
		
		return this;
	}
	
	@Override
	public void reset() {
		this.set(0, 0, 0);
	}
}
