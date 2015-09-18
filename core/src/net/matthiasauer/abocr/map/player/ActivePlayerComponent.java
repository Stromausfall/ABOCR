package net.matthiasauer.abocr.map.player;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool.Poolable;

public class ActivePlayerComponent implements Component, Poolable {
	public Player owner;
	
	@Override
	public void reset() {
		this.set(Player.Neutral);
	}
	
	public ActivePlayerComponent set(Player owner) {
		this.owner = owner;
		
		return this;
	}
}
