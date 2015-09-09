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
import net.matthiasauer.abocr.map.unit.UnitFastAccessSystem;
import net.matthiasauer.abocr.map.unit.UnitStrength;
import net.matthiasauer.abocr.map.unit.range.TargetComponent;

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
					TargetComponent.class).get();
	
	private PooledEngine pooledEngine;
	private ImmutableArray<Entity> clickedTilesEntities;
	private final ComponentMapper<TargetComponent> targetComponentMapper;
	private final ComponentMapper<UnitComponent> unitComponentMapper;
	private final ComponentMapper<TileComponent> tileComponentMapper;
	private UnitFastAccessSystem unitFastAccessSystem;
	
	public UnitSelectionMovementSystem() {
		super(selectedEntitiesFamily);
		
		this.targetComponentMapper =
				ComponentMapper.getFor(TargetComponent.class);
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
		this.unitFastAccessSystem =
				this.pooledEngine.getSystem(UnitFastAccessSystem.class);
	}

	@Override
	protected void processEntity(Entity attackerUnitEntity, float deltaTime) {
		if (this.clickedTilesEntities.size() > 1) {
			throw new NullPointerException("more than one clicked tile !");
		}
		
		if (this.clickedTilesEntities.size() == 1) {
			Entity defenderTileEntity = this.clickedTilesEntities.first();
			
			TargetComponent targetComponent =
					this.targetComponentMapper.get(defenderTileEntity);
			TileComponent defenderTileComponent =
					this.tileComponentMapper.get(defenderTileEntity);
			UnitComponent attackerUnitComponent =
					this.unitComponentMapper.get(attackerUnitEntity);			
			
			switch (targetComponent.type) {
			case Attack:
				Entity defenderUnitEntity =
						this.unitFastAccessSystem.getUnit(
								defenderTileComponent.x,
								defenderTileComponent.y);
				
				boolean attackSuccessful =
						this.performAttack(attackerUnitEntity, defenderUnitEntity);
				
				if (!attackSuccessful) {
					break;
				}
			case Move:
				attackerUnitComponent.x = defenderTileComponent.x;
				attackerUnitComponent.y = defenderTileComponent.y;
				attackerUnitComponent.movement -= targetComponent.range;
				break;
			default:
				break;
			}
		}
	}
	
	private boolean performAttack(Entity attacker, Entity defender) {
		UnitComponent attackerUnitComponent =
				this.unitComponentMapper.get(attacker);
		UnitComponent defenderUnitComponent =
				this.unitComponentMapper.get(defender);
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
