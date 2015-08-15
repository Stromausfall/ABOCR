 package net.matthiasauer.abocr.map.tile;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.math.Vector2;

import net.matthiasauer.abocr.TextureLoader;
import net.matthiasauer.abocr.graphics.RenderComponent;
import net.matthiasauer.abocr.graphics.RenderLayer;
import net.matthiasauer.abocr.input.InputTouchTargetComponent;

public class TileRenderSystem extends IteratingSystem {
	private static final int TILE_SIZE = 128;
	@SuppressWarnings("unchecked")
	private static final Family family =
			Family.all(TileComponent.class).get();
	private final ComponentMapper<TileComponent> tileComponentMapper; 

	private final AtlasRegion grass;
	private final AtlasRegion dirt;
	private final AtlasRegion sand;
	private PooledEngine engine;
	
	public TileRenderSystem() {
		super(family);

		this.grass = TextureLoader.getInstance().getTexture("tile_grass");
		this.dirt = TextureLoader.getInstance().getTexture("tile_dirt");
		this.sand = TextureLoader.getInstance().getTexture("tile_sand");

		this.tileComponentMapper =
				ComponentMapper.getFor(TileComponent.class);
	}
	
	@Override
	public void addedToEngine(Engine engine) {
		this.engine = (PooledEngine)engine;
		
		super.addedToEngine(this.engine);
	};

	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		TileComponent tileComponent = 
				this.tileComponentMapper.get(entity);
		AtlasRegion texture =
				this.getTexture(tileComponent);
		RenderComponent renderComponent =
				this.engine.createComponent(RenderComponent.class);
		
		this.updateTileCoordinates(
				tileComponent,
				renderComponent.position);
		this.updateInputTargetComponent(
				entity,
				renderComponent.position,
				tileComponent);
		
		renderComponent.layer = RenderLayer.Tiles;
		renderComponent.texture = texture;
		renderComponent.rotation = 0;
		renderComponent.absolutePosition = false;
		
		entity.add(renderComponent);
	}
	
	private AtlasRegion getTexture(TileComponent tileComponent) {
		switch (tileComponent.tileType) {
		case Dirt:
			return this.dirt;
		case Grass:
			return this.grass;
		case Sand:
			return this.sand;
		}
		
		throw new NullPointerException("no texture for TileType : " + tileComponent.tileType);
	}
	
	private void updateTileCoordinates(
			TileComponent tileComponent, Vector2 coordinates) {
		coordinates.x = tileComponent.x * TILE_SIZE;
		coordinates.y = tileComponent.y * TILE_SIZE;
		boolean shiftXCoordinate =
				(tileComponent.y % 2) == 1;

		if (shiftXCoordinate) {
			coordinates.x += TILE_SIZE / 2;
		}
	}
	
	private void updateInputTargetComponent(
			Entity tile, Vector2 coordinates, TileComponent tileComponent) {
		if (tileComponent.receivesInput) {
			InputTouchTargetComponent inputTargetComponent =
					this.engine.createComponent(InputTouchTargetComponent.class);
			inputTargetComponent.target.x = coordinates.x;
			inputTargetComponent.target.y = coordinates.y;
			inputTargetComponent.target.height = TILE_SIZE;
			inputTargetComponent.target.width = TILE_SIZE;
			
			tile.add(inputTargetComponent);
		}
	}
}
