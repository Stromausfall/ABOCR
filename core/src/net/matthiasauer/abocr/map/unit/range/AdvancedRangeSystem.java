package net.matthiasauer.abocr.map.unit.range;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.ashley.utils.ImmutableArray;

import net.matthiasauer.abocr.map.owner.MapElementOwnerComponent;
import net.matthiasauer.abocr.map.tile.TileComponent;
import net.matthiasauer.abocr.map.unit.UnitFastAccessSystem;
import net.matthiasauer.abocr.map.unit.interaction.select.UnitSelectionMovementOrigin;

public class AdvancedRangeSystem extends IteratingSystem {
	@SuppressWarnings("unchecked")
	private static final Family targetComponentFamily =
			Family.all(TargetComponent.class).get();
	@SuppressWarnings("unchecked")
	private static final Family unitSelectionMovementOriginFamily =
			Family.all(UnitSelectionMovementOrigin.class).get();
	private final ComponentMapper<TargetComponent> targetComponentMapper;
	
	private UnitFastAccessSystem unitFastAccessSystem;
	private ImmutableArray<Entity> unitSelectionMovementOriginEntities;
	private final ComponentMapper<TileComponent> tileComponentMapper;
	private final ComponentMapper<MapElementOwnerComponent> mapElementOwnerComponentMapper;
	private PooledEngine pooledEngine;

	public AdvancedRangeSystem() {
		super(targetComponentFamily);
		
		this.tileComponentMapper =
				ComponentMapper.getFor(TileComponent.class);
		this.mapElementOwnerComponentMapper =
				ComponentMapper.getFor(MapElementOwnerComponent.class);
		this.targetComponentMapper =
				ComponentMapper.getFor(TargetComponent.class);
	}
	
	@Override
	public void addedToEngine(Engine engine) {
		this.pooledEngine = (PooledEngine) engine;
		this.unitFastAccessSystem =
				this.pooledEngine.getSystem(UnitFastAccessSystem.class);
		this.unitSelectionMovementOriginEntities =
				this.pooledEngine.getEntitiesFor(unitSelectionMovementOriginFamily);
		
		super.addedToEngine(engine);
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		TargetComponent targetComponent =
				this.targetComponentMapper.get(entity);
		TileComponent tileComponent =
				this.tileComponentMapper.get(entity);
		Entity originEntity =
				this.unitSelectionMovementOriginEntities.first();
		MapElementOwnerComponent originEntityOwner =
				this.mapElementOwnerComponentMapper.get(originEntity);
		
		if (tileComponent.traversable) {
			// only add if the terrain is traversable !
			Entity unitAtTile =
					this.unitFastAccessSystem.getUnit(tileComponent.x, tileComponent.x);					
			
			if (unitAtTile == null) {
				// no unit on the tile
				targetComponent.type = TargetType.Move;
			} else {
				// unit on the tile
				MapElementOwnerComponent mapElementOwner =
						this.mapElementOwnerComponentMapper.get(unitAtTile);
				
				if (mapElementOwner.owner != originEntityOwner.owner) {
					// only add it if the tile is NOT owned by the player 
					// (for whom the range is drawn)
					targetComponent.type = TargetType.Attack;
				} else {
					targetComponent.type = TargetType.NoMove;
				}
						
			}
		} else {
			// not traversable
			targetComponent.type = TargetType.NoMove;
		}
	}
}
