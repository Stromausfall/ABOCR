package net.matthiasauer.abocr.map.unit.interaction.select;

import com.badlogic.ashley.core.Entity;

import net.matthiasauer.abocr.utils.state.BaseStateGeneratorSystem;

public class BaseInteractionGeneratorSystem extends BaseStateGeneratorSystem<UnitInteractionActorComponent, UnitInteractionStartComponent> {
	public BaseInteractionGeneratorSystem() {
		super(
				UnitInteractionActorComponent.class,
				UnitInteractionStartComponent.class);
	}

	@Override
	protected UnitInteractionStartComponent createStartComponent(Entity entity, float deltaTime) {
		System.err.println("created !");
		return new UnitInteractionStartComponent();
	}
}
