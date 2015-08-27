package net.matthiasauer.abocr.utils.state;

import com.badlogic.ashley.core.Component;

public abstract class StateComponent implements Component {
	public StateEnum state;
	public StateSystem<? extends StateComponent, ? extends StateComponent> claimant;
}
