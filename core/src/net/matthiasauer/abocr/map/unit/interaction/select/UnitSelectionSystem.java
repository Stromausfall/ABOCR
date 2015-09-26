package net.matthiasauer.abocr.map.unit.interaction.select;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.ashley.utils.ImmutableArray;

import net.matthiasauer.abocr.input.click.ClickedComponent;
import net.matthiasauer.abocr.map.player.MapElementOwnerComponent;
import net.matthiasauer.abocr.map.player.Player;
import net.matthiasauer.abocr.map.unit.UnitComponent;
import net.matthiasauer.abocr.utils.ILateInitialization;
import net.matthiasauer.abocr.utils.Mappers;
import net.matthiasauer.abocr.utils.Systems;

public class UnitSelectionSystem extends IteratingSystem implements ILateInitialization {
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
	private Systems systems;

	public UnitSelectionSystem() {
		super(family);
	}
	
	@Override
	public void lateInitialization(Systems systems) {
		this.systems = systems;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void addedToEngine(Engine engine) {
		this.pooledEngine = (PooledEngine) engine;
		this.unitSelectionEntity = this.pooledEngine.createEntity();
		this.pooledEngine.addEntity(this.unitSelectionEntity);

		this.selectedOriginEntities =
				this.pooledEngine.getEntitiesFor(
						Family.all(UnitSelectionMovementOriginComponent.class).get());
		super.addedToEngine(engine);
	}
	
	private void selectUnit(Entity entity) {
		entity.add(
				this.pooledEngine.createComponent(UnitSelectionMovementOriginComponent.class));
	}
	
	@Override
	public void update(float deltaTime) {
		for (Entity entity : this.selectedOriginEntities) {
			MapElementOwnerComponent mapElementOwner =
					Mappers.mapElementOwnerComponent.get(entity);
			Player currentPlayer =
					this.systems.ownerManagement.getPlayer();
			
			if (mapElementOwner.owner != currentPlayer) {
				entity.remove(UnitSelectionMovementOriginComponent.class);
			}
		}
		
		super.update(deltaTime);
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

				previouslySelected.remove(UnitSelectionMovementOriginComponent.class);
			}
			
			// only select the entity if it was not already selected !
			if (!wasAlreadySelected) {
				this.selectUnit(entity);
			}
		}
	}
}
