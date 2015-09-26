package net.matthiasauer.abocr.map.unit.interaction.select;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.ashley.utils.ImmutableArray;

import net.matthiasauer.abocr.input.click.ClickedComponent;
import net.matthiasauer.abocr.map.tile.TileComponent;
import net.matthiasauer.abocr.map.unit.UnitComponent;
import net.matthiasauer.abocr.map.unit.movement.MovementComponent;
import net.matthiasauer.abocr.map.unit.range.TargetComponent;
import net.matthiasauer.abocr.utils.Mappers;

public class UnitSelectionMovementSystem extends IteratingSystem {
	@SuppressWarnings("unchecked")
	private static final Family selectedEntitiesFamily =
			Family.all(
					UnitComponent.class,
					UnitSelectionMovementOriginComponent.class).get();
	@SuppressWarnings("unchecked")
	private static final Family clickedTilesFamily =
			Family.all(
					TileComponent.class,
					ClickedComponent.class,
					TargetComponent.class).get();
	
	private PooledEngine pooledEngine;
	private ImmutableArray<Entity> clickedTilesEntities;
	
	public UnitSelectionMovementSystem() {
		super(selectedEntitiesFamily);
	}
	
	@Override
	public void addedToEngine(Engine engine) {
		super.addedToEngine(engine);
		
		this.pooledEngine = (PooledEngine) engine;
		this.clickedTilesEntities =
				this.pooledEngine.getEntitiesFor(clickedTilesFamily);
	}

	@Override
	protected void processEntity(Entity attackerUnitEntity, float deltaTime) {
		if (this.clickedTilesEntities.size() > 1) {
			throw new NullPointerException("more than one clicked tile !");
		}
		
		if (this.clickedTilesEntities.size() == 1) {
			Entity defenderTileEntity = this.clickedTilesEntities.first();
			
			TargetComponent targetComponent =
					Mappers.targetComponent.get(defenderTileEntity);
			
			if (targetComponent.inRange) {
				MovementComponent movement =
						this.pooledEngine.createComponent(MovementComponent.class);
				movement.set(
						targetComponent.type,
						defenderTileEntity,
						attackerUnitEntity,
						targetComponent.range);
				
				attackerUnitEntity.add(movement);
			}
		}
	}
}
