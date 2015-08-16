package net.matthiasauer.abocr.map.unit;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;

import net.matthiasauer.abocr.TextureContainer;
import net.matthiasauer.abocr.graphics.RenderComponent;
import net.matthiasauer.abocr.graphics.RenderLayer;
import net.matthiasauer.abocr.graphics.RenderPositionUnit;

public class UnitRenderSystem extends IteratingSystem {
	@SuppressWarnings("unchecked")
	private static final Family family =
			Family.all(UnitComponent.class).get();
	private final ComponentMapper<UnitComponent> unitComponentMapper;
	private final TextureContainer<UnitType> unitTypeTextureContainer;
	private PooledEngine engine; 

	public UnitRenderSystem() {
		super(family);
		
		this.unitTypeTextureContainer = new TextureContainer<UnitType>();
		this.unitTypeTextureContainer.add(UnitType.Infantry, "infantry");

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
		AtlasRegion typeTexture =
				this.unitTypeTextureContainer.get(unitComponent.type);
		RenderComponent renderComponent =
				this.engine.createComponent(RenderComponent.class).set(
						unitComponent.x,
						unitComponent.y,
						0,
						RenderPositionUnit.Tiles,
						typeTexture,
						RenderLayer.UnitType);
		
		entity.add(renderComponent);
	}
}
