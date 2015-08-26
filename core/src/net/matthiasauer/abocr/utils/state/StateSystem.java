package net.matthiasauer.abocr.utils.state;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.ashley.systems.IteratingSystem;

public abstract class StateSystem<T extends StateActorComponent, S extends StateComponent, U extends StateComponent> extends IteratingSystem {
	protected PooledEngine pooledEngine;
	private final Class<S> createdComponentClazz;
	private final Class<U> triggerComponentClazz;
	
	@SuppressWarnings("unchecked")
	protected StateSystem(
			Class<T> stateActorComponentClazz,
			Class<S> createdComponentClazz,
			Class<U> triggerComponentClazz) {
		super(Family.all(stateActorComponentClazz).get());
		
		if (createdComponentClazz == null) {
			throw new NullPointerException("createdComponentClazz was null !");
		}
		if (triggerComponentClazz == null) {
			throw new NullPointerException("triggerComponentClazz was null !");
		}
		
		this.createdComponentClazz = createdComponentClazz;
		this.triggerComponentClazz = triggerComponentClazz;
	}
	
	@Override
	public final void addedToEngine(Engine engine) {
		this.pooledEngine = (PooledEngine) engine;
	}
	
	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		// first step is always remove the component the system produces !
		entity.remove(this.createdComponentClazz);
		
		// then check if there is the component that triggers this system 
		U triggerComponent =
				entity.getComponent(this.triggerComponentClazz);

		if (triggerComponent != null) {
			this.trigger(triggerComponent);
		}
	}
	
	protected abstract void trigger(U component);
}
