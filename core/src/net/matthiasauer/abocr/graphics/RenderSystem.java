package net.matthiasauer.abocr.graphics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class RenderSystem extends EntitySystem {
	public final OrthographicCamera camera;
	private final SpriteBatch spriteBatch;
	private ImmutableArray<Entity> entitiesToRender;
	private ComponentMapper<RenderComponent> renderComponentMapper;
	private final Map<RenderLayer, List<RenderComponent>> sortedComponents;

	public RenderSystem(OrthographicCamera camera) {
		this.camera = camera;
		this.spriteBatch = new SpriteBatch();
		this.sortedComponents = new HashMap<RenderLayer, List<RenderComponent>>();
		
		for (RenderLayer layer : RenderLayer.values()) {
			this.sortedComponents.put(layer, new ArrayList<RenderComponent>());
		}
	}
	
	private void clearSortedComponents() {
		for (RenderLayer layer : this.sortedComponents.keySet()) {
			this.sortedComponents.get(layer).clear();
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void addedToEngine(Engine engine) {
		this.renderComponentMapper =
				ComponentMapper.getFor(RenderComponent.class);
		this.entitiesToRender =
				engine.getEntitiesFor(
						Family.all(RenderComponent.class).get());
		
		super.addedToEngine(engine);
	}
	
	@Override
	public void update(float deltaTime) {
		Gdx.gl.glClearColor(0.5f, 0.5f, 0.5f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		this.orderRenderComponents();

		this.spriteBatch.begin();
		Boolean lastProjectedValue = null;
		
		// iterate over the enum in order
		for (RenderLayer layer : RenderLayer.values()) {
			
			
			if (lastProjectedValue != (Boolean)layer.projected) {
				lastProjectedValue = layer.projected;
				
				// end
				this.spriteBatch.end();
				
				if (layer.projected) {
					this.spriteBatch.setProjectionMatrix(this.camera.combined);
				} else {
					this.spriteBatch.setProjectionMatrix(this.camera.projection);
				}
				
				// start new batch
				this.spriteBatch.begin();
			}
			
			
			for (RenderComponent renderComponent : this.sortedComponents.get(layer)) {
				
				this.spriteBatch.draw(
						renderComponent.texture,
						renderComponent.position.x - renderComponent.texture.getRegionWidth() / 2,
						renderComponent.position.y - renderComponent.texture.getRegionHeight() / 2,
						renderComponent.texture.getRegionWidth()/2,
						renderComponent.texture.getRegionHeight()/2,
						renderComponent.texture.getRegionWidth(),
						renderComponent.texture.getRegionHeight(),
						1f,
						1f,
						renderComponent.rotation);
			}
		}
		
		this.spriteBatch.end();
	}
	
	private void orderRenderComponents() {
		this.clearSortedComponents();
		
		for (Entity renderEntity : this.entitiesToRender) {
			RenderComponent data =
					this.renderComponentMapper.get(renderEntity);
			
			this.sortedComponents.get(data.layer).add(data);
		}
	}
}
