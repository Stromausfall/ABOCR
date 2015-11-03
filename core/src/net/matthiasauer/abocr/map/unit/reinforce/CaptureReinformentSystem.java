package net.matthiasauer.abocr.map.unit.reinforce;

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
import net.matthiasauer.abocr.map.unit.UnitComponent;
import net.matthiasauer.abocr.map.unit.UnitStrength;
import net.matthiasauer.abocr.utils.ILateInitialization;
import net.matthiasauer.abocr.utils.Mappers;
import net.matthiasauer.abocr.utils.Systems;
import net.matthiasauer.ecstools.input.click.ClickedComponent;

/**
 * Captures player interaction and creates a request for a reinforcement of an already existing unit
 */
public class CaptureReinformentSystem extends IteratingSystem implements ILateInitialization {
	@SuppressWarnings("unchecked")
	private static final Family family =
			Family.all(IncomeComponent.class).get();
	@SuppressWarnings("unchecked")
	private static final Family clickedUnitsFamily =
			Family.all(
					UnitComponent.class,
					ClickedComponent.class).get();
	private ImmutableArray<Entity> clickedUnits;
	private Systems systems;
	private PooledEngine pooledEngine;

	public CaptureReinformentSystem() {
		super(family);
	}

	@Override
	public void lateInitialization(Systems systems) {
		this.systems = systems;
	}
	
	@Override
	public void addedToEngine(Engine engine) {
		this.clickedUnits = 
				engine.getEntitiesFor(clickedUnitsFamily);
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
		
		if (this.clickedUnits.size() == 0) {
			// no entity was clicked !
			return;
		}
		
		Entity clickedEntity = this.clickedUnits.first();
		UnitComponent unitComponent = Mappers.unitComponent.get(clickedEntity);
		MapElementOwnerComponent owner = Mappers.mapElementOwnerComponent.get(clickedEntity);
		
		if (owner.owner != player) {
			// the player didn't click at one of his own units
			return;
		}
		
		int currentStrength = unitComponent.strength.count;
		int newPossibleStrength = currentStrength + 1;

		if (newPossibleStrength > UnitStrength.largest.count) {
			// can't be reinforced = already at full strength !
			return;
		}
		
		// remove income and create reinforcements for the value
		incomeComponent.income -= 1;
		clickedEntity.add(
				this.pooledEngine.createComponent(RequestReinforcementComponent.class)
						.set(unitComponent.x, unitComponent.y, 1));
	}
	
}
