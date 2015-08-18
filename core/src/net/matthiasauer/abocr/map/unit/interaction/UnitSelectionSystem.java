package net.matthiasauer.abocr.map.unit.interaction;

import java.util.Date;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.ashley.systems.IteratingSystem;

import net.matthiasauer.abocr.input.InputTouchEventComponent;
import net.matthiasauer.abocr.input.InputTouchEventType;

public class UnitSelectionSystem extends IteratingSystem {
	@SuppressWarnings("unchecked")
	private static final Family family =
			Family.all(InputTouchEventComponent.class).get();
	private PooledEngine pooledEngine;
	private ComponentMapper<UnitSelectableComponent> unitSelectableComponentMapper;
	private ComponentMapper<InputTouchEventComponent> inputTouchEventComponentComponentMapper;

	public UnitSelectionSystem() {
		super(family);
	}
	
	@Override
	public void addedToEngine(Engine engine) {
		super.addedToEngine(engine);
		
		this.pooledEngine = (PooledEngine) engine;
		this.unitSelectableComponentMapper =
				ComponentMapper.getFor(UnitSelectableComponent.class);
		this.inputTouchEventComponentComponentMapper =
				ComponentMapper.getFor(InputTouchEventComponent.class);
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		InputTouchEventComponent inputTouchEventComponent =
				this.inputTouchEventComponentComponentMapper.get(entity);
		Entity targetEntity =
				inputTouchEventComponent.target;
		
		if (targetEntity != null) {
			// if there is an entity 'under' the mouse

			UnitSelectableComponent unitSelectableComponent =
					this.unitSelectableComponentMapper.get(targetEntity);

					
			if (unitSelectableComponent != null) {
				// and it is selectable !
				
				if (inputTouchEventComponent.inputType == InputTouchEventType.TouchUp)

				System.err.println("oi !" + new Date() + " - " + targetEntity);
				
			}
			
		}
	}
}
