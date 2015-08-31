package net.matthiasauer.abocr.map.unit.interaction.select;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;

import net.matthiasauer.abocr.graphics.RenderComponent;
import net.matthiasauer.abocr.graphics.RenderLayer;
import net.matthiasauer.abocr.graphics.RenderPositionUnit;
import net.matthiasauer.abocr.graphics.texture.TextureLoader;
import net.matthiasauer.abocr.map.tile.TileComponent;

public class UnitSelectionMovementTargetRenderSystem extends IteratingSystem { 
	@SuppressWarnings("unchecked")
	private static final Family family =
			Family.all(
					TileComponent.class,
					UnitSelectionMovementTarget.class).get();
	private final ComponentMapper<TileComponent> tileComponentMapper;
	private final AtlasRegion texture;
	private List<Entity> selectedEntities;
	private PooledEngine engine;
	
	public UnitSelectionMovementTargetRenderSystem() {
		super(family);
		
		this.texture =
				TextureLoader.getInstance().getTexture("selection");
		this.tileComponentMapper =
				ComponentMapper.getFor(TileComponent.class);
		this.selectedEntities =
				new ArrayList<Entity>();
	}
	
	@Override
	public void addedToEngine(Engine engine) {
		super.addedToEngine(engine);
		
		this.engine = (PooledEngine) engine;
	}
	
	@Override
	public void update(float deltaTime) {
		for (Entity entity : this.selectedEntities) {
			entity.removeAll();
			this.engine.removeEntity(entity);
		}
		
		this.selectedEntities.clear();
		
		super.update(deltaTime);
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		TileComponent unitComponent =
				this.tileComponentMapper.get(entity);
		Entity renderTargetEntity =
				this.engine.createEntity();

		RenderComponent selectionTargetRenderComponent =
				this.engine.createComponent(RenderComponent.class).set(
						unitComponent.x,
						unitComponent.y,
						0,
						RenderPositionUnit.Tiles,
						this.texture,
						RenderLayer.UnitSelection);
		 
		renderTargetEntity.add(selectionTargetRenderComponent);
		renderTargetEntity.add(
				this.engine.createComponent(UnitSelectionMovementTargetRenderRemoveComponent.class));
		
		this.selectedEntities.add(renderTargetEntity);
		this.engine.addEntity(renderTargetEntity);
	}
}
