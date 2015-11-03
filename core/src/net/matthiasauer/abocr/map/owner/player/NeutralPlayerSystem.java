package net.matthiasauer.abocr.map.owner.player;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.PooledEngine;

import net.matthiasauer.abocr.map.player.Player;
import net.matthiasauer.abocr.map.player.PlayerManagementSystem;

public class NeutralPlayerSystem extends EntitySystem {
	private PlayerManagementSystem ownerManagementSystem;
	private PooledEngine pooledEngine;
	
	public NeutralPlayerSystem() {
	}
	
	@Override
	public void addedToEngine(Engine engine) {
		this.pooledEngine = (PooledEngine) engine;
		this.ownerManagementSystem =
				this.pooledEngine.getSystem(PlayerManagementSystem.class);
	}
	
	@Override
	public void update(float deltaTime) {
		Player owner =
				this.ownerManagementSystem.getActivePlayer();
		
		if (owner == Player.Neutral) {
			// we only want to process the neutral player
			
			System.err.println("Neutral Player System's turn");
			
			this.ownerManagementSystem.nextPlayer();
		}
	}
}
