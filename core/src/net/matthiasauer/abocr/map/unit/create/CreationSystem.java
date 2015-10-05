package net.matthiasauer.abocr.map.unit.create;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.ashley.systems.IteratingSystem;

import net.matthiasauer.abocr.map.player.MapElementOwnerComponent;
import net.matthiasauer.abocr.map.tile.TileComponent;
import net.matthiasauer.abocr.map.unit.UnitComponent;
import net.matthiasauer.abocr.map.unit.UnitStrength;
import net.matthiasauer.abocr.map.unit.UnitType;
import net.matthiasauer.abocr.utils.ILateInitialization;
import net.matthiasauer.abocr.utils.Mappers;
import net.matthiasauer.abocr.utils.Systems;

public class CreationSystem extends IteratingSystem implements ILateInitialization {
	@SuppressWarnings("unchecked")
	private static final Family requestedCreationFamily =
			Family.all(RequestCreationComponent.class).get();
	private Systems systems;
	private PooledEngine engine;

	public CreationSystem() {
		super(requestedCreationFamily);
	}
	
	@Override
	public void addedToEngine(Engine engine) {
		super.addedToEngine(engine);
		
		this.engine = (PooledEngine)engine;
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		RequestCreationComponent requestComponent =
				Mappers.requestCreationComponent.get(entity);
		Entity tileEntity =
				systems.tileFastAccess.getTile(requestComponent.targetUnitPosition);
		TileComponent tileComponent =
				Mappers.tileComponent.get(tileEntity);
		MapElementOwnerComponent tileOwnerComponent =
				Mappers.mapElementOwnerComponent.get(tileEntity);

		Entity unitEntity =
				this.engine.createEntity();
		UnitComponent unitComponent =
				this.engine.createComponent(UnitComponent.class).set(
						tileComponent.x,
						tileComponent.y,
						UnitStrength.get(requestComponent.reinforcements),
						UnitType.Infantry,
						0);
		MapElementOwnerComponent ownerComponent =
				this.engine.createComponent(MapElementOwnerComponent.class).set(tileOwnerComponent);

		unitEntity.add(unitComponent);			
		unitEntity.add(ownerComponent);
		this.engine.addEntity(unitEntity);
		
		entity.remove(RequestCreationComponent.class);
	}

	@Override
	public void lateInitialization(Systems systems) {
		this.systems = systems;
	}

}
