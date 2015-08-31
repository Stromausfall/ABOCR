package net.matthiasauer.abocr.map.unit.interaction.select;

import java.util.ArrayList;
import java.util.Collection;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.ashley.utils.ImmutableArray;

import net.matthiasauer.abocr.map.tile.TileComponent;
import net.matthiasauer.abocr.map.unit.UnitComponent;

public class UnitSelectionMovementRangeSystem extends IteratingSystem {
	@SuppressWarnings("unchecked")
	private static final Family selectedMovementOriginFamily =
			Family.all(
					UnitSelectionMovementOrigin.class).get();
	@SuppressWarnings("unchecked")
	private static final Family selectedMovementTargetFamily =
			Family.all(
					UnitSelectionMovementTarget.class).get();
	@SuppressWarnings("unchecked")
	private static final Family tileComponentFamily =
			Family.all(
					TileComponent.class).get();
	private ImmutableArray<Entity> selectedMovementTargetEntities;
	private final ComponentMapper<UnitComponent> unitComponentMapper;
	private final ComponentMapper<TileComponent> tileComponentMapper;
	private PooledEngine pooledEngine;
	private ImmutableArray<Entity> tileEntities;

	public UnitSelectionMovementRangeSystem() {
		super(selectedMovementOriginFamily);
		
		this.unitComponentMapper =
				ComponentMapper.getFor(UnitComponent.class);
		this.tileComponentMapper =
				ComponentMapper.getFor(TileComponent.class);
	}
	
	@Override
	public void addedToEngine(Engine engine) {
		this.pooledEngine = (PooledEngine) engine;
		this.selectedMovementTargetEntities =
				this.pooledEngine.getEntitiesFor(selectedMovementTargetFamily);
		this.tileEntities =
				this.pooledEngine.getEntitiesFor(tileComponentFamily);
		
		super.addedToEngine(engine);
	}
	
	@Override
	public void update(float deltaTime) {
		for (Entity entity : this.selectedMovementTargetEntities) {
			entity.remove(UnitSelectionMovementTarget.class);
		}
		
		super.update(deltaTime);
	}
	
	private void addEntity(int x, int y, Collection<Entity> result) {
		for (Entity entity : this.tileEntities) {
			TileComponent tileComponent =
					this.tileComponentMapper.get(entity);
			
			if ((tileComponent.x == x) && (tileComponent.y == y)) {
				result.add(entity);
				break;
			}
		}
	}
	
	private Collection<Entity> getSurroundingTileEntities(int x, int y) {
		Collection<Entity> entities =
				new ArrayList<Entity>();
		
		// display always !
		this.addEntity(x, y+1, entities);
		this.addEntity(x, y-1, entities);
		this.addEntity(x-1, y, entities);
		this.addEntity(x+1, y, entities);
		
		if ((y % 2) == 1) {
			this.addEntity(x+1, y+1, entities);
			this.addEntity(x+1, y-1, entities);
		} else {
			this.addEntity(x-1, y-1, entities);
			this.addEntity(x-1, y+1, entities);
		}
		
		return entities;
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		UnitComponent unitComponent =
				this.unitComponentMapper.get(entity);
		
		for (Entity surroundingTileEntity : this.getSurroundingTileEntities(unitComponent.x, unitComponent.y)) {
			surroundingTileEntity.add(
					this.pooledEngine.createComponent(UnitSelectionMovementTarget.class));
		}
	}
}
