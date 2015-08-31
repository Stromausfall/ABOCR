package net.matthiasauer.abocr.map.unit.interaction.select;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.ashley.utils.ImmutableArray;

import net.matthiasauer.abocr.input.click.ClickedComponent;
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

	public UnitSelectionSystem() {
		super(family);
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
		super.addedToEngine(engine);
	}
	
	private void selectUnit(Entity entity) {
		entity.add(
				this.pooledEngine.createComponent(UnitSelectionMovementOrigin.class));
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
