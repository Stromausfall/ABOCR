package net.matthiasauer.abocr.map.unit;

import java.util.LinkedList;
import java.util.List;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;

import net.matthiasauer.ecstools.graphics.RenderComponent;

import net.matthiasauer.ecstools.graphics.texture.TextureContainer;
import net.matthiasauer.ecstools.input.base.touch.InputTouchTargetComponent;
import net.matthiasauer.ecstools.input.click.ClickableComponent;
import net.matthiasauer.abocr.graphics.RenderLayer;
import net.matthiasauer.abocr.graphics.TileRenderComponentConversion;
import net.matthiasauer.abocr.map.player.MapElementOwnerComponent;
import net.matthiasauer.abocr.utils.Mappers;

public class UnitRenderSystem extends IteratingSystem {
	@SuppressWarnings("unchecked")
	private static final Family family =
			Family.all(UnitComponent.class).get();
	private final TextureContainer<UnitType> unitTypeTextureContainer;
	private final TextureContainer<UnitStrength> unitStrengthTextureContainer;
	private PooledEngine engine;
	private final List<Entity> renderTargets;

	public UnitRenderSystem() {
		super(family);
		
		this.unitTypeTextureContainer = new TextureContainer<UnitType>();
		this.unitTypeTextureContainer.add(UnitType.Infantry, "infantry");
		
		this.unitStrengthTextureContainer = new TextureContainer<UnitStrength>();
		this.unitStrengthTextureContainer.add(UnitStrength.One, "oneUnit");
		this.unitStrengthTextureContainer.add(UnitStrength.Two, "twoUnit");
		this.unitStrengthTextureContainer.add(UnitStrength.Three, "threeUnit");
		this.unitStrengthTextureContainer.add(UnitStrength.Four, "fourUnit");

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
		UnitComponent unitComponent = 
				Mappers.unitComponent.get(entity);
		
		this.displayCounterAndMakeItClickable(entity, unitComponent);
		this.displayStrength(entity, unitComponent);
	}
	
	private void displayCounterAndMakeItClickable(Entity entity, UnitComponent unitComponent) {
		AtlasRegion typeTexture =
				this.unitTypeTextureContainer.get(unitComponent.type);
		MapElementOwnerComponent ownerComponent =
				Mappers.mapElementOwnerComponent.get(entity);
		
		RenderComponent typeRenderComponent =
				TileRenderComponentConversion.createSprite(
						this.engine,
						unitComponent.x,
						unitComponent.y,
						0,
						typeTexture,
						RenderLayer.UnitType,
						null);

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
	
	private void displayStrength(Entity entity, UnitComponent unitComponent) {
		Entity strengthRenderUnit = 
				this.engine.createEntity();		
		AtlasRegion strengthTexture =
				this.unitStrengthTextureContainer.get(unitComponent.strength);
		RenderComponent strengthRenderComponent =
				TileRenderComponentConversion.createSprite(
						this.engine,
						unitComponent.x,
						unitComponent.y,
						0,
						strengthTexture,
						RenderLayer.UnitType,
						null);

		strengthRenderUnit.add(strengthRenderComponent);
		this.engine.addEntity(strengthRenderUnit);
		this.renderTargets.add(strengthRenderUnit);
	}
}
