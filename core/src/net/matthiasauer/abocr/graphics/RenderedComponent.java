package net.matthiasauer.abocr.graphics;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Pool.Poolable;

public class RenderedComponent implements Component, Poolable {
	public final Rectangle renderedTarget = new Rectangle();
	
	public RenderedComponent set(float x, float y, float width, float height) {
		this.renderedTarget.x = x;
		this.renderedTarget.y = y;
		this.renderedTarget.width = width;
		this.renderedTarget.height = height;
		
		return this;
	}

	@Override
	public void reset() {
		this.set(0, 0, 0, 0);
	}
}
