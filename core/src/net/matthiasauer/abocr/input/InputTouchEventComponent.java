package net.matthiasauer.abocr.input;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool.Poolable;

public class InputTouchEventComponent implements Component, Poolable {
	public Entity target;
	public InputTouchEventType inputType;
	public long timestamp;
	public final Vector2 projectedPosition = new Vector2();
	public final Vector2 unprojectedPosition = new Vector2();
	
	@Override
	public void reset() {
		this.target = null;
		this.inputType = null;
		this.timestamp = -1;
		this.unprojectedPosition.x = 0;
		this.unprojectedPosition.y = 0;
		this.projectedPosition.x = 0;
		this.projectedPosition.y = 0;
	}
}
