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
import net.matthiasauer.abocr.graphics.texture.TextureLoader;
import net.matthiasauer.abocr.map.unit.UnitComponent;
import net.matthiasauer.abocr.utils.Mappers;

public class UnsuppliedRenderSystem extends IteratingSystem {
	@SuppressWarnings("unchecked")
	private static final Family family =
			Family.all(UnsuppliedComponent.class).get();
	private PooledEngine engine;
	private final AtlasRegion texture;
	private final List<Entity> renderTargets;

	public UnsuppliedRenderSystem() {
		super(family);

		this.renderTargets =
				new LinkedList<Entity>();
		this.texture =
				TextureLoader.getInstance().getTexture("unsupplied");
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
		UnitComponent unit =
				Mappers.unitComponent.get(entity);
		Entity renderEntity =
				this.engine.createEntity();
		
		RenderComponent typeRenderComponent =
				this.engine.createComponent(RenderComponent.class).setSprite(
						unit.x,
						unit.y,
						0,
						RenderPositionUnit.Tiles,
						this.texture,
						RenderLayer.Unsupplied,
						null);

		renderEntity.add(typeRenderComponent);
		
		this.engine.addEntity(renderEntity);
		this.renderTargets.add(renderEntity);
	}
}
