package net.matthiasauer.abocr.map.owner.player;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.ashley.utils.ImmutableArray;

import net.matthiasauer.abocr.graphics.RenderComponent;
import net.matthiasauer.abocr.map.player.MapElementOwnerComponent;
import net.matthiasauer.abocr.map.player.Player;
import net.matthiasauer.abocr.map.player.PlayerManagementSystem;
import net.matthiasauer.abocr.map.tile.TileComponent;
import net.matthiasauer.abocr.map.unit.UnitComponent;
import net.matthiasauer.abocr.map.unit.movement.MovementComponent;
import net.matthiasauer.abocr.map.unit.range.TargetType;
import net.matthiasauer.abocr.utils.ILateInitialization;
import net.matthiasauer.abocr.utils.Mappers;
import net.matthiasauer.abocr.utils.Systems;

public class AIPlayerSystem extends EntitySystem implements ILateInitialization {
	@SuppressWarnings("unchecked")
	private Family unitEntityFamily =
			Family.all(UnitComponent.class).get();
	private PlayerManagementSystem ownerManagementSystem;
	private PooledEngine pooledEngine;
	private ImmutableArray<Entity> unitEntities;
	private Systems systems;
	
	@Override
	public void addedToEngine(Engine engine) {
		this.pooledEngine = (PooledEngine) engine;
		this.ownerManagementSystem =
				this.pooledEngine.getSystem(PlayerManagementSystem.class);
		
		this.unitEntities =
				this.pooledEngine.getEntitiesFor(unitEntityFamily);
	}

	@Override
	public void lateInitialization(Systems systems) {
		this.systems = systems;
	}
	
	@Override
	public void update(float deltaTime) {
		Player owner =
				this.ownerManagementSystem.getPlayer();
		
		if ((owner != Player.Neutral) && (!owner.interaction)) {
			// we only want non-interactable players which are NOT the NEUTRAL player !
			
			System.err.println("AI Player System's turn : " + owner);
			
			// get all units			
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
					Collections.shuffle(surroundingTiles);
					
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
