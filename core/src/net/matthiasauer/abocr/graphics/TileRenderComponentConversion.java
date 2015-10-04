package net.matthiasauer.abocr.graphics;

import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;

import net.matthiasauer.abocr.map.tile.TileRenderSystem;
import net.matthiasauer.ecstools.graphics.RenderComponent;
import net.matthiasauer.ecstools.graphics.RenderLayer;
import net.matthiasauer.ecstools.graphics.RenderPositionUnit;

public class TileRenderComponentConversion extends RenderComponent {
	private TileRenderComponentConversion() {
	}

	public static RenderComponent createText(
			PooledEngine engine,
			int tilePositionX,
			int tilePositionY,
			float rotation,
			RenderLayer layer,
			String textString,
			String textFont,
			Color tint) {
		RenderComponent component =
				engine.createComponent(RenderComponent.class);
		
		component.setText(
				translateX(tilePositionX, tilePositionY),
				translateY(tilePositionX, tilePositionY),
				rotation,
				RenderPositionUnit.Pixels,
				layer,
				textString,
				textFont,
				tint);
		
		return component;
	}
	
	public static RenderComponent createSprite(
			PooledEngine engine,
			int tilePositionX,
			int tilePositionY,
			float rotation,
			AtlasRegion texture,
			RenderLayer layer,
			Color tint) {
		RenderComponent component =
				engine.createComponent(RenderComponent.class);
		
		component.setSprite(
				translateX(tilePositionX, tilePositionY),
				translateY(tilePositionX, tilePositionY),
				rotation,
				RenderPositionUnit.Pixels,
				texture,
				layer,
				tint);
		
		return component;
	}
	
	private static float translateX(float x, float y) {
		float result = x * TileRenderSystem.TILE_SIZE;
		boolean shiftXCoordinate =
				(y % 2) == 1;

		if (shiftXCoordinate) {
			result += TileRenderSystem.TILE_SIZE / 2;
		}
		
		return result;
	}
	
	private static float translateY(float x, float y) {
		return y * TileRenderSystem.TILE_SIZE;
	}
}
