package net.matthiasauer.abocr.utils.interaction;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.PooledEngine;

public abstract class Interaction<T extends Component, S extends Component> extends EntitySystem {
	protected final Entity interactionEntity;
	protected PooledEngine pooledEngine;
	private final Class<T> createdComponentClazz;
	private final Class<S> triggerComponentClazz;
	
	protected Interaction(
			Entity interactionEntity,
			Class<T> createdComponentClazz,
			Class<S> triggerComponentClazz) {
		if (interactionEntity == null) {
			throw new NullPointerException("interactionEntity was null !");
		}
		if (createdComponentClazz == null) {
			throw new NullPointerException("createdComponentClazz was null !");
		}
		if (triggerComponentClazz == null) {
			throw new NullPointerException("triggerComponentClazz was null !");
		}
		
		this.interactionEntity = interactionEntity;
		this.createdComponentClazz = createdComponentClazz;
		this.triggerComponentClazz = triggerComponentClazz;
	}
	
	@Override
	public final void addedToEngine(Engine engine) {
		this.pooledEngine = (PooledEngine) engine;
	}
	
	@Override
	public final void update(float deltaTime) {
		// first step is always remove the component the system produces !
		this.interactionEntity.remove(this.createdComponentClazz);
		
		// then check if there is the component that triggers this system 
		S triggerComponent =
				this.interactionEntity.getComponent(this.triggerComponentClazz);
		
		if (triggerComponent != null) {
			this.trigger(triggerComponent);
		}
	}
	
	protected abstract void trigger(S component);
}
