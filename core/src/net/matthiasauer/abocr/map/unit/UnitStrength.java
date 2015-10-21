package net.matthiasauer.abocr.map.unit;

public enum UnitStrength {
	One(1),
	Two(2),
	Three(3),
	Four(4);

	public static final UnitStrength smallest;
	public static final UnitStrength largest;
	
	static {
		UnitStrength smallestValue = UnitStrength.values()[0];
		UnitStrength largestValue = UnitStrength.values()[0];
		
		for (UnitStrength element : UnitStrength.values()) {
			if (element.count > largestValue.count) {
				largestValue = element;
			}
			
			if (element.count < smallestValue.count) {
				smallestValue = element;
			}
		}
		
		smallest = smallestValue;
		largest = largestValue;
	}
	
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
