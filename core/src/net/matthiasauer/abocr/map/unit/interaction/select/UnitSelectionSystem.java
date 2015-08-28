package net.matthiasauer.abocr.map.unit.interaction.select;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.ashley.systems.IteratingSystem;

import net.matthiasauer.abocr.graphics.RenderComponent;
import net.matthiasauer.abocr.input.click.ClickedComponent;
import net.matthiasauer.abocr.map.unit.UnitComponent;

public class UnitSelectionSystem extends IteratingSystem {
	@SuppressWarnings("unchecked")
	private static final Family family =
			Family.all(
					UnitComponent.class,
					ClickedComponent.class).get();
	private final ComponentMapper<UnitComponent> unitComponentMapper;
	private PooledEngine pooledEngine;

	public UnitSelectionSystem() {
		super(family);
		
		this.unitComponentMapper =
				ComponentMapper.getFor(UnitComponent.class);
	}
	
	@Override
	public void addedToEngine(Engine engine) {
		this.pooledEngine = (PooledEngine) engine;
		
		super.addedToEngine(engine);
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		UnitComponent unitComponent =
				this.unitComponentMapper.get(entity);
		
		// switch between selected and not selected
		if (entity.getComponent(UnitSelectionMovementOrigin.class) == null) {
			entity.add(new UnitSelectionMovementOrigin());
		} else {
			entity.remove(UnitSelectionMovementOrigin.class);
			unitComponent.selectedEntity.remove(RenderComponent.class);
		}
	}
}
