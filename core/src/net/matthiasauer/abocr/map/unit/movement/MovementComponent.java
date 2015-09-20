package net.matthiasauer.abocr.map.unit.movement;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.Pool.Poolable;

import net.matthiasauer.abocr.map.unit.range.TargetType;

public class MovementComponent implements Component, Poolable {
	public int range;
	public TargetType type;
	public Entity defenderTileEntity;
	public Entity attackerUnitEntity;
	
	public MovementComponent set(
			TargetType type,
			Entity defenderTileEntity,
			Entity attackerUnitEntity,
			int range) {
		this.type = type;
		this.defenderTileEntity = defenderTileEntity;
		this.attackerUnitEntity = attackerUnitEntity;
		this.range = range;
		
		return this;
	}
	
	@Override
	public void reset() {
		this.set(null, null, null, 0);
	}
}
