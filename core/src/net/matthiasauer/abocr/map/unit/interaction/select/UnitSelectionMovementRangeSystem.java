package net.matthiasauer.abocr.map.unit.interaction.select;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.ashley.utils.ImmutableArray;

import net.matthiasauer.abocr.map.tile.TileComponent;
import net.matthiasauer.abocr.map.tile.TileFastAccessSystem;
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
	private TileFastAccessSystem tileFastAccessSystem;
	private ImmutableArray<Entity> selectedMovementTargetEntities;
	private final ComponentMapper<UnitComponent> unitComponentMapper;
	private final ComponentMapper<TileComponent> tileComponentMapper;
	private PooledEngine pooledEngine;

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
		this.tileFastAccessSystem =
				this.pooledEngine.getSystem(TileFastAccessSystem.class);
		
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
		Entity entity =
				this.tileFastAccessSystem.getTile(x, y);
		
		if (entity != null) {
			result.add(entity);
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
	
	private void getSurroundingTileEntities(int x, int y, int range) {
		Set<Entity> tiles =
				new HashSet<Entity>();
		Set<Entity> tiles2 =
				new HashSet<Entity>();
		this.addEntity(x, y, tiles);
		Entity originEntity = tiles.iterator().next();

		// for each range unit
		for (int i = 0; i < range; i++) {
			
			// we get the surrounding elements of all tiles we already have
			// therefore increasing the range by one !
			for (Entity entity : tiles) {
				TileComponent unitComponent =
						this.tileComponentMapper.get(entity);
				
				//tiles2.addAll(
				Collection<Entity> surrounding =
					this.getSurroundingTileEntities(
							unitComponent.x,
							unitComponent.y);
				
				for (Entity surroundingEntity : surrounding) {
					
					// if we don't know about it yet - then we know that the minimum
					// moves to reach this tile are 'i'
					if (!tiles.contains(surroundingEntity)
							&& !tiles2.contains(surroundingEntity)
							&& (surroundingEntity != originEntity)) {
						surroundingEntity.add(
								this.pooledEngine.createComponent(UnitSelectionMovementTarget.class)
									.set(i+1));
						
						tiles2.add(surroundingEntity);
					}
				}
			}
			
			tiles.addAll(tiles2);
			tiles2.clear();
		}
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		UnitComponent unitComponent =
				this.unitComponentMapper.get(entity);
		this.getSurroundingTileEntities(
				unitComponent.x,
				unitComponent.y,
				unitComponent.movement);
	}
}
