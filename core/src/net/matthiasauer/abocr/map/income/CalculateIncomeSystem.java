package net.matthiasauer.abocr.map.income;

import java.util.Set;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.Vector2;

import net.matthiasauer.abocr.map.player.Player;
import net.matthiasauer.abocr.map.supply.CityComponent;
import net.matthiasauer.abocr.utils.ILateInitialization;
import net.matthiasauer.abocr.utils.Mappers;
import net.matthiasauer.abocr.utils.Systems;

public class CalculateIncomeSystem extends IteratingSystem implements ILateInitialization {
	@SuppressWarnings("unchecked")
	private static final Family requestIncome =
			Family.all(RequestIncomeCalculation.class).get();
	private Systems systems;
	
	public CalculateIncomeSystem() {
		super(requestIncome);
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		entity.remove(RequestIncomeCalculation.class);
		
		Player player =
				systems.ownerManagement.getPlayer();
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

				System.err.println("x !111 " + player + " = " + ownedCities);
	}

	@Override
	public void lateInitialization(Systems systems) {
		this.systems = systems;
	}
}
