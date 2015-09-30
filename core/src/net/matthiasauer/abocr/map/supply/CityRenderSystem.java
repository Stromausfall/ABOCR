package net.matthiasauer.abocr.map.supply;

import java.util.LinkedList;
import java.util.List;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;

import net.matthiasauer.ecstools.graphics.RenderComponent;
import net.matthiasauer.ecstools.graphics.RenderLayer;
import net.matthiasauer.ecstools.graphics.RenderPositionUnit;
import net.matthiasauer.ecstools.graphics.texture.TextureContainer;
import net.matthiasauer.abocr.utils.Mappers;

public class CityRenderSystem extends IteratingSystem {
	@SuppressWarnings("unchecked")
	private static final Family family =
			Family.all(CityComponent.class).get();
	private final TextureContainer<CityType> unitTypeTextureContainer;
	private PooledEngine pooledEngine;
	private final List<Entity> renderTargets;

	public CityRenderSystem() {
		super(family);
		
		this.unitTypeTextureContainer = new TextureContainer<CityType>();
		this.unitTypeTextureContainer.add(CityType.Normal, "normalCity");

		this.renderTargets = new LinkedList<Entity>();
	}
	
	@Override
	public void addedToEngine(Engine engine) {
		this.pooledEngine = (PooledEngine)engine;
		
		super.addedToEngine(this.pooledEngine);
	};
	
	@Override
	public void update(float deltaTime) {
		for (Entity entity : this.renderTargets) {
			entity.removeAll();
			this.pooledEngine.removeEntity(entity);
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
		Entity renderEntity = this.pooledEngine.createEntity();

		RenderComponent typeRenderComponent =
				this.pooledEngine.createComponent(RenderComponent.class).setSprite(
						cityComponent.x,
						cityComponent.y,
						0,
						RenderPositionUnit.Tiles,
						typeTexture,
						RenderLayer.Cities,
						null);

		renderEntity.add(typeRenderComponent);
		
		this.pooledEngine.addEntity(renderEntity);
		this.renderTargets.add(renderEntity);
	}
}
