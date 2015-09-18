package net.matthiasauer.abocr.utils;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.EntitySystem;

import net.matthiasauer.abocr.map.player.PlayerManagementSystem;
import net.matthiasauer.abocr.map.tile.TileFastAccessSystem;
import net.matthiasauer.abocr.map.unit.UnitFastAccessSystem;

public class Systems extends EntitySystem {
	public final UnitFastAccessSystem unitFastAccess;
	public final TileFastAccessSystem tileFastAccess;
	public final PlayerManagementSystem ownerManagement;
	
	public Systems(Engine engine) {
		this.unitFastAccess =
				engine.getSystem(UnitFastAccessSystem.class);
		this.tileFastAccess =
				engine.getSystem(TileFastAccessSystem.class);
		this.ownerManagement =
				engine.getSystem(PlayerManagementSystem.class);
	}
	
	@Override
	public void addedToEngine(Engine engine) {
		for (EntitySystem system : engine.getSystems()) {
			if (system instanceof ILateInitialization) {
				ILateInitialization instance = (ILateInitialization) system;
				
				instance.lateInitialization(this);
			}
		}
	}
}
