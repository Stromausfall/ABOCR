 package net.matthiasauer.abocr.map.tile;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;

import net.matthiasauer.abocr.TextureContainer;
import net.matthiasauer.abocr.graphics.RenderComponent;
import net.matthiasauer.abocr.graphics.RenderLayer;
import net.matthiasauer.abocr.graphics.RenderPositionUnit;
import net.matthiasauer.abocr.input.InputTouchTargetComponent;

public class TileRenderSystem extends IteratingSystem {
	public static final int TILE_SIZE = 128;
	@SuppressWarnings("unchecked")
	private static final Family family =
			Family.all(TileComponent.class).get();
	private final ComponentMapper<TileComponent> tileComponentMapper; 
	private final TextureContainer<TileType> tileTextureContainer;
	private PooledEngine engine;
	
	public TileRenderSystem() {
		super(family);
		
		this.tileTextureContainer = new TextureContainer<TileType>();
		this.tileTextureContainer.add(TileType.Grass, "tile_grass");
		this.tileTextureContainer.add(TileType.Dirt, "tile_dirt");
		this.tileTextureContainer.add(TileType.Sand, "tile_sand");

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
				this.tileTextureContainer.get(tileComponent.tileType);
		RenderComponent renderComponent =
				this.engine.createComponent(RenderComponent.class).set(
						tileComponent.x,
						tileComponent.y,
						0,
						RenderPositionUnit.Tiles,
						texture,
						RenderLayer.Tiles);
		InputTouchTargetComponent inputTouchTargetComponent =
				this.engine.createComponent(InputTouchTargetComponent.class);
		
		entity.add(inputTouchTargetComponent);
		entity.add(renderComponent);
	}
}
