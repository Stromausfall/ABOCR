package net.matthiasauer.abocr.map.unit.interaction.select;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;

import net.matthiasauer.abocr.graphics.RenderComponent;

public class UnitSelectionMovementOriginRenderRemoveSystem extends IteratingSystem {
	@SuppressWarnings("unchecked")
	private static final Family family =
			Family.all(
					UnitSelectionMovementOriginRenderRemoveComponent.class).get();

	public UnitSelectionMovementOriginRenderRemoveSystem() {
		super(family);
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		entity.remove(RenderComponent.class);
		entity.remove(UnitSelectionMovementOriginRenderRemoveComponent.class);
	}
}
