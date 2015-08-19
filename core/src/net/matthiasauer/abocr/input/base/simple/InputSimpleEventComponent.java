package net.matthiasauer.abocr.input.base.simple;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool.Poolable;

public class InputSimpleEventComponent implements Component, Poolable {
	public InputSimpleEventType inputType;
	public long timestamp;
	public int argument;
	
	@Override
	public void reset() {
		this.set(null, 0, 0);
	}
	
	public InputSimpleEventComponent set(InputSimpleEventType inputType, long timestamp, int argument) {
		this.inputType = inputType;
		this.timestamp = timestamp;
		this.argument = argument;
		
		return this;
	}
}
