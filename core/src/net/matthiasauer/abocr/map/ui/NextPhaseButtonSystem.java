package net.matthiasauer.abocr.map.ui;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;

import net.matthiasauer.ecstools.graphics.RenderComponent;
import net.matthiasauer.ecstools.graphics.RenderPositionUnit;
import net.matthiasauer.ecstools.graphics.texture.TextureLoader;
import net.matthiasauer.ecstools.input.base.touch.InputTouchTargetComponent;
import net.matthiasauer.ecstools.input.click.ClickableComponent;
import net.matthiasauer.ecstools.input.click.ClickedComponent;
import net.matthiasauer.abocr.graphics.RenderLayer;
import net.matthiasauer.abocr.map.player.TurnPhase;
import net.matthiasauer.abocr.utils.ILateInitialization;
import net.matthiasauer.abocr.utils.Mappers;
import net.matthiasauer.abocr.utils.Systems;

public class NextPhaseButtonSystem extends EntitySystem implements ILateInitialization {
	private final AtlasRegion texture;
	private PooledEngine pooledEngine;
	private Entity buttonEntity;
	private Systems systems;
	
	public NextPhaseButtonSystem() {
		this.texture =
				TextureLoader.getInstance().getTexture("nextTurn");
	}
	
	@Override
	public void addedToEngine(Engine engine) {
		this.pooledEngine = (PooledEngine) engine;
		this.buttonEntity = this.pooledEngine.createEntity();
		this.pooledEngine.addEntity(this.buttonEntity);
	}
	
	@Override
	public void update(float deltaTime) {
		boolean isCurrentPlayerHuman =
				systems.ownerManagement.getActivePlayer().interaction;
		this.buttonEntity.remove(RenderComponent.class);
		
		if (isCurrentPlayerHuman) {
			this.buttonEntity.add(
					this.pooledEngine.createComponent(RenderComponent.class).setSprite(
							80,
							-80,
							0,
							RenderPositionUnit.Percent,
							null,
							RenderLayer.UI.order,
							RenderLayer.UI.projected,
							texture));
			this.buttonEntity.add(
					this.pooledEngine.createComponent(ClickableComponent.class));
			this.buttonEntity.add(
					this.pooledEngine.createComponent(InputTouchTargetComponent.class));
			
			ClickedComponent clickedComponent =
					Mappers.clickedComponent.get(this.buttonEntity);
			
			if (clickedComponent != null) {
				System.err.println("next phase !");
				TurnPhase currentPhase =
						this.systems.ownerManagement.getActivePlayerComponent().activePhase;
				
				if (currentPhase == TurnPhase.SpendReinforcements) {
					this.systems.ownerManagement.getActivePlayerComponent().activePhase = TurnPhase.MoveUnits;
				}
			}
		}
	}

	@Override
	public void lateInitialization(Systems systems) {
		this.systems = systems;
	}
}
