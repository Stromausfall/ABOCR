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
		super.addedToEngine(engine);
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		if (this.selectedOriginEntities.size() == 0) {
			// no entity was previous selected !
			entity.add(
					this.pooledEngine.createComponent(UnitSelectionMovementOrigin.class));
		} else {
			boolean wasAlreadySelected = false;
			
			// remove the selection from all (except if it is the one we selected now !)
			for (Entity previouslySelected : this.selectedOriginEntities) {
				if (previouslySelected == entity) {
					wasAlreadySelected = true;
				}

				previouslySelected.remove(UnitSelectionMovementOrigin.class);
			}
			
			// only select the entity if it was not already selected !
			if (!wasAlreadySelected) {
				entity.add(
						this.pooledEngine.createComponent(UnitSelectionMovementOrigin.class));
			}
		}
	}
}