package net.matthiasauer.abocr.map.unit.interaction;

import com.badlogic.ashley.core.Component;

public abstract class Interaction {
	public enum Result {
		
	}
	
	public abstract boolean checkIfRejected(Component component);
	public abstract boolean checkIfBeing(Component component);
	
	public abstract boolean process(Component component);
}
