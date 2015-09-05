package net.matthiasauer.abocr.map.owner;

import com.badlogic.gdx.graphics.Color;

public enum Owner {
	Player1(Color.BLUE),
	Player2(Color.RED),
	Neutral(Color.LIGHT_GRAY);
	
	public final Color color;
	
	Owner(Color color) {
		this.color = color;
	}
}
