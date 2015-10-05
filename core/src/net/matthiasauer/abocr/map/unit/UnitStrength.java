package net.matthiasauer.abocr.map.unit;

public enum UnitStrength {
	One(1),
	Two(2),
	Three(3),
	Four(4);
	
	public int count;
	
	private UnitStrength(int count) {
		this.count = count;
	}
	
	public static UnitStrength get(int count) {		
		for (UnitStrength element : UnitStrength.values()) {
			if (element.count == count) {
				return element;
			}
		}
		
		throw new NullPointerException("Found no UnitStrength with count : " + count);
	}
}
