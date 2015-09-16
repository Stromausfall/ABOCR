package net.matthiasauer.abocr.map.tile;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;

import net.matthiasauer.abocr.graphics.RenderComponent;
import net.matthiasauer.abocr.graphics.RenderLayer;
import net.matthiasauer.abocr.graphics.RenderPositionUnit;
import net.matthiasauer.abocr.graphics.texture.TextureLoader;
import net.matthiasauer.abocr.map.owner.MapElementOwnerComponent;
import net.matthiasauer.abocr.utils.Mappers;

public class TileOwnerRenderSystem extends IteratingSystem {
	@SuppressWarnings("unchecked")
	private static final Family tileFamily =
			Family.all(TileComponent.class).get();
	@SuppressWarnings("unchecked")
	private static final Family tileOwnerFamily =
			Family.all(TileOwnerComponent.class).get();
	private final AtlasRegion tileOwner;
	private ImmutableArray<Entity> tileOwnerEntities;
	private PooledEngine engine;
	
	public TileOwnerRenderSystem() {
		super(tileFamily);
		
		this.tileOwner = TextureLoader.getInstance().getTexture("tile_owner");
	}

	@Override
	public void addedToEngine(Engine engine) {
		super.addedToEngine(engine);
		
		this.engine = (PooledEngine) engine;
		
		this.tileOwnerEntities =
				this.engine.getEntitiesFor(tileOwnerFamily);
	}

	@Override
	public void update(float deltaTime) {
		super.update(deltaTime);

		// remove all the tile owners
		for (Entity entity : this.tileOwnerEntities) {
			this.engine.removeEntity(entity);
		}
	}
	
	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		TileComponent tileComponent =
				Mappers.tileComponent.get(entity);
		MapElementOwnerComponent mapElementOwner =
				Mappers.mapElementOwnerComponent.get(entity);
		RenderComponent renderComponent =
				this.engine.createComponent(RenderComponent.class).set(
						tileComponent.x,
						tileComponent.y,
						0,
						RenderPositionUnit.Tiles,
						this.tileOwner,
						RenderLayer.TileOwner,
						mapElementOwner.owner.color);
		Entity tileOwnerEntity = this.engine.createEntity();
		
		tileOwnerEntity.add(
				this.engine.createComponent(TileOwnerComponent.class));
		tileOwnerEntity.add(renderComponent);
		
		this.engine.addEntity(tileOwnerEntity);
	}
}
