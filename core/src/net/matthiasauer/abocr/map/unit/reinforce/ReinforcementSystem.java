package net.matthiasauer.abocr.map.unit.reinforce;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;

import net.matthiasauer.abocr.map.unit.UnitComponent;
import net.matthiasauer.abocr.map.unit.UnitStrength;
import net.matthiasauer.abocr.utils.ILateInitialization;
import net.matthiasauer.abocr.utils.Mappers;
import net.matthiasauer.abocr.utils.Systems;

public class ReinforcementSystem extends IteratingSystem implements ILateInitialization {
	@SuppressWarnings("unchecked")
	private static final Family requestedReinforcementFamily =
			Family.all(RequestReinforcementComponent.class).get();
	private Systems systems;

	public ReinforcementSystem() {
		super(requestedReinforcementFamily);
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		RequestReinforcementComponent requestComponent =
				Mappers.requestReinforcementComponent.get(entity);
		Entity unitEntity =
				systems.unitFastAccess.getUnit(requestComponent.targetUnitPosition);
		UnitComponent unitComponent =
				Mappers.unitComponent.get(unitEntity);
		
		int currentCount = unitComponent.strength.count;
		int newCount = currentCount + requestComponent.reinforcements;
		
		unitComponent.strength = UnitStrength.get(newCount);
		
		entity.remove(RequestReinforcementComponent.class);
	}

	@Override
	public void lateInitialization(Systems systems) {
		this.systems = systems;
	}

}
