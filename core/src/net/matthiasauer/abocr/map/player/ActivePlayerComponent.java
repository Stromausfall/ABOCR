package net.matthiasauer.abocr.map.player;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool.Poolable;

public class ActivePlayerComponent implements Component, Poolable {
	public Player owner;
	public TurnPhase activePhase;
	
	@Override
	public void reset() {
		this.set(Player.Neutral, TurnPhase.Inactive);
	}
	
	public ActivePlayerComponent set(Player owner, TurnPhase activePhase) {
		this.activePhase = activePhase;
		this.owner = owner;
		
		return this;
	}
}
