package net.matthiasauer.abocr.map.unit.interaction.select;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

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
	private final ComponentMapper<UnitSelectionMovementOrigin> unitSelectionComponentMapper;
	private final AtlasRegion texture;
	private final List<Entity> toRemove;
	private Set<Entity> selectedEntities;
	private PooledEngine engine;
	
	public UnitSelectionMovementOriginRenderSystem() {
		super(family);
		
		this.toRemove =
				new LinkedList<Entity>();
		this.texture =
				TextureLoader.getInstance().getTexture("selection");
		this.unitComponentMapper =
				ComponentMapper.getFor(UnitComponent.class);
		this.unitSelectionComponentMapper =
				ComponentMapper.getFor(UnitSelectionMovementOrigin.class);
		this.selectedEntities =
				new HashSet<Entity>();
	}
	
	@Override
	public void addedToEngine(Engine engine) {
		super.addedToEngine(engine);
		
		this.engine = (PooledEngine) engine;
	}
	
	@Override
	public void update(float deltaTime) {
		this.toRemove.clear();
		
		for (Entity entity : this.selectedEntities) {
			UnitSelectionMovementOrigin component =
					this.unitSelectionComponentMapper.get(entity);
			
			if (component == null) {
				entity.remove(RenderComponent.class);
				this.toRemove.add(entity);
			}
		}
		
		this.selectedEntities.removeAll(this.toRemove);
		 
		super.update(deltaTime);
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		UnitComponent unitComponent =
				this.unitComponentMapper.get(entity);

		RenderComponent selectionOriginRenderComponent =
				this.engine.createComponent(RenderComponent.class).set(
						unitComponent.x,
						unitComponent.y,
						0,
						RenderPositionUnit.Tiles,
						this.texture,
						RenderLayer.UnitSelection);
		 
		unitComponent.selectedEntity.add(selectionOriginRenderComponent);
		unitComponent.selectedEntity.add(
				this.engine.createComponent(UnitSelectionMovementOriginRenderRemoveComponent.class));
		
		this.selectedEntities.add(entity);
	}
}
