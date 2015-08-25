package net.matthiasauer.abocr.utils.interaction;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.ashley.utils.ImmutableArray;

public abstract class InteractionBaseGenerator<T extends InteractionStopComponent, S extends InteractionStartComponent> extends EntitySystem {
	public final Entity interactionEntity;
	private final Class<T> interactionStopComponentClazz;
	private final Class<S> InteractionStartComponentClazz;
	private final ImmutableArray<Entity> interactionStopComponentEntities;
	private final PooledEngine engine;
	
	@SuppressWarnings("unchecked")
	protected InteractionBaseGenerator(
			Class<T> interactionStopComponentClazz,
			Class<S> InteractionStartComponentClazz,
			PooledEngine engine) {
		this.engine = engine;
		this.interactionStopComponentClazz = interactionStopComponentClazz;
		this.InteractionStartComponentClazz = InteractionStartComponentClazz;
		this.interactionStopComponentEntities =
				this.engine.getEntitiesFor(
						Family.all(this.interactionStopComponentClazz).get());

		// create the entity that connects all interaction !
		this.interactionEntity = this.engine.createEntity();
		this.engine.addEntity(this.interactionEntity);
	}
	
	@Override
	public final void update(float deltaTime) {
		// first remove the StartToken
		this.interactionEntity.remove(this.InteractionStartComponentClazz);
		
		if (this.interactionStopComponentEntities.size() == 0) {
			// and if there is no StopComponent !
			// then create a new StartComponent and add it to the interationEntity
			this.interactionEntity.add(
					this.createStartComponent(deltaTime));
		}
	}
	
	protected abstract S createStartComponent(float deltaTime);
}

