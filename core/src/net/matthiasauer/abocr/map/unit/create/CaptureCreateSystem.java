package net.matthiasauer.abocr.map.unit.create;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.ashley.utils.ImmutableArray;

import net.matthiasauer.abocr.map.income.IncomeComponent;
import net.matthiasauer.abocr.map.player.MapElementOwnerComponent;
import net.matthiasauer.abocr.map.player.Player;
import net.matthiasauer.abocr.map.player.TurnPhase;
import net.matthiasauer.abocr.map.tile.TileComponent;
import net.matthiasauer.abocr.utils.ILateInitialization;
import net.matthiasauer.abocr.utils.Mappers;
import net.matthiasauer.abocr.utils.Systems;
import net.matthiasauer.ecstools.input.click.ClickedComponent;

/**
 * Captures player interaction and creates a request for the creation of a new unit
 */
public class CaptureCreateSystem extends IteratingSystem implements ILateInitialization {
	@SuppressWarnings("unchecked")
	private static final Family family =
			Family.all(IncomeComponent.class).get();
	@SuppressWarnings("unchecked")
	private static final Family clickedTilesFamily =
			Family.all(
					TileComponent.class,
					ClickedComponent.class).get();
	private ImmutableArray<Entity> clickedTiles;
	private Systems systems;
	private PooledEngine pooledEngine;

	public CaptureCreateSystem() {
		super(family);
	}

	@Override
	public void lateInitialization(Systems systems) {
		this.systems = systems;
	}
	
	@Override
	public void addedToEngine(Engine engine) {
		this.clickedTiles = 
				engine.getEntitiesFor(clickedTilesFamily);
		this.pooledEngine = (PooledEngine) engine;
		
		super.addedToEngine(engine);
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		if (this.systems.ownerManagement.getActivePlayerComponent().activePhase != TurnPhase.SpendReinforcements) {
			// only move units if we are in the phase of the turn that deals with reinforcement
			return;
		}
		
		IncomeComponent incomeComponent =
				Mappers.incomeComponent.get(entity);
		Player player =
				systems.ownerManagement.getActivePlayer();
		
		if (!player.interaction) {
			// this is a computer player !
			return;
		}
		
		if (incomeComponent.income <= 0) {
			// no money nothing to do !
			return;
		}
		
		if (this.clickedTiles.size() == 0) {
			// no tile was clicked !
			return;
		}
		
		Entity clickedTileEntity = this.clickedTiles.first();
		TileComponent tileComponent = Mappers.tileComponent.get(clickedTileEntity);
		MapElementOwnerComponent owner = Mappers.mapElementOwnerComponent.get(clickedTileEntity);
		
		if (owner.owner != player) {
			// the player didn't click at one of his own tiles
			return;
		}
		
		Entity clickedUnitEntity =
				this.systems.unitFastAccess.getUnit(tileComponent.x, tileComponent.y);
		
		if (clickedUnitEntity != null) {
			// there was already an entity
			return;
		}
		
		// remove income and create reinforcements for the value
		incomeComponent.income -= 1;
		entity.add(
				this.pooledEngine.createComponent(RequestCreationComponent.class)
						.set(tileComponent.x, tileComponent.y, 1));
	}
	
}
