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
import net.matthiasauer.abocr.map.unit.UnitComponent;

public class UnitSelectionMovementOriginRenderSystem extends IteratingSystem { 
	@SuppressWarnings("unchecked")
	private static final Family family =
			Family.all(
					UnitComponent.class,
					UnitSelectionMovementOrigin.class).get();
	private final ComponentMapper<UnitComponent> unitComponentMapper;
	private final AtlasRegion texture;
	private List<Entity> selectedEntities;
	private PooledEngine engine;
	
	public UnitSelectionMovementOriginRenderSystem() {
		super(family);
		
		this.texture =
				TextureLoader.getInstance().getTexture("selection");
		this.unitComponentMapper =
				ComponentMapper.getFor(UnitComponent.class);
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
		UnitComponent unitComponent =
				this.unitComponentMapper.get(entity);
		Entity renderEntity =
				this.engine.createEntity();

		RenderComponent selectionOriginRenderComponent =
				this.engine.createComponent(RenderComponent.class).set(
						unitComponent.x,
						unitComponent.y,
						0,
						RenderPositionUnit.Tiles,
						this.texture,
						RenderLayer.UnitSelection);
		 
		renderEntity.add(selectionOriginRenderComponent);
		renderEntity.add(
				this.engine.createComponent(UnitSelectionMovementOriginRenderRemoveComponent.class));
		
		this.selectedEntities.add(renderEntity);
		this.engine.addEntity(renderEntity);
	}
}
