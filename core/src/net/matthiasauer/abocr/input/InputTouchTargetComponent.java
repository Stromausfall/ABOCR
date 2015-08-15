package net.matthiasauer.abocr.input;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Pool.Poolable;

public class InputTouchTargetComponent implements Component, Poolable {
	public final Rectangle target = new Rectangle();

	@Override
	public void reset() {
		this.target.height = 0;
		this.target.width = 0;
		this.target.x = 0;
		this.target.y = 0;
	}
}
