package net.matthiasauer.abocr.map.unit;

public enum UnitType {
	Infantry(2);
	
	public final int maxMovement;
	
	private UnitType(int maxMovement) {
		this.maxMovement = maxMovement;
	}
}
