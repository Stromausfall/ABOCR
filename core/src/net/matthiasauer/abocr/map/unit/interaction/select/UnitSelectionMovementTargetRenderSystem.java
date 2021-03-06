package net.matthiasauer.abocr.map.unit.interaction.select;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.Color;

import net.matthiasauer.ecstools.graphics.RenderComponent;
import net.matthiasauer.ecstools.graphics.texture.TextureContainer;
import net.matthiasauer.abocr.graphics.RenderLayer;
import net.matthiasauer.abocr.graphics.TileRenderComponentConversion;
import net.matthiasauer.abocr.map.tile.TileComponent;
import net.matthiasauer.abocr.map.unit.range.TargetComponent;
import net.matthiasauer.abocr.map.unit.range.TargetType;
import net.matthiasauer.abocr.utils.Mappers;

public class UnitSelectionMovementTargetRenderSystem extends IteratingSystem { 
	@SuppressWarnings("unchecked")
	private static final Family family =
			Family.all(
					TileComponent.class,
					TargetComponent.class).get();
	private final TextureContainer<TargetType> textureContainer;
	private List<Entity> selectedEntities;
	private PooledEngine engine;
	
	public UnitSelectionMovementTargetRenderSystem() {
		super(family);
		
		this.textureContainer = 
				new TextureContainer<TargetType>();
		this.textureContainer.add(TargetType.Attack, "attackTarget");
		this.textureContainer.add(TargetType.Move, "moveTarget");
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
		TileComponent tileComponent =
				Mappers.tileComponent.get(entity);
		Entity renderTargetEntity =
				this.engine.createEntity();
		TargetComponent targetComponent =
				Mappers.targetComponent.get(entity);
		
		Color tint = null;
		
		// only for move
		if (targetComponent.inRange) {
			tint = null;
		} else {
			tint = Color.DARK_GRAY;
		}

		RenderComponent selectionTargetRenderComponent =
				TileRenderComponentConversion.createSprite(
						this.engine,
						tileComponent.x,
						tileComponent.y,
						0,
						this.textureContainer.get(targetComponent.type),
						RenderLayer.UnitSelection,
						tint);
		 
		renderTargetEntity.add(selectionTargetRenderComponent);
		
		this.selectedEntities.add(renderTargetEntity);
		this.engine.addEntity(renderTargetEntity);
	}
}
