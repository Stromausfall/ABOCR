package net.matthiasauer.abocr.map.unit.interaction.select;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;

import net.matthiasauer.abocr.graphics.RenderComponent;
import net.matthiasauer.abocr.graphics.RenderLayer;
import net.matthiasauer.abocr.graphics.RenderPositionUnit;
import net.matthiasauer.abocr.graphics.texture.TextureContainer;
import net.matthiasauer.abocr.graphics.texture.TextureLoader;
import net.matthiasauer.abocr.map.tile.TileComponent;

public class UnitSelectionMovementTargetRenderSystem extends IteratingSystem { 
	@SuppressWarnings("unchecked")
	private static final Family family =
			Family.all(
					TileComponent.class,
					UnitSelectionMovementTarget.class).get();
	private final ComponentMapper<TileComponent> tileComponentMapper;
	private final ComponentMapper<UnitSelectionMovementTarget> unitSelectionMovementTargetMapper;
	private final TextureContainer<UnitSelectionMovementTargetType> textureContainer;
	private List<Entity> selectedEntities;
	private PooledEngine engine;
	
	public UnitSelectionMovementTargetRenderSystem() {
		super(family);
		
		this.textureContainer = 
				new TextureContainer<UnitSelectionMovementTargetType>();
		this.textureContainer.add(UnitSelectionMovementTargetType.Attack, "attackTarget");
		this.textureContainer.add(UnitSelectionMovementTargetType.Move, "moveTarget");
		this.textureContainer.add(UnitSelectionMovementTargetType.NoMove, "noTarget");
		this.tileComponentMapper =
				ComponentMapper.getFor(TileComponent.class);
		this.unitSelectionMovementTargetMapper =
				ComponentMapper.getFor(UnitSelectionMovementTarget.class);
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
				this.tileComponentMapper.get(entity);
		Entity renderTargetEntity =
				this.engine.createEntity();
		UnitSelectionMovementTarget unitSelectionMovementTargetComponent =
				this.unitSelectionMovementTargetMapper.get(entity);
		
		Color tint = Color.RED;
		
		switch (unitSelectionMovementTargetComponent.range) {
		case 1:
			tint = null;
			break;
		case 2:
			tint = Color.LIGHT_GRAY;
			break;
		case 3:
			tint = Color.DARK_GRAY;
		default:
			break;
		}

		RenderComponent selectionTargetRenderComponent =
				this.engine.createComponent(RenderComponent.class).set(
						tileComponent.x,
						tileComponent.y,
						0,
						RenderPositionUnit.Tiles,
						this.textureContainer.get(unitSelectionMovementTargetComponent.type),
						RenderLayer.UnitSelection,
						tint);
		 
		renderTargetEntity.add(selectionTargetRenderComponent);
		renderTargetEntity.add(
				this.engine.createComponent(UnitSelectionMovementTargetRenderRemoveComponent.class));
		
		this.selectedEntities.add(renderTargetEntity);
		this.engine.addEntity(renderTargetEntity);
	}
}
