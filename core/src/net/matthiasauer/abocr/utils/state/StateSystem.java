package net.matthiasauer.abocr.utils.state;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.ashley.systems.IteratingSystem;

public abstract class StateSystem<Created extends StateComponent, Trigger extends StateComponent> extends IteratingSystem {
	protected PooledEngine pooledEngine;
	private final Class<Created> createdComponentClazz;
	private final Class<Trigger> triggerComponentClazz;
	private final ComponentMapper<Created> createdComponentMapper;
	private final ComponentMapper<Trigger> triggerComponentMapper;
	
	@SuppressWarnings("unchecked")
	protected StateSystem(
			Class<Created> createdComponentClazz,
			Class<Trigger> triggerComponentClazz) {
		super(Family.one(createdComponentClazz, triggerComponentClazz).get());
		
		if (createdComponentClazz == null) {
			throw new NullPointerException("createdComponentClazz was null !");
		}
		if (triggerComponentClazz == null) {
			throw new NullPointerException("triggerComponentClazz was null !");
		}
		
		this.createdComponentClazz = createdComponentClazz;
		this.triggerComponentClazz = triggerComponentClazz;
		this.createdComponentMapper =
				ComponentMapper.getFor(this.createdComponentClazz);
		this.triggerComponentMapper =
				ComponentMapper.getFor(this.triggerComponentClazz);
	}
	
	@Override
	public void addedToEngine(Engine engine) {
		this.pooledEngine = (PooledEngine) engine;
		
		super.addedToEngine(engine);
	}
	
	private void handleCreated(Trigger triggerComponent, Created createdComponent, Entity entity, float deltaTime) {
		if (createdComponent.state == StateEnum.RequestFinished) {
			// right now flatout agree to the request 
			//TODO: maybe add some conditional logic here ?
			createdComponent.state = StateEnum.Finished;
		}
		
		if (createdComponent.state == StateEnum.Unclaimed) {
			// remove the unclaimed created component
			entity.remove(this.createdComponentClazz); 
		}
		
		if (createdComponent.state == StateEnum.Finished) {
			// remove the finished created component
			entity.remove(this.createdComponentClazz);
			
			// and forward the remove request to the trigger component
			triggerComponent.state = StateEnum.RequestFinished;
		}
		
		if (createdComponent.state == StateEnum.Preparing) {
			// continue to prepare the component
			this.prepare(createdComponent, entity, deltaTime);			
		}
	}
	
	protected abstract void prepare(Created createdComponent, Entity entity, float deltaTime);

	/**
	 * If the trigger component is still unclaimed then check if the StateSystem implementation
	 * creates a component. If so, then claim the trigger component and add the newly created
	 * component to the entity !
	 * @param triggerComponent
	 * @param entity
	 * @param deltaTime
	 */
	private void handleTrigger(Trigger triggerComponent, Entity entity, float deltaTime) {
		if (triggerComponent.state == StateEnum.Unclaimed) {			
			// create the component
			Created created =
					this.createComponent(triggerComponent, entity, deltaTime);
			
			if (created != null) {
				// only if a component was created !				
				entity.add(created);
			
				// claim the trigger component !
				triggerComponent.claimant = this;
				triggerComponent.state = StateEnum.Claimed;
				
				if ((created.state == StateEnum.Claimed)
						|| (created.state == StateEnum.Finished)) {
					throw new NullPointerException(
							"A newly created component must be in state '" + StateEnum.Preparing
								+ "' or '" + StateEnum.Unclaimed + "' !");
				}
			}
		}
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		Created createdComponent =
				this.createdComponentMapper.get(entity);
		Trigger triggerComponent =
				this.triggerComponentMapper.get(entity);
		
		// there is a trigger but no created component !
		if ((triggerComponent != null) && (createdComponent == null)) {
			this.handleTrigger(triggerComponent, entity, deltaTime);
		}
		
		// there is a created component !
		if (createdComponent != null) {
			this.handleCreated(triggerComponent, createdComponent, entity, deltaTime);
		}
	}
	
	protected abstract Created createComponent(Trigger triggerComponent, Entity entity, float deltaTime);
}
