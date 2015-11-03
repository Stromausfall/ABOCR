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
import net.matthiasauer.abocr.map.player.PlayerManagementSystem;
import net.matthiasauer.abocr.utils.Mappers;

public class NextTurnButtonSystem extends EntitySystem {
	private PlayerManagementSystem ownerManagementSystem;
	private final AtlasRegion texture;
	private PooledEngine pooledEngine;
	private Entity buttonEntity;
	
	public NextTurnButtonSystem() {
		this.texture =
				TextureLoader.getInstance().getTexture("nextTurn");
	}
	
	@Override
	public void addedToEngine(Engine engine) {
		this.pooledEngine = (PooledEngine) engine;
		this.buttonEntity = this.pooledEngine.createEntity();
		this.pooledEngine.addEntity(this.buttonEntity);
		this.ownerManagementSystem =
				this.pooledEngine.getSystem(PlayerManagementSystem.class);
	}
	
	@Override
	public void update(float deltaTime) {
		this.buttonEntity.add(
				this.pooledEngine.createComponent(RenderComponent.class).setSprite(
						80,
						80,
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
			// button clicked
			// only works if the current player is interactable !
			if (this.ownerManagementSystem.getActivePlayer().interaction) {
				System.err.println("oi ! next turn now :) was : " + this.ownerManagementSystem.getActivePlayer());
				this.ownerManagementSystem.nextPlayer();
			}
		}
	}
}
