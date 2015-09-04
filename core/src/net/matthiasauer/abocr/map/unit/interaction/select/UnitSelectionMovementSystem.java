package net.matthiasauer.abocr.map.unit.interaction.select;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.ashley.utils.ImmutableArray;

import net.matthiasauer.abocr.input.click.ClickedComponent;
import net.matthiasauer.abocr.map.tile.TileComponent;
import net.matthiasauer.abocr.map.unit.UnitComponent;

public class UnitSelectionMovementSystem extends IteratingSystem {
	@SuppressWarnings("unchecked")
	private static final Family selectedEntitiesFamily =
			Family.all(
					UnitComponent.class,
					UnitSelectionMovementOrigin.class).get();
	

	@SuppressWarnings("unchecked")
	private static final Family clickedTilesFamily =
			Family.all(
					TileComponent.class,
					ClickedComponent.class,
					UnitSelectionMovementTarget.class).get();
	
	private PooledEngine pooledEngine;
	private ImmutableArray<Entity> clickedTilesEntities;
	private final ComponentMapper<UnitSelectionMovementTarget> unitSelectionMovementTargetMapper;
	private final ComponentMapper<UnitComponent> unitComponentMapper;
	private final ComponentMapper<TileComponent> tileComponentMapper;
	
	public UnitSelectionMovementSystem() {
		super(selectedEntitiesFamily);
		
		this.unitSelectionMovementTargetMapper =
				ComponentMapper.getFor(UnitSelectionMovementTarget.class);
		this.unitComponentMapper =
				ComponentMapper.getFor(UnitComponent.class);
		this.tileComponentMapper =
				ComponentMapper.getFor(TileComponent.class);
	}
	
	@Override
	public void addedToEngine(Engine engine) {
		super.addedToEngine(engine);
		
		this.pooledEngine = (PooledEngine) engine;
		this.clickedTilesEntities =
				this.pooledEngine.getEntitiesFor(clickedTilesFamily);
				
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		if (this.clickedTilesEntities.size() > 1) {
			throw new NullPointerException("more than one clicked tile !");
		}
		
		if (this.clickedTilesEntities.size() == 1) {
			Entity tileEntity = this.clickedTilesEntities.first();
			
			UnitSelectionMovementTarget unitSelectionMovementTarget =
					this.unitSelectionMovementTargetMapper.get(tileEntity);
			TileComponent tileComponent =
					this.tileComponentMapper.get(tileEntity);
			UnitComponent unitComponent =
					this.unitComponentMapper.get(entity);
			
			if ((unitSelectionMovementTarget.type == UnitSelectionMovementTargetType.Move)
					|| (unitSelectionMovementTarget.type == UnitSelectionMovementTargetType.Attack)) {
				unitComponent.x = tileComponent.x;
				unitComponent.y = tileComponent.y;
				unitComponent.movement -= unitSelectionMovementTarget.range;
			}
		}
	}

}
