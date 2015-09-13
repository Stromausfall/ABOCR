package net.matthiasauer.abocr.map.ui;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;

import net.matthiasauer.abocr.graphics.RenderComponent;
import net.matthiasauer.abocr.graphics.RenderLayer;
import net.matthiasauer.abocr.graphics.RenderPositionUnit;
import net.matthiasauer.abocr.graphics.texture.TextureLoader;
import net.matthiasauer.abocr.input.base.touch.InputTouchTargetComponent;
import net.matthiasauer.abocr.input.click.ClickableComponent;
import net.matthiasauer.abocr.input.click.ClickedComponent;
import net.matthiasauer.abocr.map.owner.OwnerManagementSystem;

public class NextTurnButtonSystem extends EntitySystem {
	private ComponentMapper<ClickedComponent> clickedComponentMapper;
	private OwnerManagementSystem ownerManagementSystem;
	private final AtlasRegion texture;
	private PooledEngine pooledEngine;
	private Entity buttonEntity;
	
	public NextTurnButtonSystem() {
		this.texture =
				TextureLoader.getInstance().getTexture("nextTurn");
		this.clickedComponentMapper =
				ComponentMapper.getFor(ClickedComponent.class);
	}
	
	@Override
	public void addedToEngine(Engine engine) {
		this.pooledEngine = (PooledEngine) engine;
		this.buttonEntity = this.pooledEngine.createEntity();
		this.pooledEngine.addEntity(this.buttonEntity);
		this.ownerManagementSystem =
				this.pooledEngine.getSystem(OwnerManagementSystem.class);
	}
	
	@Override
	public void update(float deltaTime) {
		this.buttonEntity.add(
				this.pooledEngine.createComponent(RenderComponent.class).set(
						80,
						80,
						0,
						RenderPositionUnit.Percent,
						texture,
						RenderLayer.UI,
						null));
		this.buttonEntity.add(
				this.pooledEngine.createComponent(ClickableComponent.class));
		this.buttonEntity.add(
				this.pooledEngine.createComponent(InputTouchTargetComponent.class));
		
		ClickedComponent clickedComponent =
				this.clickedComponentMapper.get(this.buttonEntity);
		
		if (clickedComponent != null) {
			// button clicked
			// only works if the current player is interactable !
			if (this.ownerManagementSystem.getPlayer().interaction) {
				System.err.println("oi ! next turn now :) was : " + this.ownerManagementSystem.getPlayer());
				this.ownerManagementSystem.nextPlayer();
			}
		}
	}
}
