package net.matthiasauer.abocr.map.income;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool.Poolable;

public class IncomeComponent implements Component, Poolable {
	public int income;
	
	public IncomeComponent set(int income) {
		this.income = income;
		
		return this;
	}
	
	@Override
	public void reset() {
	}
}
