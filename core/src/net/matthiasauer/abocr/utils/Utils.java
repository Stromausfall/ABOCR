package net.matthiasauer.abocr.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

public class Utils {
	private static final Random random = new Random();
	
	public static <T> void shuffle(Collection<T> collection) {
		List<T> temp = new ArrayList<T>(collection);
		
		collection.clear();
		
		while (!temp.isEmpty()) {
			int randomIndex = random.nextInt(temp.size());
			T randomElement = temp.remove(randomIndex);
			
			collection.add(randomElement);
		}
	}
}
