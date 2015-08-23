package net.matthiasauer.abocr.map.unit;

import com.badlogic.ashley.core.ComponentMapper;
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

public class UnitRenderSystem extends IteratingSystem {
	@SuppressWarnings("unchecked")
	private static final Family family =
			Family.all(UnitComponent.class).get();
	private final ComponentMapper<UnitComponent> unitComponentMapper;
	private final TextureContainer<UnitType> unitTypeTextureContainer;
	private final TextureContainer<UnitStrength> unitStrengthTextureContainer;
	private PooledEngine engine; 

	public UnitRenderSystem() {
		super(family);
		
		this.unitTypeTextureContainer = new TextureContainer<UnitType>();
		this.unitTypeTextureContainer.add(UnitType.Infantry, "infantry");
		
		this.unitStrengthTextureContainer = new TextureContainer<UnitStrength>();
		this.unitStrengthTextureContainer.add(UnitStrength.One, "oneUnit");
		this.unitStrengthTextureContainer.add(UnitStrength.Two, "twoUnit");
		this.unitStrengthTextureContainer.add(UnitStrength.Three, "threeUnit");
		this.unitStrengthTextureContainer.add(UnitStrength.Four, "fourUnit");

		this.unitComponentMapper =
				ComponentMapper.getFor(UnitComponent.class);
	}
	
	@Override
	public void addedToEngine(Engine engine) {
		this.engine = (PooledEngine)engine;
		
		super.addedToEngine(this.engine);
	};

	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		UnitComponent unitComponent = 
				this.unitComponentMapper.get(entity);
		
		this.displayCounterAndMakeItClickable(entity, unitComponent);
		this.displayStrength(entity, unitComponent);;		
	}
	
	private void displayCounterAndMakeItClickable(Entity entity, UnitComponent unitComponent) {
		AtlasRegion typeTexture =
				this.unitTypeTextureContainer.get(unitComponent.type);
		RenderComponent typeRenderComponent =
				this.engine.createComponent(RenderComponent.class).set(
						unitComponent.x,
						unitComponent.y,
						0,
						RenderPositionUnit.Tiles,
						typeTexture,
						RenderLayer.UnitType);

		entity.add(typeRenderComponent);
		
		this.makeClickable(entity);
	}
	
	private void makeClickable(Entity entity) {
		// also make the counter clickable !
		InputTouchTargetComponent inputTouchTargetComponent =
				this.engine.createComponent(InputTouchTargetComponent.class);
		
		entity.add(inputTouchTargetComponent);
	}
	
	private void displayStrength(Entity entity, UnitComponent unitComponent) {
		AtlasRegion strengthTexture =
				this.unitStrengthTextureContainer.get(unitComponent.strength);
		RenderComponent strengthRenderComponent =
				this.engine.createComponent(RenderComponent.class).set(
						unitComponent.x,
						unitComponent.y,
						0,
						RenderPositionUnit.Tiles,
						strengthTexture,
						RenderLayer.UnitType);

		unitComponent.strengthUnit.add(strengthRenderComponent);
	}
}
