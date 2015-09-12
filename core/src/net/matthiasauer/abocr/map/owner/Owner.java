package net.matthiasauer.abocr.map.owner;

import com.badlogic.gdx.graphics.Color;

public enum Owner {
	Player1(Color.BLUE, true),
	Player2(Color.RED, true),
	Neutral(Color.LIGHT_GRAY, false);
	
	public final Color color;
	public final boolean interaction;
	
	Owner(Color color, boolean interaction) {
		this.color = color;
		this.interaction = interaction;
	}
}
