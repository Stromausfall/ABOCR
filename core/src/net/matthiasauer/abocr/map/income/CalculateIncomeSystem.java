package net.matthiasauer.abocr.map.income;

import java.util.Set;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.math.Vector2;

import net.matthiasauer.abocr.map.player.Player;
import net.matthiasauer.abocr.map.supply.CityComponent;
import net.matthiasauer.abocr.utils.ILateInitialization;
import net.matthiasauer.abocr.utils.Mappers;
import net.matthiasauer.abocr.utils.Systems;

public class CalculateIncomeSystem extends IteratingSystem implements ILateInitialization {
	@SuppressWarnings("unchecked")
	private static final Family requestIncome =
			Family.all(RequestIncomeCalculationComponent.class).get();
	@SuppressWarnings("unchecked")
	private static final Family incomeFamily =
			Family.all(IncomeComponent.class).get();
	private ImmutableArray<Entity> incomeEntities;
	private Systems systems;
	private PooledEngine engine;
	
	public CalculateIncomeSystem() {
		super(requestIncome);
	}
	
	@Override
	public void addedToEngine(Engine engine) {
		super.addedToEngine(engine);
		
		this.engine = (PooledEngine)engine;
		this.incomeEntities =
				this.engine.getEntitiesFor(incomeFamily);
	}
	
	@Override
	public void update(float deltaTime) {
		super.update(deltaTime);
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		for (Entity entity2 : this.incomeEntities) {
			entity2.remove(IncomeComponent.class);
		}
		
		entity.remove(RequestIncomeCalculationComponent.class);
		
		Player player =
				systems.ownerManagement.getActivePlayer();
		Set<Vector2> ownedTiles =
				systems.tileFastAccess.getOwnedTiles(player);
		
		// count cities
		int ownedCities = 0;
		
		for (Vector2 position : ownedTiles) {
			Entity tileEntity =
					this.systems.tileFastAccess.getTile(position);
			CityComponent cityComponent = 
					Mappers.cityComponent.get(tileEntity);
			
			if (cityComponent != null) {
				ownedCities++;
			}
		}
		
		int income = ownedCities;
		
		entity.add(
				this.engine.createComponent(IncomeComponent.class).set(income));
	}

	@Override
	public void lateInitialization(Systems systems) {
		this.systems = systems;
	}
}
