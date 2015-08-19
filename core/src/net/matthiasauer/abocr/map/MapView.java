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

import net.matthiasauer.abocr.graphics.RenderSystem;
import net.matthiasauer.abocr.graphics.camera.CameraSystem;
import net.matthiasauer.abocr.graphics.camera.CameraZoomSystem;
import net.matthiasauer.abocr.graphics.texture.archive.RenderTextureArchiveSystem;
import net.matthiasauer.abocr.input.click.ClickableComponent;
import net.matthiasauer.abocr.input.base.simple.InputSimpleEventGenerator;
import net.matthiasauer.abocr.input.base.touch.InputTouchGeneratorSystem;
import net.matthiasauer.abocr.input.click.ClickGeneratorSystem;
import net.matthiasauer.abocr.map.tile.TileComponent;
import net.matthiasauer.abocr.map.tile.TileRenderSystem;
import net.matthiasauer.abocr.map.tile.TileType;
import net.matthiasauer.abocr.map.unit.UnitComponent;
import net.matthiasauer.abocr.map.unit.UnitRenderSystem;
import net.matthiasauer.abocr.map.unit.UnitStrength;
import net.matthiasauer.abocr.map.unit.UnitType;

public class MapView extends ScreenAdapter {
	private final PooledEngine engine;
	private static final Random random = new Random();
	private final OrthographicCamera camera;
	private final InputMultiplexer inputMultiplexer;
	private final Viewport viewport;

	public MapView() {
		this.engine = new PooledEngine();
		this.camera = new OrthographicCamera(800, 600);
		this.viewport = new ScreenViewport(this.camera);

		this.inputMultiplexer = new InputMultiplexer();		
		Gdx.input.setInputProcessor(this.inputMultiplexer);
		
		this.createMap();
		this.createUnits();
		
		this.engine.addSystem(new RenderTextureArchiveSystem());

		this.engine.addSystem(new TileRenderSystem());
		this.engine.addSystem(new UnitRenderSystem());

		this.engine.addSystem(new InputTouchGeneratorSystem(this.inputMultiplexer, this.camera));
		this.engine.addSystem(new InputSimpleEventGenerator(this.inputMultiplexer));
		this.engine.addSystem(new ClickGeneratorSystem());
		this.engine.addSystem(new CameraZoomSystem(this.camera));
		this.engine.addSystem(new CameraSystem(this.camera));

		this.engine.addSystem(new RenderSystem(this.camera));
		
		//Gdx.app.setLogLevel(Gdx.app.LOG_ERROR);
	}
	
	private static final int xSize = 4;
	private static final int ySize = 3;
	private static final double unitChancePercentage = 25;
	
	private static <T> T choice(T ... elements) {
		int randomIndex = random.nextInt(elements.length);
		return
				elements[randomIndex];
	}
	
	private void createUnits() {
		
		for (int x = 0; x < xSize; x++) {
			for (int y = 0; y < ySize; y++) {
				if (random.nextInt(100) <= unitChancePercentage) {
					Entity unit =
							this.engine.createEntity();
					
					UnitComponent unitComponent =
							this.engine.createComponent(UnitComponent.class);
					unitComponent.x = x;
					unitComponent.y = y;
					unitComponent.type = choice(UnitType.values());
					unitComponent.strength = choice(UnitStrength.values());
					unitComponent.strengthUnit = this.engine.createEntity();
					
					unit.add(unitComponent);
					unit.add(new ClickableComponent());
					
					this.engine.addEntity(unit);
					this.engine.addEntity(unitComponent.strengthUnit);
				}
			}
		}
		
	}
	
	private void createMap() {		
		for (int x = 0; x < xSize; x++) {
			for (int y = 0; y < ySize; y++) {
				Entity tile =
						this.engine.createEntity();
				
				TileComponent tileComponent =
						this.engine.createComponent(TileComponent.class);
				tileComponent.x = x;
				tileComponent.y = y;
				tileComponent.tileType = choice(TileType.values());
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
