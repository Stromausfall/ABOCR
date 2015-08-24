package net.matthiasauer.abocr.map.unit.interaction;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.ashley.systems.IteratingSystem;

import net.matthiasauer.abocr.input.click.ClickedComponent;
import net.matthiasauer.abocr.map.unit.UnitComponent;

public class UnitInteractionSystem extends IteratingSystem {
	@SuppressWarnings("unchecked")
	private static final Family family =
			Family.all(
					ClickedComponent.class,
					UnitComponent.class).get();
	private ComponentMapper<UnitComponent> unitComponentMapper;
	private ComponentMapper<ClickedComponent> clickedComponentMapper;
	private PooledEngine engine;

	public UnitInteractionSystem() {
		super(family);
	}

	@Override
	public void addedToEngine(Engine engine) {
		this.engine = (PooledEngine) engine;
		this.unitComponentMapper =
				ComponentMapper.getFor(UnitComponent.class);
		this.clickedComponentMapper =
				ComponentMapper.getFor(ClickedComponent.class);
		
		super.addedToEngine(engine);
	}
	
	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		UnitComponent unitComponent =
				this.unitComponentMapper.get(entity);

		unitComponent.selected = !unitComponent.selected;
	}

}
