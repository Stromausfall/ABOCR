package net.matthiasauer.abocr.map.unit;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.math.Vector2;

public class UnitFastAccessSystem extends EntitySystem {
	@SuppressWarnings("unchecked")
	private static final Family tileComponentFamily =
			Family.all(UnitComponent.class).get();
	private ComponentMapper<UnitComponent> unitComponentMapper;
	private ImmutableArray<Entity> unitEntities;
	private PooledEngine pooledEngine;
	private final Map<Vector2, Entity> fastAccessUnits =
			new HashMap<Vector2, Entity>();
			
	
	@Override
	public void addedToEngine(Engine engine) {
		super.addedToEngine(engine);
		
		this.pooledEngine = (PooledEngine) engine;
		this.unitEntities =
				this.pooledEngine.getEntitiesFor(tileComponentFamily);
		this.unitComponentMapper =
				ComponentMapper.getFor(UnitComponent.class);
	}
	
	@Override
	public void update(float deltaTime) {
		super.update(deltaTime);
		
		this.fastAccessUnits.clear();
		
		for (Entity tileEntity : this.unitEntities) {
			UnitComponent unitComponent =
					this.unitComponentMapper.get(tileEntity);
			
			this.fastAccessUnits.put(
					new Vector2(
							unitComponent.x,
							unitComponent.y),
					tileEntity);
		}
	}
	
	public Entity getUnit(int x, int y) {
		Vector2 key =
				new Vector2(x, y);
		
		return this.fastAccessUnits.get(key);
	}
	
	public void removeUnit(Entity entity, PooledEngine pooledEngine) {
		UnitComponent unitComponent =
				this.unitComponentMapper.get(entity);
		
		this.fastAccessUnits.remove(
				new Vector2(unitComponent.x, unitComponent.y));
		
		this.pooledEngine.removeEntity(entity);
	}
}
