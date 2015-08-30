package net.matthiasauer.abocr.map.unit.interaction.select;

import java.util.ArrayList;
import java.util.Collection;

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

public class UnitSelectionSystem extends IteratingSystem {
	/**
	 * Get all clicked entities
	 */
	@SuppressWarnings("unchecked")
	private static final Family family =
			Family.all(
					UnitComponent.class,
					ClickedComponent.class).get();
	private PooledEngine pooledEngine;
	private Entity unitSelectionEntity;
	private ImmutableArray<Entity> selectedOriginEntities;
	private ImmutableArray<Entity> selectedTargetEntities;
	private ImmutableArray<Entity> tileEntities;
	private final ComponentMapper<UnitComponent> unitComponentMapper;
	private final ComponentMapper<TileComponent> tileComponentMapper;

	public UnitSelectionSystem() {
		super(family);
		
		this.unitComponentMapper =
				ComponentMapper.getFor(UnitComponent.class);
		this.tileComponentMapper =
				ComponentMapper.getFor(TileComponent.class);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void addedToEngine(Engine engine) {
		this.pooledEngine = (PooledEngine) engine;
		this.unitSelectionEntity = this.pooledEngine.createEntity();
		this.pooledEngine.addEntity(this.unitSelectionEntity);

		this.selectedOriginEntities =
				this.pooledEngine.getEntitiesFor(
						Family.all(UnitSelectionMovementOrigin.class).get());
		this.selectedTargetEntities =
				this.pooledEngine.getEntitiesFor(
						Family.all(UnitSelectionMovementTarget.class).get());
		this.tileEntities =
				this.pooledEngine.getEntitiesFor(
						Family.all(TileComponent.class).get());
		super.addedToEngine(engine);
	}
	
	private void addEntity(int x, int y, Collection<Entity> result) {
		for (Entity entity : this.tileEntities) {
			TileComponent tileComponent =
					this.tileComponentMapper.get(entity);
			
			if ((tileComponent.x == x) && (tileComponent.y == y)) {
				result.add(entity);
				break;
			}
		}
	}
	
	private Collection<Entity> getSurroundingTileEntities(int x, int y) {
		Collection<Entity> entities =
				new ArrayList<Entity>();
		
		// display always !
		this.addEntity(x, y+1, entities);
		this.addEntity(x, y-1, entities);
		this.addEntity(x-1, y, entities);
		this.addEntity(x+1, y, entities);
		
		if ((y % 2) == 1) {
			this.addEntity(x+1, y+1, entities);
			this.addEntity(x+1, y-1, entities);
		} else {
			this.addEntity(x-1, y-1, entities);
			this.addEntity(x-1, y+1, entities);
		}
		
		return entities;
	}
	
	private void selectUnit(Entity entity) {
		UnitComponent unitComponent =
				this.unitComponentMapper.get(entity);
		
		entity.add(
				this.pooledEngine.createComponent(UnitSelectionMovementOrigin.class));
		
		for (Entity surroundingTileEntity : this.getSurroundingTileEntities(unitComponent.x, unitComponent.y)) {
			surroundingTileEntity.add(
					this.pooledEngine.createComponent(UnitSelectionMovementTarget.class));
		}
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) {		
		if (this.selectedOriginEntities.size() == 0) {
			// no entity was previous selected !
			this.selectUnit(entity);
		} else {
			boolean wasAlreadySelected = false;
			
			// remove the selection from all (except if it is the one we selected now !)
			for (Entity previouslySelected : this.selectedOriginEntities) {
				if (previouslySelected == entity) {
					wasAlreadySelected = true;
				}

				previouslySelected.remove(UnitSelectionMovementOrigin.class);
			}
			for (Entity previousTarget : this.selectedTargetEntities) {
				previousTarget.remove(UnitSelectionMovementTarget.class);
			}
			
			// only select the entity if it was not already selected !
			if (!wasAlreadySelected) {
				this.selectUnit(entity);
			}
		}
	}
}
