package net.matthiasauer.abocr.map;

import java.util.Random;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import net.matthiasauer.abocr.graphics.CameraSystem;
import net.matthiasauer.abocr.graphics.RenderSystem;
import net.matthiasauer.abocr.graphics.RenderTextureArchiveSystem;
import net.matthiasauer.abocr.input.InputTouchGeneratorSystem;
import net.matthiasauer.abocr.map.tile.TileComponent;
import net.matthiasauer.abocr.map.tile.TileRenderSystem;
import net.matthiasauer.abocr.map.tile.TileType;

public class MapView extends ScreenAdapter {
	private final PooledEngine engine;
	private final Random random;
	private final OrthographicCamera camera;
	private final InputMultiplexer inputMultiplexer;
	private final Viewport viewport;

	public MapView() {
		this.engine = new PooledEngine();
		this.random = new Random();
		this.camera = new OrthographicCamera(800, 600);
		this.viewport = new ScreenViewport(this.camera);

		this.inputMultiplexer = new InputMultiplexer();
		Gdx.input.setInputProcessor(this.inputMultiplexer);
		
		this.createMap();
		
		this.engine.addSystem(new RenderTextureArchiveSystem());
		this.engine.addSystem(new InputTouchGeneratorSystem(this.inputMultiplexer, this.camera));
		this.engine.addSystem(new CameraSystem(this.camera));
		this.engine.addSystem(new TileRenderSystem());

		this.engine.addSystem(new RenderSystem(this.camera));
		
		//Gdx.app.setLogLevel(Gdx.app.LOG_ERROR);
	}
	
	private void createMap() {		
		for (int x = 0; x < 4; x++) {
			for (int y = 0; y < 3; y++) {
				Entity tile =
						this.engine.createEntity();
				
				TileComponent tileComponent =
						this.engine.createComponent(TileComponent.class);
				tileComponent.x = x;
				tileComponent.y = y;
				tileComponent.tileType =
						TileType.values()[random.nextInt(TileType.values().length)];
				tileComponent.receivesInput = true;

				tile.add(tileComponent);
				
				this.engine.addEntity(tile);
			}
		}
	}
	
	@Override
	public void resize(int width, int height) {
		this.viewport.update(width, height);
	}

	@Override
	public void render (float delta) {
		this.engine.update(delta);
	}
}
