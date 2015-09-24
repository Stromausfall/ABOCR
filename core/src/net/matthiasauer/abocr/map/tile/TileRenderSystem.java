 package net.matthiasauer.abocr.map.tile;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;

import net.matthiasauer.abocr.graphics.RenderComponent;
import net.matthiasauer.abocr.graphics.RenderLayer;
import net.matthiasauer.abocr.graphics.RenderPositionUnit;
import net.matthiasauer.abocr.graphics.texture.TextureContainer;
import net.matthiasauer.abocr.input.base.touch.InputTouchTargetComponent;
import net.matthiasauer.abocr.utils.Mappers;

public class TileRenderSystem extends IteratingSystem {
	public static final int TILE_SIZE = 128;
	@SuppressWarnings("unchecked")
	private static final Family family =
			Family.all(TileComponent.class).get();
	private final TextureContainer<TileType> tileTextureContainer;
	private PooledEngine engine;
	
	public TileRenderSystem() {
		super(family);
		
		this.tileTextureContainer = new TextureContainer<TileType>();
		this.tileTextureContainer.add(TileType.GRASS, "tile_grass");
		this.tileTextureContainer.add(TileType.DIRT, "tile_dirt");
		this.tileTextureContainer.add(TileType.MOUNTAIN, "tile_mountain");
	}
	
	@Override
	public void addedToEngine(Engine engine) {
		this.engine = (PooledEngine)engine;
		
		super.addedToEngine(this.engine);
	};

	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		TileComponent tileComponent =
				Mappers.tileComponent.get(entity);
		AtlasRegion texture =
				this.tileTextureContainer.get(tileComponent.tileType);
		RenderComponent renderComponent =
				this.engine.createComponent(RenderComponent.class).setSprite(
						tileComponent.x,
						tileComponent.y,
						0,
						RenderPositionUnit.Tiles,
						texture,
						RenderLayer.Tiles,
						null);
		InputTouchTargetComponent inputTouchTargetComponent =
				this.engine.createComponent(InputTouchTargetComponent.class);
		
		entity.add(inputTouchTargetComponent);
		entity.add(renderComponent);
	}
}
