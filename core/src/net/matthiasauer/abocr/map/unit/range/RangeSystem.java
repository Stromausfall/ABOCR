package net.matthiasauer.abocr.map.unit.range;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.ashley.utils.ImmutableArray;

import net.matthiasauer.abocr.map.owner.MapElementOwnerComponent;
import net.matthiasauer.abocr.map.tile.TileComponent;
import net.matthiasauer.abocr.map.tile.TileFastAccessSystem;
import net.matthiasauer.abocr.map.unit.UnitComponent;
import net.matthiasauer.abocr.map.unit.UnitFastAccessSystem;
import net.matthiasauer.abocr.map.unit.interaction.select.UnitSelectionMovementOrigin;
import net.matthiasauer.abocr.utils.Mappers;

public class RangeSystem extends IteratingSystem {
	@SuppressWarnings("unchecked")
	private static final Family selectedUnits =
			Family.all(
					UnitSelectionMovementOrigin.class).get();
	@SuppressWarnings("unchecked")
	private static final Family targetTileFamily =
			Family.all(TargetComponent.class).get();
	private ImmutableArray<Entity> targetTileEntities;
	private TileFastAccessSystem tileFastAccessSystem;
	private UnitFastAccessSystem unitFastAccessSystem;
	private PooledEngine pooledEngine;
	
	public RangeSystem() {
		super(selectedUnits);
	}
	
	@Override
	public void addedToEngine(Engine engine) {
		this.pooledEngine = (PooledEngine) engine;
		this.tileFastAccessSystem =
				this.pooledEngine.getSystem(TileFastAccessSystem.class);
		this.unitFastAccessSystem =
				this.pooledEngine.getSystem(UnitFastAccessSystem.class);
		this.targetTileEntities =
				this.pooledEngine.getEntitiesFor(targetTileFamily);
		
		super.addedToEngine(this.pooledEngine);
	}
	
	@Override
	public void update(float deltaTime) {
		for (Entity entity : this.targetTileEntities) {
			entity.remove(TargetComponent.class);
		}
		
		super.update(deltaTime);
	}

	@Override
	protected void processEntity(final Entity center, float deltaTime) {
		UnitComponent unitComponent =
				Mappers.unitComponent.get(center);
		Entity centerTile =
				this.tileFastAccessSystem.getTile(unitComponent.x, unitComponent.y);
		
		// we start from the origin !
		Collection<Entity> total =
				new LinkedList<Entity>(Arrays.asList(centerTile));
	
		for (int currentRange = 1; currentRange <= unitComponent.movement; currentRange++) {
			Set<Entity> copyOfTotal =
					new HashSet<Entity>(total);
			
			for (Entity tileEntity : copyOfTotal) {
				this.getNextRangeTiles(center, tileEntity, currentRange, total);
			}
		}
	}
	
	private Collection<Entity> getSurroundingTiles(Entity tileEntity) {
		TileComponent tileComponent =
				this.tileFastAccessSystem.getTileComponent(tileEntity);
		TargetComponent centerTargetComponent =
				Mappers.targetComponent.get(tileEntity);
		
		if (centerTargetComponent != null) {
			if (centerTargetComponent.type != TargetType.Move) {
				// if the center is not traversable - then we can't reach any other field from it !
				// therefore no need to calculate the surrounding entities !
				return
						new LinkedList<Entity>();
			}
		}
		
		return
				this.tileFastAccessSystem.getSurroundingTiles(
						tileComponent.x,
						tileComponent.y);
	}
	
	private void getNextRangeTiles(
			Entity centerEntity, Entity tileEntity, int currentRange, Collection<Entity> total) {
		Collection<Entity> surroundingTiles =
				this.getSurroundingTiles(tileEntity);

		for (Entity surroundingEntity : surroundingTiles) {
			if (!total.contains(surroundingEntity)) {
				// if we haven't already calculate the entity !
				TargetType targetType =
						this.calculateTargetType(centerEntity, surroundingEntity);
				
				// if we haven't found the entity yet - it must be
				// exactly 'currentRange' from the centre
				surroundingEntity.add(
						this.pooledEngine.createComponent(TargetComponent.class)
								.set(currentRange, targetType));
				
				total.add(surroundingEntity);
			}
		}
	}
	
	private TargetType calculateTargetType(Entity centerEntity, Entity surroundingEntity) {
		TileComponent tileComponent =
				this.tileFastAccessSystem.getTileComponent(surroundingEntity);
		
		if (tileComponent.tileType.traversable) {
			// only add if the terrain is traversable !
			Entity unitAtTile =
					this.unitFastAccessSystem.getUnit(tileComponent.x, tileComponent.y);					
			
			if (unitAtTile == null) {
				// no unit on the tile
				return TargetType.Move;
			} else {				
				if (this.sameOwner(unitAtTile, centerEntity)) {
					return TargetType.NoMove;
				} else {
					// only add it if the tile is NOT owned by the player 
					// (for whom the range is drawn)
					return TargetType.Attack;
				}
			}
		} else {
			// not traversable
			return TargetType.NoMove;
		}
	}
	
	private boolean sameOwner(Entity locationA, Entity locationB) {
		// unit on the tile
		MapElementOwnerComponent locationAOwner =
				Mappers.mapElementOwnerComponent.get(locationA);
		MapElementOwnerComponent locationBOwner =
				Mappers.mapElementOwnerComponent.get(locationB);
		
		if (locationAOwner.owner != locationBOwner.owner) {
			return false;
		} else {
			return true;
		}
	}
}
