package net.matthiasauer.abocr.input.base.gestures;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool.Poolable;

public class InputGestureEventComponent implements Component, Poolable {
	public InputGestureEventType type;
	public float argument;
	
	@Override
	public void reset() {
		this.type = null;
		this.argument = 0;
	}
	
	public InputGestureEventComponent set(InputGestureEventType type, float argument) {
		this.argument = argument;
		this.type = type;
		
		return this;
	}
	
}
