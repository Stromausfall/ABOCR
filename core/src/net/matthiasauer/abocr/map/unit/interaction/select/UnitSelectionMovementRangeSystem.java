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

import net.matthiasauer.abocr.map.owner.MapElementOwnerComponent;
import net.matthiasauer.abocr.map.owner.Owner;
import net.matthiasauer.abocr.map.tile.TileComponent;
import net.matthiasauer.abocr.map.tile.TileFastAccessSystem;
import net.matthiasauer.abocr.map.unit.UnitComponent;
import net.matthiasauer.abocr.map.unit.UnitFastAccessSystem;

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
	private UnitFastAccessSystem unitFastAccessSystem;
	private ImmutableArray<Entity> selectedMovementTargetEntities;
	private final ComponentMapper<UnitComponent> unitComponentMapper;
	private final ComponentMapper<TileComponent> tileComponentMapper;
	private final ComponentMapper<MapElementOwnerComponent> mapElementOwnerComponentMapper;
	private PooledEngine pooledEngine;

	public UnitSelectionMovementRangeSystem() {
		super(selectedMovementOriginFamily);
		
		this.unitComponentMapper =
				ComponentMapper.getFor(UnitComponent.class);
		this.tileComponentMapper =
				ComponentMapper.getFor(TileComponent.class);
		this.mapElementOwnerComponentMapper =
				ComponentMapper.getFor(MapElementOwnerComponent.class);
	}
	
	@Override
	public void addedToEngine(Engine engine) {
		this.pooledEngine = (PooledEngine) engine;
		this.selectedMovementTargetEntities =
				this.pooledEngine.getEntitiesFor(selectedMovementTargetFamily);
		this.tileFastAccessSystem =
				this.pooledEngine.getSystem(TileFastAccessSystem.class);
		this.unitFastAccessSystem =
				this.pooledEngine.getSystem(UnitFastAccessSystem.class);
		
		super.addedToEngine(engine);
	}
	
	@Override
	public void update(float deltaTime) {
		for (Entity entity : this.selectedMovementTargetEntities) {
			entity.remove(UnitSelectionMovementTarget.class);
		}
		
		super.update(deltaTime);
	}
	
	private void addTileEntity(int x, int y, Collection<Entity> result, Owner originEntityOwner) {
		Entity entity =
				this.tileFastAccessSystem.getTile(x, y);
		
		if (entity != null) {
			Entity unitAtTile =
					this.unitFastAccessSystem.getUnit(x, y);					
			
			if (unitAtTile == null) {
				// no unit on the tile
				result.add(entity);
			} else {
				// unit on the tile
				MapElementOwnerComponent mapElementOwner =
						this.mapElementOwnerComponentMapper.get(unitAtTile);
				
				if (mapElementOwner.owner != originEntityOwner) {
					// only add it if the tile is NOT owned by the player 
					// (for whom the range is drawn)
					result.add(entity);
				}
						
			}
		}
	}
	
	private Collection<Entity> getSurroundingTileEntities(int x, int y, Owner originEntityOwner) {
		Collection<Entity> entities =
				new ArrayList<Entity>();
		
		// display always !
		this.addTileEntity(x, y+1, entities, originEntityOwner);
		this.addTileEntity(x, y-1, entities, originEntityOwner);
		this.addTileEntity(x-1, y, entities, originEntityOwner);
		this.addTileEntity(x+1, y, entities, originEntityOwner);
		
		if ((y % 2) == 1) {
			this.addTileEntity(x+1, y+1, entities, originEntityOwner);
			this.addTileEntity(x+1, y-1, entities, originEntityOwner);
		} else {
			this.addTileEntity(x-1, y-1, entities, originEntityOwner);
			this.addTileEntity(x-1, y+1, entities, originEntityOwner);
		}
		
		return entities;
	}
	
	private void getSurroundingTileEntities(int x, int y, int range, Owner originEntityOwner) {
		Set<Entity> tiles =
				new HashSet<Entity>();
		Set<Entity> tiles2 =
				new HashSet<Entity>();
		tiles.add(this.tileFastAccessSystem.getTile(x, y));
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
							unitComponent.y,
							originEntityOwner);
				
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
		MapElementOwnerComponent originEntityMapElementOwnerComponent =
				this.mapElementOwnerComponentMapper.get(entity);
		Owner originEntityOwner =
				originEntityMapElementOwnerComponent.owner;
		
		this.getSurroundingTileEntities(
				unitComponent.x,
				unitComponent.y,
				unitComponent.movement,
				originEntityOwner);
	}
}