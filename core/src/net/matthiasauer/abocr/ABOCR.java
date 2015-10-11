package net.matthiasauer.abocr;

import com.badlogic.gdx.Game;

import net.matthiasauer.abocr.map.MapView;

public class ABOCR extends Game {	
	@Override
	public void create () {
		setScreen(new MapView());
	}
}
