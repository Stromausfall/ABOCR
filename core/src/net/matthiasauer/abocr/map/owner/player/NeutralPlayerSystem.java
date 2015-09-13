package net.matthiasauer.abocr.map.owner.player;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.PooledEngine;

import net.matthiasauer.abocr.map.owner.Owner;
import net.matthiasauer.abocr.map.owner.OwnerManagementSystem;

public class NeutralPlayerSystem extends EntitySystem {
	private OwnerManagementSystem ownerManagementSystem;
	private PooledEngine pooledEngine;
	
	public NeutralPlayerSystem() {
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
		
		if (owner == Owner.Neutral) {
			// we only want to process the neutral player
			
			System.err.println("Neutral Player System's turn");
			
			this.ownerManagementSystem.nextPlayer();
		}
	}
}
