package net.matthiasauer.abocr.utils.state;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.ashley.systems.IteratingSystem;

public abstract class BaseStateGeneratorSystem<StateActor extends StateActorComponent, BaseState extends StateComponent> extends IteratingSystem {
	protected PooledEngine engine;
	private final Class<BaseState> baseStateClazz;
	private final ComponentMapper<BaseState> componentMapper;

	@SuppressWarnings("unchecked")
	protected BaseStateGeneratorSystem(Class<StateActor> clazz, Class<BaseState> baseStateClazz) {
		super(Family.all(clazz).get());
		
		this.baseStateClazz = baseStateClazz;
		this.componentMapper =
				ComponentMapper.getFor(this.baseStateClazz);
	}
	
	@Override
	public void addedToEngine(Engine engine) {
		this.engine = (PooledEngine) engine;
		
		super.addedToEngine(engine);
	}
	
	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		BaseState baseStateComponent =
				this.componentMapper.get(entity);

		if (baseStateComponent == null) {
			System.err.println("!");
			entity.add(this.createStartComponent(entity, deltaTime));
		}
		
		if (baseStateComponent != null) {
			if ((baseStateComponent.state == StateEnum.Finished)
					|| (baseStateComponent.state == StateEnum.RequestFinished)
					|| (baseStateComponent.state ==  StateEnum.Unclaimed)) {
				entity.remove(this.baseStateClazz);
			}
		}
	}
	
	protected abstract BaseState createStartComponent(Entity entity, float deltaTime);
}
