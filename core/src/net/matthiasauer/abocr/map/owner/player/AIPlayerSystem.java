package net.matthiasauer.abocr.map.owner.player;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.PooledEngine;

import net.matthiasauer.abocr.map.owner.Owner;
import net.matthiasauer.abocr.map.owner.OwnerManagementSystem;

public class AIPlayerSystem extends EntitySystem {
	private OwnerManagementSystem ownerManagementSystem;
	private PooledEngine pooledEngine;
	
	public AIPlayerSystem() {
	}
	
	@Override
	public void addedToEngine(Engine engine) {
		this.pooledEngine = (PooledEngine) engine;
		this.ownerManagementSystem =
				this.pooledEngine.getSystem(OwnerManagementSystem.class);
	}
	
	@Override
	public void update(float deltaTime) {
		Owner owner =
				this.ownerManagementSystem.getPlayer();
		
		if ((owner != Owner.Neutral) && (!owner.interaction)) {
			// we only want non-interactable players which are NOT the NEUTRAL player !
			
			System.err.println("AI Player System's turn : " + owner);
			
			this.ownerManagementSystem.nextPlayer();
		}
	}
}
