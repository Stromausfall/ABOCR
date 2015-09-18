package net.matthiasauer.abocr.map.supply;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.ashley.utils.ImmutableArray;

import net.matthiasauer.abocr.map.unit.UnitComponent;
import net.matthiasauer.abocr.utils.Mappers;

public class SupplySystem extends IteratingSystem {
	@SuppressWarnings("unchecked")
	private static final Family unitFamily =
			Family.all(UnitComponent.class).get();
	@SuppressWarnings("unchecked")
	private static final Family nextTurnFamily =
			Family.all(NextTurnComponent.class).get();
	private ImmutableArray<Entity> unitEntities;

	public SupplySystem() {
		super(nextTurnFamily);
	}
	
	@Override
	public void addedToEngine(Engine engine) {
		super.addedToEngine(engine);
		
		this.unitEntities = engine.getEntitiesFor(unitFamily);
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		// this will only be triggered once there is a NextTurn element
		
		for (Entity unitEntity : this.unitEntities) {
			UnitComponent unit =
					Mappers.unitComponent.get(unitEntity);
			
			unit.movement = unit.type.maxMovement;
		}
	}
}
