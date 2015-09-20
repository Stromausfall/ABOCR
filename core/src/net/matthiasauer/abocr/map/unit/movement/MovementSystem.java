package net.matthiasauer.abocr.map.unit.movement;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.ashley.systems.IteratingSystem;

import net.matthiasauer.abocr.map.player.MapElementOwnerComponent;
import net.matthiasauer.abocr.map.tile.TileComponent;
import net.matthiasauer.abocr.map.unit.UnitComponent;
import net.matthiasauer.abocr.map.unit.UnitFastAccessSystem;
import net.matthiasauer.abocr.map.unit.UnitStrength;
import net.matthiasauer.abocr.utils.ILateInitialization;
import net.matthiasauer.abocr.utils.Mappers;
import net.matthiasauer.abocr.utils.Systems;

public class MovementSystem extends IteratingSystem implements ILateInitialization {
	@SuppressWarnings("unchecked")
	private static final Family movementEntitiesFamily =
			Family.all(MovementComponent.class).get();
	private UnitFastAccessSystem unitFastAccessSystem;
	private PooledEngine pooledEngine;

	public MovementSystem() {
		super(movementEntitiesFamily);
	}

	@Override
	public void lateInitialization(Systems systems) {
		this.unitFastAccessSystem = systems.unitFastAccess;
		this.pooledEngine = (PooledEngine) this.getEngine();
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		MovementComponent movement =
				Mappers.movementComponent.get(entity);

		TileComponent defenderTileComponent =
				Mappers.tileComponent.get(movement.defenderTileEntity);
		UnitComponent attackerUnitComponent =
				Mappers.unitComponent.get(movement.attackerUnitEntity);
		
		switch (movement.type) {
		case Attack:
			Entity defenderUnitEntity =
					this.unitFastAccessSystem.getUnit(
							defenderTileComponent.x,
							defenderTileComponent.y);
			
			boolean attackSuccessful =
					this.performAttack(
							movement.attackerUnitEntity,
							defenderUnitEntity);
			
			if (!attackSuccessful) {
				break;
			}
		case Move:
			attackerUnitComponent.x = defenderTileComponent.x;
			attackerUnitComponent.y = defenderTileComponent.y;
			attackerUnitComponent.movement -= movement.range;
			
			this.changeTileOwner(
					movement.defenderTileEntity,
					movement.attackerUnitEntity);
			
			break;
		default:
			break;
		}
		
		entity.remove(MovementComponent.class);
	}
	
	public void changeTileOwner(Entity targetTileEntity, Entity attackerUnitEntity) {
		MapElementOwnerComponent targetTileOwner =
				Mappers.mapElementOwnerComponent.get(targetTileEntity);
		MapElementOwnerComponent attackerUnitOwner =
				Mappers.mapElementOwnerComponent.get(attackerUnitEntity);
		
		targetTileOwner.set(attackerUnitOwner);
	}
	
	private boolean performAttack(Entity attacker, Entity defender) {
		UnitComponent attackerUnitComponent =
				Mappers.unitComponent.get(attacker);
		UnitComponent defenderUnitComponent =
				Mappers.unitComponent.get(defender);
		int attackerUnitCount =
				attackerUnitComponent.strength.count;
		int defenderUnitCount =
				defenderUnitComponent.strength.count;
		
		if (attackerUnitCount == defenderUnitCount) {
			this.unitFastAccessSystem.removeUnit(attacker, pooledEngine);
			this.unitFastAccessSystem.removeUnit(defender, pooledEngine);
			return false;
		}
		if (attackerUnitCount < defenderUnitCount) {
			this.unitFastAccessSystem.removeUnit(attacker, pooledEngine);
			defenderUnitComponent.strength = UnitStrength.One;
			return false;
		} else {
			this.unitFastAccessSystem.removeUnit(defender, pooledEngine);
			attackerUnitComponent.strength = UnitStrength.One;
			return true;
		}
	}
}
