package net.matthiasauer.abocr.map.owner.player;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Set;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.math.Vector2;

import net.matthiasauer.abocr.map.income.IncomeComponent;
import net.matthiasauer.abocr.map.player.MapElementOwnerComponent;
import net.matthiasauer.abocr.map.player.Player;
import net.matthiasauer.abocr.map.player.PlayerManagementSystem;
import net.matthiasauer.abocr.map.tile.TileComponent;
import net.matthiasauer.abocr.map.unit.UnitComponent;
import net.matthiasauer.abocr.map.unit.UnitStrength;
import net.matthiasauer.abocr.map.unit.create.RequestCreationComponent;
import net.matthiasauer.abocr.map.unit.movement.MovementComponent;
import net.matthiasauer.abocr.map.unit.range.TargetType;
import net.matthiasauer.abocr.map.unit.reinforce.RequestReinforcementComponent;
import net.matthiasauer.abocr.utils.ILateInitialization;
import net.matthiasauer.abocr.utils.Mappers;
import net.matthiasauer.abocr.utils.Systems;
import net.matthiasauer.abocr.utils.Utils;

public class AIPlayerSystem extends EntitySystem implements ILateInitialization {
	@SuppressWarnings("unchecked")
	private Family unitEntityFamily =
			Family.all(UnitComponent.class).get();
	@SuppressWarnings("unchecked")
	private Family incomeEntityFamily =
			Family.all(IncomeComponent.class).get();
	private PlayerManagementSystem ownerManagementSystem;
	private PooledEngine pooledEngine;
	private ImmutableArray<Entity> unitEntities;
	private ImmutableArray<Entity> incomeEntities;
	private Systems systems;
	
	@Override
	public void addedToEngine(Engine engine) {
		this.pooledEngine = (PooledEngine) engine;
		this.ownerManagementSystem =
				this.pooledEngine.getSystem(PlayerManagementSystem.class);
		
		this.unitEntities =
				this.pooledEngine.getEntitiesFor(unitEntityFamily);
		this.incomeEntities =
				this.pooledEngine.getEntitiesFor(incomeEntityFamily);
	}

	@Override
	public void lateInitialization(Systems systems) {
		this.systems = systems;
	}
	
	private Set<Entity> calculateUnitsToReinforce(Player player) {
		Set<Entity> result =
				new HashSet<Entity>();
		UnitStrength max = UnitStrength.largest;
		
		for (Entity unitEntity : this.unitEntities) {
			MapElementOwnerComponent unitOwner =
					Mappers.mapElementOwnerComponent.get(unitEntity);
			
			if (unitOwner.owner == player) {
				UnitComponent unitComponent =
						Mappers.unitComponent.get(unitEntity);
				
				if (unitComponent.strength != max) {
					result.add(unitEntity);
				}
			}
		}
		
		return result;
	}
	
	private Set<Entity> calculateTilesToCreateUnitsOn(Player player) {
		Set<Entity> result =
				new HashSet<Entity>();
		Set<Vector2> allTiles =
				this.systems.tileFastAccess.getOwnedTiles(player);
		
		for (Vector2 tilePos : allTiles) {
			if (this.systems.unitFastAccess.getUnit(tilePos) == null) {
				Entity tileEntity =
						this.systems.tileFastAccess.getTile(tilePos);
				result.add(tileEntity);
			}
		}
		
		return result;
	}
	
	private static final Random random = new Random();
	
	@Override
	public void update(float deltaTime) {
		Player owner =
				this.ownerManagementSystem.getPlayer();
		
		if ((owner != Player.Neutral) && (!owner.interaction)) {
			// we only want non-interactable players which are NOT the NEUTRAL player !
			
			System.err.println("AI Player System's turn : " + owner);

			if (this.incomeEntities.size() == 0l) {
				return;
			}
				
				
			IncomeComponent income =
					Mappers.incomeComponent.get(
							this.incomeEntities.first());
			
			if (income.income > 0) {			
				Set<Entity> unitsToReinforce	=
						this.calculateUnitsToReinforce(owner);
				
				if (!unitsToReinforce.isEmpty()) {
					// we can reinforce (do it with 50% chance)
					if (random.nextBoolean()) {
						Entity unitEntity = unitsToReinforce.iterator().next();
						UnitComponent unit = 
								Mappers.unitComponent.get(unitEntity);
						
						unitEntity.add(
								this.pooledEngine.createComponent(RequestReinforcementComponent.class).set(
										unit.x, unit.y , 1));
						
						income.income -= 1;

						return;
					}
					
					// otherwise try to create a unit
				}
				
				Set<Entity> freeOwnedTiles = this.calculateTilesToCreateUnitsOn(owner);
				
				if (!freeOwnedTiles.isEmpty()) {
					Entity tileEntity =
							freeOwnedTiles.iterator().next();
					TileComponent tileComponent =
							this.systems.tileFastAccess.getTileComponent(tileEntity);
					
					tileEntity.add(
							this.pooledEngine.createComponent(RequestCreationComponent.class).set(
									tileComponent.x, tileComponent.y, 1));
					
					income.income -= 1;
					return;
				}
				
			}
			
			
			// move all units			
			for (Entity attackerUnitEntity : this.unitEntities) {
				UnitComponent unit =
						Mappers.unitComponent.get(attackerUnitEntity);
				MapElementOwnerComponent attackUnitOwner =
						Mappers.mapElementOwnerComponent.get(attackerUnitEntity);
				
				if (attackUnitOwner.owner != owner) {
					// the unit doesn't belong to the owner
					continue;
				}
				
				boolean ableToMove = true;
				
				while ((unit.movement != 0) && ableToMove) {
					List<Entity> surroundingTiles =
							new LinkedList<Entity>(
									systems.tileFastAccess.getSurroundingTiles(unit.x, unit.y));
					
					// we want to get a different ordering !
					Utils.shuffle(surroundingTiles);
					
					ableToMove = false;
					
					if (unit.movement == 0) {
						// no movement points
						break;
					}
					
					for (Entity defenderTileEntity : surroundingTiles) {
						TileComponent surroundingTile =
								Mappers.tileComponent.get(defenderTileEntity);
						Entity unitAtSurroundingTile =
								systems.unitFastAccess.getUnit(
										surroundingTile.x,
										surroundingTile.y);
						MapElementOwnerComponent surroundingOwner =
								Mappers.mapElementOwnerComponent.get(defenderTileEntity);
						
						if ((surroundingOwner.owner != owner)
								&& surroundingTile.tileType.traversable) {
							// attacking an enemy tile !
							MovementComponent movementComponent =
									this.pooledEngine.createComponent(MovementComponent.class);
							
							movementComponent.attackerUnitEntity = attackerUnitEntity;
							movementComponent.defenderTileEntity = defenderTileEntity;
							movementComponent.range = 1;
							
							if (unitAtSurroundingTile != null) {
								movementComponent.type = TargetType.Attack;
							} else {
								movementComponent.type = TargetType.Move;
							}
							
							attackerUnitEntity.add(movementComponent);
							
							ableToMove = true;
							return;
						}
						if ((surroundingOwner.owner == owner)
								&& (unitAtSurroundingTile == null)) {
							// moving to an own tile
							MovementComponent movementComponent =
									this.pooledEngine.createComponent(MovementComponent.class);
							
							movementComponent.attackerUnitEntity = attackerUnitEntity;
							movementComponent.defenderTileEntity = defenderTileEntity;
							movementComponent.range = 1;
							movementComponent.type = TargetType.Move;
							
							attackerUnitEntity.add(movementComponent);
							
							ableToMove = true;
							return;
						}
					}
				}
			}
			
			this.ownerManagementSystem.nextPlayer();
System.err.println("moved all my entities :)");
		}
	}
}
