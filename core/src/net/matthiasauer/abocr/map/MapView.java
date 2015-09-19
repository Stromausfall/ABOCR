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
import net.matthiasauer.abocr.graphics.camera.move.CameraMoveSystem;
import net.matthiasauer.abocr.graphics.camera.move.InputGestureToMoveEventSystem;
import net.matthiasauer.abocr.graphics.camera.zoom.CameraZoomSystem;
import net.matthiasauer.abocr.graphics.camera.zoom.InputGestureToZoomEventSystem;
import net.matthiasauer.abocr.graphics.camera.zoom.InputSimpleToZoomEventSystem;
import net.matthiasauer.abocr.graphics.texture.archive.RenderTextureArchiveSystem;
import net.matthiasauer.abocr.input.click.ClickableComponent;
import net.matthiasauer.abocr.input.base.gestures.InputGestureEventGenerator;
import net.matthiasauer.abocr.input.base.simple.InputSimpleEventGenerator;
import net.matthiasauer.abocr.input.base.touch.InputTouchGeneratorSystem;
import net.matthiasauer.abocr.input.click.ClickGeneratorSystem;
import net.matthiasauer.abocr.map.owner.player.AIPlayerSystem;
import net.matthiasauer.abocr.map.owner.player.NeutralPlayerSystem;
import net.matthiasauer.abocr.map.player.MapElementOwnerComponent;
import net.matthiasauer.abocr.map.player.Player;
import net.matthiasauer.abocr.map.player.PlayerManagementSystem;
import net.matthiasauer.abocr.map.supply.CityComponent;
import net.matthiasauer.abocr.map.supply.CityRenderSystem;
import net.matthiasauer.abocr.map.supply.CityType;
import net.matthiasauer.abocr.map.supply.SupplySystem;
import net.matthiasauer.abocr.map.supply.UnsuppliedRenderSystem;
import net.matthiasauer.abocr.map.tile.TileComponent;
import net.matthiasauer.abocr.map.tile.TileFastAccessSystem;
import net.matthiasauer.abocr.map.tile.TileOwnerRenderSystem;
import net.matthiasauer.abocr.map.tile.TileRenderSystem;
import net.matthiasauer.abocr.map.tile.TileType;
import net.matthiasauer.abocr.map.ui.NextTurnButtonSystem;
import net.matthiasauer.abocr.map.unit.UnitComponent;
import net.matthiasauer.abocr.map.unit.UnitFastAccessSystem;
import net.matthiasauer.abocr.map.unit.UnitRenderSystem;
import net.matthiasauer.abocr.map.unit.UnitStrength;
import net.matthiasauer.abocr.map.unit.UnitType;
import net.matthiasauer.abocr.map.unit.interaction.select.UnitSelectionMovementOriginRenderSystem;
import net.matthiasauer.abocr.map.unit.interaction.select.UnitSelectionMovementSystem;
import net.matthiasauer.abocr.map.unit.interaction.select.UnitSelectionMovementTargetRenderSystem;
import net.matthiasauer.abocr.map.unit.interaction.select.UnitSelectionSystem;
import net.matthiasauer.abocr.map.unit.range.RangeSystem;
import net.matthiasauer.abocr.utils.Systems;

public class MapView extends ScreenAdapter {
	private final PooledEngine engine;
	private final Random random;
	private final OrthographicCamera camera;
	private final InputMultiplexer inputMultiplexer;
	private final Viewport viewport;
	
	public MapView() {
		Random xxx = new Random();
		long seed = xxx.nextLong();
		this.random = new Random(seed);
		System.err.println("seed : " + seed);
		
		
		this.engine = new PooledEngine();
		this.camera = new OrthographicCamera(800, 600);
		this.viewport = new ScreenViewport(this.camera);

		this.inputMultiplexer = new InputMultiplexer();		
		Gdx.input.setInputProcessor(this.inputMultiplexer);
		
		this.createMap();
		
		this.engine.addSystem(new PlayerManagementSystem());
		this.engine.addSystem(new SupplySystem());
		this.engine.addSystem(new UnsuppliedRenderSystem());
		
		
		this.engine.addSystem(new NeutralPlayerSystem());
		this.engine.addSystem(new AIPlayerSystem());
		
		this.engine.addSystem(new TileFastAccessSystem());
		this.engine.addSystem(new UnitFastAccessSystem());
		
		this.engine.addSystem(new RenderTextureArchiveSystem());

		this.engine.addSystem(new TileOwnerRenderSystem());
		this.engine.addSystem(new TileRenderSystem());
		this.engine.addSystem(new UnitRenderSystem());
		this.engine.addSystem(new CityRenderSystem());

		this.engine.addSystem(new InputGestureEventGenerator(this.inputMultiplexer));
		this.engine.addSystem(new InputTouchGeneratorSystem(this.inputMultiplexer, this.camera));
		
		this.engine.addSystem(new InputSimpleEventGenerator(this.inputMultiplexer));
		
		this.engine.addSystem(new ClickGeneratorSystem());
		
		
		
		
		
		this.engine.addSystem(new UnitSelectionSystem());
		this.engine.addSystem(new UnitSelectionMovementSystem());
		

		this.engine.addSystem(new RangeSystem());
		
		this.engine.addSystem(new UnitSelectionMovementOriginRenderSystem());
		this.engine.addSystem(new UnitSelectionMovementTargetRenderSystem());
		
		
		
		
		
		this.engine.addSystem(new InputGestureToZoomEventSystem());
		this.engine.addSystem(new InputSimpleToZoomEventSystem());
		
		this.engine.addSystem(new CameraZoomSystem(this.camera));
		
		this.engine.addSystem(new InputGestureToMoveEventSystem());
		
		this.engine.addSystem(new CameraMoveSystem(this.camera));
		



		this.engine.addSystem(new NextTurnButtonSystem());
		
		this.engine.addSystem(new RenderSystem(this.camera));
		
		this.engine.addSystem(new Systems(this.engine));
		//Gdx.app.setLogLevel(Gdx.app.LOG_ERROR);
	}
	
	private static final int xSize = 8;
	private static final int ySize = 8;
	private static final double unitChancePercentage = 25;
	private static final double cityChancePercentage = 15;
	
	private <T> T choice(T ... elements) {
		int randomIndex = random.nextInt(elements.length);
		return
				elements[randomIndex];
	}
	
	private void createUnits(int x, int y, Player owner) {
		if (random.nextInt(100) <= unitChancePercentage) {
			Entity unit =
					this.engine.createEntity();
			
			UnitComponent unitComponent =
					this.engine.createComponent(UnitComponent.class);
			unitComponent.x = x;
			unitComponent.y = y;
			unitComponent.type = choice(UnitType.values());
			unitComponent.strength = choice(UnitStrength.values());
			unitComponent.movement = 1 + random.nextInt(3);
			
			unit.add(unitComponent);			
			unit.add(new MapElementOwnerComponent().set(owner, false));
			
			this.engine.addEntity(unit);
		}
	}
	
	private void createCities(int x, int y, Player owner) {
		if (random.nextInt(100) <= cityChancePercentage) {
			Entity unit =
					this.engine.createEntity();
			
			CityComponent cityComponent =
					this.engine.createComponent(CityComponent.class);
			cityComponent.x = x;
			cityComponent.y = y;
			cityComponent.type = choice(CityType.values());
			
			unit.add(cityComponent);			
			unit.add(new MapElementOwnerComponent().set(owner, false));
			
			this.engine.addEntity(unit);
		}
	}
	
	private void createMap() {		
		for (int x = 0; x < xSize; x++) {
			for (int y = 0; y < ySize; y++) {
				Entity tile =
						this.engine.createEntity();
				Player owner = choice(Player.values());
				
				TileComponent tileComponent =
						this.engine.createComponent(TileComponent.class);
				tileComponent.x = x;
				tileComponent.y = y;
				tileComponent.tileType = choice(TileType.values());
				tileComponent.receivesInput = true;

				tile.add(tileComponent);
				tile.add(new ClickableComponent());
				tile.add(new MapElementOwnerComponent().set(owner, false));
				
				this.createUnits(x, y, owner);
				this.createCities(x, y, owner);
				
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
