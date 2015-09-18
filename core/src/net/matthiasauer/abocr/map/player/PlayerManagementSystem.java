package net.matthiasauer.abocr.map.player;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.ashley.utils.ImmutableArray;

import net.matthiasauer.abocr.map.supply.AboutToNextTurnComponent;
import net.matthiasauer.abocr.map.supply.NextTurnComponent;
import net.matthiasauer.abocr.utils.Mappers;

public class PlayerManagementSystem extends EntitySystem {
	@SuppressWarnings("unchecked")
	private static final Family activePlayerFamily =
			Family.all(ActivePlayerComponent.class).get();
	@SuppressWarnings("unchecked")
	private static final Family mapElementOwnerFamily =
			Family.all(MapElementOwnerComponent.class).get();
	private ImmutableArray<Entity> activePlayerEntities;
	private ImmutableArray<Entity> mapElementOwnerEntities;
	private PooledEngine pooledEngine;
	private Entity entity;
	
	@Override
	public void addedToEngine(Engine engine) {
		this.pooledEngine =
				(PooledEngine) engine;
		this.activePlayerEntities =
				this.pooledEngine.getEntitiesFor(activePlayerFamily);
		this.mapElementOwnerEntities =
				this.pooledEngine.getEntitiesFor(mapElementOwnerFamily);
		this.entity =
				this.pooledEngine.createEntity();
		this.pooledEngine.addEntity(this.entity);
		
		this.setPlayer(Player.Neutral);
	}
	
	public void setPlayer(Player owner) {
		this.entity.add(
				this.pooledEngine.createComponent(ActivePlayerComponent.class).set(owner));
	}
	
	public void nextPlayer() {
		Player firstPlayer = Player.Neutral;
		Player currentPlayer = this.getPlayer();
		Player nextHigherPlayer = null;
		
		for (Player owner : Player.values()) {
			if (owner.order < firstPlayer.order) {
				firstPlayer = owner;
			}
			
			if (owner.order > currentPlayer.order) {
				// if the owner comes AFTER the current player
				if (nextHigherPlayer == null) {
					// no next player yet
					nextHigherPlayer = owner;
				} else {
					// otherwise 
					if (owner.order < nextHigherPlayer.order) {
						// overwrite the next player if this player
						// is closer to the current player !
						nextHigherPlayer = owner;
					}
				}
			}
		}
		
		if (nextHigherPlayer == null) {
			// no higher - use the first one
			this.setPlayer(firstPlayer);
			
			// this also means that we are now in a new turn !
			this.entity.add(
					this.pooledEngine.createComponent(AboutToNextTurnComponent.class));
		} else {
			this.setPlayer(nextHigherPlayer);
		}
	}
	
	public Player getPlayer() {
		if (activePlayerEntities.size() != 1) {
			throw new NullPointerException(
					"There must only ever be exactly one active player ! But there were : "
					+	activePlayerEntities.size());
		}
		
		Entity entity = this.activePlayerEntities.first();
		ActivePlayerComponent activePlayerComponent =
				Mappers.activePlayerComponent.get(entity);
		
		return activePlayerComponent.owner;
	}
	
	@Override
	public void update(float deltaTime) {
		Player activeOwner = this.getPlayer();
		
		for (Entity entity : this.mapElementOwnerEntities) {
			MapElementOwnerComponent mapElementOwnerComponent =
					Mappers.mapElementOwnerComponent.get(entity);
			
			if (mapElementOwnerComponent.owner == activeOwner) {
				mapElementOwnerComponent.active = true;
			} else {
				mapElementOwnerComponent.active = false;
			}
		}
		
		// remove the next turn component
		this.entity.remove(NextTurnComponent.class);
		
		// create a next turn component (if necessary)
		if (this.entity.getComponent(AboutToNextTurnComponent.class) != null) {

			this.entity.remove(AboutToNextTurnComponent.class);
			this.entity.add(
					this.pooledEngine.createComponent(NextTurnComponent.class));
		}
		
		super.update(deltaTime);
	}
}
