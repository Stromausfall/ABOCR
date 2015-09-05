package net.matthiasauer.abocr.map.unit.range;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
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
import net.matthiasauer.abocr.map.unit.interaction.select.UnitSelectionMovementOrigin;

public class BasicRangeSystem extends IteratingSystem {
	@SuppressWarnings("unchecked")
	private static final Family selectedUnits =
			Family.all(
					UnitSelectionMovementOrigin.class).get();
	@SuppressWarnings("unchecked")
	private static final Family targetTileFamily =
			Family.all(TargetComponent.class).get();
	private ImmutableArray<Entity> targetTileEntities;
	private final ComponentMapper<UnitComponent> unitComponentMapper;
	private TileFastAccessSystem tileFastAccessSystem;
	private PooledEngine pooledEngine;
	
	public BasicRangeSystem() {
		super(selectedUnits);
		
		this.unitComponentMapper =
				ComponentMapper.getFor(UnitComponent.class);
	}
	
	@Override
	public void addedToEngine(Engine engine) {
		this.pooledEngine = (PooledEngine) engine;
		this.tileFastAccessSystem =
				this.pooledEngine.getSystem(TileFastAccessSystem.class);
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
				this.unitComponentMapper.get(center);
		Entity centerTile =
				this.tileFastAccessSystem.getTile(unitComponent.x, unitComponent.y);
		
		// we start from the origin !
		Collection<Entity> currentRangeTiles =
				new LinkedList<Entity>(Arrays.asList(centerTile));
		Collection<Entity> total =
				new HashSet<Entity>(currentRangeTiles);
	
		for (int currentRange = 1; currentRange <= unitComponent.movement; currentRange++) {
			Set<Entity> copyOfAlreadyCoveredTiles =
					new HashSet<Entity>(currentRangeTiles);
			currentRangeTiles.clear();
			
			for (Entity tileEntity : copyOfAlreadyCoveredTiles) {
				currentRangeTiles.addAll(
						this.getNextRangeTiles(tileEntity, currentRange, total));
			}
		}
	}
	
	private Collection<Entity> getNextRangeTiles(Entity tileEntity, int currentRange, Collection<Entity> total) {
		Collection<Entity> nextRangeTiles =
				new LinkedList<Entity>();
		TileComponent tileComponent =
				this.tileFastAccessSystem.getTileComponent(tileEntity);
		
		Collection<Entity> surroundingTiles =
				this.tileFastAccessSystem.getSurroundingTiles(
						tileComponent.x,
						tileComponent.y);

		for (Entity surroundingEntity : surroundingTiles) {
			if (!total.contains(surroundingEntity)) {
				// if we haven't found the entity yet - it must be
				// exactly 'currentRange' from the centre
				surroundingEntity.add(
						this.pooledEngine.createComponent(TargetComponent.class)
								.set(currentRange, TargetType.Move));
				
				nextRangeTiles.add(surroundingEntity);
				total.add(surroundingEntity);
			}
		}
		
		return nextRangeTiles;
	}
}
