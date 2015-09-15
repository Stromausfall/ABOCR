package net.matthiasauer.abocr.map.supply;

import java.util.LinkedList;
import java.util.List;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;

import net.matthiasauer.abocr.graphics.RenderComponent;
import net.matthiasauer.abocr.graphics.RenderLayer;
import net.matthiasauer.abocr.graphics.RenderPositionUnit;
import net.matthiasauer.abocr.graphics.texture.TextureContainer;
import net.matthiasauer.abocr.input.base.touch.InputTouchTargetComponent;
import net.matthiasauer.abocr.input.click.ClickableComponent;
import net.matthiasauer.abocr.map.owner.MapElementOwnerComponent;
import net.matthiasauer.abocr.utils.Mappers;

public class CityRenderSystem extends IteratingSystem {
	@SuppressWarnings("unchecked")
	private static final Family family =
			Family.all(CityComponent.class).get();
	private final TextureContainer<CityType> unitTypeTextureContainer;
	private PooledEngine engine;
	private final List<Entity> renderTargets;

	public CityRenderSystem() {
		super(family);
		
		this.unitTypeTextureContainer = new TextureContainer<CityType>();
		this.unitTypeTextureContainer.add(CityType.Normal, "normalCity");

		this.renderTargets = new LinkedList<Entity>();
	}
	
	@Override
	public void addedToEngine(Engine engine) {
		this.engine = (PooledEngine)engine;
		
		super.addedToEngine(this.engine);
	};
	
	@Override
	public void update(float deltaTime) {
		for (Entity entity : this.renderTargets) {
			entity.removeAll();
			this.engine.removeEntity(entity);
		}
		
		this.renderTargets.clear();
		
		super.update(deltaTime);
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		CityComponent cityComponent = 
				Mappers.cityComponent.get(entity);
		
		this.displayCounterAndMakeItClickable(entity, cityComponent);
	}
	
	private void displayCounterAndMakeItClickable(Entity entity, CityComponent cityComponent) {
		AtlasRegion typeTexture =
				this.unitTypeTextureContainer.get(cityComponent.type);
		MapElementOwnerComponent ownerComponent =
				Mappers.mapElementOwnerComponent.get(entity);
		
		
		RenderComponent typeRenderComponent =
				this.engine.createComponent(RenderComponent.class).set(
						cityComponent.x,
						cityComponent.y,
						0,
						RenderPositionUnit.Tiles,
						typeTexture,
						RenderLayer.Cities,
						ownerComponent.owner.color);

		entity.add(typeRenderComponent);
		
		this.makeClickable(entity, ownerComponent);
	}
	
	private void makeClickable(Entity entity, MapElementOwnerComponent ownerComponent) {
		// also make the counter clickable !
		InputTouchTargetComponent inputTouchTargetComponent =
				this.engine.createComponent(InputTouchTargetComponent.class);

		if (ownerComponent.owner.interaction && ownerComponent.active) {
			entity.add(new ClickableComponent());
			entity.add(inputTouchTargetComponent);
		} else {
			entity.remove(ClickableComponent.class);
			entity.remove(InputTouchTargetComponent.class);
		}
	}
}
