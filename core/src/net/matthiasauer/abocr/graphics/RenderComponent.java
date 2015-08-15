package net.matthiasauer.abocr.graphics;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool.Poolable;

public class RenderComponent implements Component, Poolable {
	public final Vector2 position = new Vector2();
	public float rotation;
	public boolean absolutePosition;
	public AtlasRegion texture;
	public RenderLayer layer;
	
	public RenderComponent() {
		this.reset();
	}
	
	public RenderComponent set(
			float positionX,
			float positionY,
			float rotation,
			AtlasRegion texture,
			RenderLayer layer) {
		this.position.x = positionX;
		this.position.y = positionY;
		this.rotation = rotation;
		this.texture = texture;
		this.layer = layer;
		
		return this;
	}
	
	@Override
	public void reset() {
		this.set(
				0,
				0,
				0,
				null,
				null);
	}
}
