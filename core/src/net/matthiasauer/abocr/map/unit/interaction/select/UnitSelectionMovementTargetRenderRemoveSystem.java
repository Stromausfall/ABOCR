package net.matthiasauer.abocr.map.unit.interaction.select;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;

import net.matthiasauer.abocr.graphics.RenderComponent;

public class UnitSelectionMovementTargetRenderRemoveSystem extends IteratingSystem {
	@SuppressWarnings("unchecked")
	private static final Family family =
			Family.all(
					UnitSelectionMovementTargetRenderRemoveComponent.class).get();

	public UnitSelectionMovementTargetRenderRemoveSystem() {
		super(family);
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		entity.remove(RenderComponent.class);
		entity.remove(UnitSelectionMovementTargetRenderRemoveComponent.class);
	}
}
