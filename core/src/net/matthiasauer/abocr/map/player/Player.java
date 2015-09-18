package net.matthiasauer.abocr.map.player;

import com.badlogic.gdx.graphics.Color;

public enum Player {
	Player1(Color.BLUE, true, 2),
	Player2(Color.RED, true, 5),
	Player3(Color.YELLOW, false, 9),
	Neutral(Color.LIGHT_GRAY, false, 10);
	
	public final Color color;
	public final boolean interaction;
	public final int order;
	
	Player(Color color, boolean interaction, int order) {
		this.order = order;
		this.color = color;
		this.interaction = interaction;
	}
}
