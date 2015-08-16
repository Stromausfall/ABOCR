package net.matthiasauer.abocr.graphics;

import net.matthiasauer.abocr.map.tile.TileRenderSystem;

/**
 * Looks cumbersome but primitives are better for the GarbageCollector than a Vector2 !
 */
public class RenderPositionUnitTranslator {
	public static float translateX(float x, float y, RenderPositionUnit positionUnit) {
		float result = x;
		
		if (positionUnit == RenderPositionUnit.Tiles) {
			result = x * TileRenderSystem.TILE_SIZE;
			boolean shiftXCoordinate =
					(y % 2) == 1;

			if (shiftXCoordinate) {
				result += TileRenderSystem.TILE_SIZE / 2;
			}
		}
		
		return result;
	}
	
	public static float translateY(float x, float y, RenderPositionUnit positionUnit) {
		float result = y;
		
		if (positionUnit == RenderPositionUnit.Tiles) {
			result = y * TileRenderSystem.TILE_SIZE;
		}
		
		return result;
	}
}
