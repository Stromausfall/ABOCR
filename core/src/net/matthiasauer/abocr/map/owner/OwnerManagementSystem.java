package net.matthiasauer.abocr.map.owner;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;

public class OwnerManagementSystem extends EntitySystem {
	@SuppressWarnings("unchecked")
	private static final Family activePlayerFamily =
			Family.all(ActivePlayerComponent.class).get();
	@SuppressWarnings("unchecked")
	private static final Family mapElementOwnerFamily =
			Family.all(MapElementOwnerComponent.class).get();
	private ImmutableArray<Entity> activePlayerEntities;
	private ImmutableArray<Entity> mapElementOwnerEntities;
	private final ComponentMapper<MapElementOwnerComponent> mapElementOwnerComponentMapper;
	private final ComponentMapper<ActivePlayerComponent> activePlayerComponentMapper;
	
	public OwnerManagementSystem() {
		this.mapElementOwnerComponentMapper =
				ComponentMapper.getFor(MapElementOwnerComponent.class);
		this.activePlayerComponentMapper =
				ComponentMapper.getFor(ActivePlayerComponent.class);
	}
	
	@Override
	public void addedToEngine(Engine engine) {
		this.activePlayerEntities =
				engine.getEntitiesFor(activePlayerFamily);
		this.mapElementOwnerEntities =
				engine.getEntitiesFor(mapElementOwnerFamily);
		
		super.addedToEngine(engine);
	}
	
	private Owner getOwner() {
		if (activePlayerEntities.size() != 1) {
			throw new NullPointerException(
					"There must only ever be exactly one active player ! But there were : "
					+	activePlayerEntities.size());
		}
		
		Entity entity = this.activePlayerEntities.first();
		ActivePlayerComponent activePlayerComponent =
				this.activePlayerComponentMapper.get(entity);
		
		return activePlayerComponent.owner;
	}
	
	@Override
	public void update(float deltaTime) {
		Owner activeOwner = this.getOwner();
		
		for (Entity entity : this.mapElementOwnerEntities) {
			MapElementOwnerComponent mapElementOwnerComponent =
					this.mapElementOwnerComponentMapper.get(entity);
			
			if (mapElementOwnerComponent.owner == activeOwner) {
				mapElementOwnerComponent.active = true;
			} else {
				mapElementOwnerComponent.active = false;
			}
		}
		
		super.update(deltaTime);
	}
}
