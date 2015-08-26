package net.matthiasauer.abocr.map.unit.interaction;

import java.util.Arrays;
import java.util.List;

import net.matthiasauer.abocr.utils.state.BaseStateGeneratorSystem;
import net.matthiasauer.abocr.utils.state.StateComponent;

public class BaseInteractionGeneratorSystem extends BaseStateGeneratorSystem<InteractionActorComponent, InteractionStartComponent> {
	private static final List<Class<? extends StateComponent>> stopComponents =
			Arrays.asList();
	
	public BaseInteractionGeneratorSystem() {
		super(InteractionActorComponent.class, stopComponents);
	}

	@Override
	protected InteractionStartComponent createStartComponent() {
		return new InteractionStartComponent();
	}
}
