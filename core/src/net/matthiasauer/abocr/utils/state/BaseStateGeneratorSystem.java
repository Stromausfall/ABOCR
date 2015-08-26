package net.matthiasauer.abocr.utils.state;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.ashley.systems.IteratingSystem;

public abstract class BaseStateGeneratorSystem<S extends StateActorComponent, T extends StateComponent> extends IteratingSystem {
	private final Set<Class<? extends StateComponent>> stopComponents;
	protected PooledEngine engine;

	@SuppressWarnings("unchecked")
	protected BaseStateGeneratorSystem(Class<S> stateActorClazz, List<Class<? extends StateComponent>> stopComponenets) {
		super(Family.all(stateActorClazz).get());
		
		this.stopComponents =
				new HashSet<Class<? extends StateComponent>>(stopComponenets);
	}
	
	@Override
	public void addedToEngine(Engine engine) {
		this.engine = (PooledEngine) engine;
		
		super.addedToEngine(engine);
	}
	
	private boolean entityContainsStopComponent(Entity entity) {
		for (Component component : entity.getComponents()) {
			Class<? extends Component> clazz = component.getClass();
			
			if (this.stopComponents.contains(clazz)) {
				return true;
			}
		}
		
		return false;
	}
	
	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		// first remove the StartToken
		entity.remove(StartStateComponent.class);

		if (!this.entityContainsStopComponent(entity)) {
			entity.add(this.createStartComponent());
		}
	}
	
	protected abstract T createStartComponent();
}
