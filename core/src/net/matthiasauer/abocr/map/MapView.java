package net.matthiasauer.abocr.map;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Random;
import java.util.Set;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import net.matthiasauer.abocr.map.income.CalculateIncomeSystem;
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
import net.matthiasauer.abocr.map.unit.movement.MovementSystem;
import net.matthiasauer.abocr.map.unit.range.RangeSystem;
import net.matthiasauer.abocr.utils.Mappers;
import net.matthiasauer.abocr.utils.Systems;
import net.matthiasauer.ecstools.graphics.RenderSystem;
import net.matthiasauer.ecstools.graphics.camera.move.CameraMoveSystem;
import net.matthiasauer.ecstools.graphics.camera.move.InputGestureToMoveEventSystem;
import net.matthiasauer.ecstools.graphics.camera.zoom.CameraZoomSystem;
import net.matthiasauer.ecstools.graphics.camera.zoom.InputGestureToZoomEventSystem;
import net.matthiasauer.ecstools.graphics.camera.zoom.InputSimpleToZoomEventSystem;
import net.matthiasauer.ecstools.graphics.texture.archive.RenderTextureArchiveSystem;
import net.matthiasauer.ecstools.input.base.gestures.InputGestureEventGenerator;
import net.matthiasauer.ecstools.input.base.simple.InputSimpleEventGenerator;
import net.matthiasauer.ecstools.input.base.touch.InputTouchGeneratorSystem;
import net.matthiasauer.ecstools.input.click.ClickGeneratorSystem;
import net.matthiasauer.ecstools.input.click.ClickableComponent;

public class MapView extends ScreenAdapter {
	private final PooledEngine engine;
	private final Random random;
	private final OrthographicCamera camera;
	private final InputMultiplexer inputMultiplexer;
	private final Viewport viewport;
	
	public MapView() {
		Random xxx = new Random();
		long seed = xxx.nextLong();
		//seed = -954972457995969242L;
		this.random = new Random(seed);
		System.err.println("seed : " + seed);
		
		
		this.engine = new PooledEngine();
		this.camera = new OrthographicCamera(800, 600);
		this.viewport = new ScreenViewport(this.camera);

		this.inputMultiplexer = new InputMultiplexer();		
		Gdx.input.setInputProcessor(this.inputMultiplexer);
		
		this.createMap(this.engine);
		
		
		this.engine.addSystem(new PlayerManagementSystem());
		this.engine.addSystem(new SupplySystem());
		this.engine.addSystem(new CalculateIncomeSystem());
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
		
		this.engine.addSystem(new MovementSystem());
		
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
	
	@SuppressWarnings("unchecked")
	private <T> T choice(Collection<T> elements) {
		return (T)choice(elements.toArray());
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
	
	private void createCities(int x, int y, Player owner, Entity tileEntity, boolean useRandom) {
		boolean createAccordingToRandom =
				random.nextInt(100) <= cityChancePercentage;	
		
		if (createAccordingToRandom || !useRandom) {
			CityComponent cityComponent =
					this.engine.createComponent(CityComponent.class);
			cityComponent.x = x;
			cityComponent.y = y;
			cityComponent.type = choice(CityType.values());
			
			tileEntity.add(cityComponent);
		}
	}
	
	private void createMap(PooledEngine engine) {
		for (int x = 0; x < xSize; x++) {
			for (int y = 0; y < ySize; y++) {
				Entity tile =
						this.engine.createEntity();
				
				TileComponent tileComponent =
						this.engine.createComponent(TileComponent.class);
				tileComponent.x = x;
				tileComponent.y = y;
				tileComponent.receivesInput = true;

				tile.add(tileComponent);
				tile.add(new ClickableComponent());
				
				this.engine.addEntity(tile);
			}
		}
		
		TileFastAccessSystem tileFastAccess = new TileFastAccessSystem();
		this.engine.addSystem(tileFastAccess);
		this.engine.update(Float.MIN_VALUE);
				
		// first spread the tile type
		for (int x = 0; x < xSize; x++) {
			for (int y = 0; y < ySize; y++) {
				Entity tile = tileFastAccess.getTile(x, y);
				
				if (tile == null) {
					System.out.println("2422");
				}
				
				TileComponent tileComponent = tileFastAccess.getTileComponent(tile);

				tileComponent.tileType = choice(TileType.values());
			}
		}
		

		// first spread the player
		// the neutral player will have the largest chunk according to the algorithm
		Map<Player, Vector2> startPositions =
				this.createStartPositions(tileFastAccess, xSize, ySize);
		Queue<Vector2> allTiles =
				this.createAllTilesWithoutStart(startPositions, xSize, ySize);
		
		while (!allTiles.isEmpty()) {
			Vector2 tilePos = allTiles.poll();
			Entity tileEntity = tileFastAccess.getTile((int)tilePos.x, (int)tilePos.y);
			TileComponent tileComponent = tileFastAccess.getTileComponent(tileEntity);
				
			// if it is not traversable then the owner has to be the neutral player !
			if (!tileComponent.tileType.traversable) {
				tileEntity.add(new MapElementOwnerComponent().set(Player.Neutral, false));
			} else {
				// get all possible players (a possible player has to own an adjacent tile
				Set<Player> surroundingPlayers =
						new HashSet<Player>();
				
				for (Entity surroundingEntity : tileFastAccess.getSurroundingTiles(tilePos)) {
					MapElementOwnerComponent owner =
							Mappers.mapElementOwnerComponent.get(surroundingEntity);
					
					if (owner != null) {
						surroundingPlayers.add(owner.owner);
					}
				}
				
				// add the neutral player
				surroundingPlayers.add(Player.Neutral);
				
				Player owner = choice(surroundingPlayers);
				
				tileEntity.add(new MapElementOwnerComponent().set(owner, false));
				
				this.createCities((int)tilePos.x, (int)tilePos.y, owner, tileEntity, true);
				this.createUnits((int)tilePos.x, (int)tilePos.y, owner);
			}
		}
		
		this.engine.removeSystem(tileFastAccess);
	}
	
	private Queue<Vector2> createAllTilesWithoutStart(
			Map<Player, Vector2> startPositions,
			int xSize,
			int ySize) {
		Queue<Vector2> tiles =
				new LinkedList<Vector2>();
		Set<Vector2> startTiles =
				new HashSet<Vector2>(startPositions.values());
		
		for (int x = 0; x < xSize; x++) {
			for (int y = 0; y < ySize; y++) {
				Vector2 position =
						new Vector2(x, y);
				
				if (!startTiles.contains(position)) {
					tiles.add(position);
				}
			}
		}
		
		return tiles;
	}

	private Map<Player, Vector2> createStartPositions(
			TileFastAccessSystem tileFastAccess,
			int maxX,
			int maxY) {
		Map<Player, Vector2> startPositions = 
				new HashMap<Player, Vector2>();
		Set<Vector2> alreadyUsed =
				new HashSet<Vector2>();
		
		for (Player player : Player.values()) {
			Vector2 startPosition = null;
			boolean validStart = false;
			Entity tileEntity = null;
			
			
			
			// find a start position not already taken
			do {
				startPosition =
						new Vector2(
								random.nextInt(maxX),
								random.nextInt(maxY));
				
				tileEntity = tileFastAccess.getTile(startPosition);
				TileComponent tileComponent =
						tileFastAccess.getTileComponent(tileEntity);
				
				// make sure the start tile is also traversable
				validStart = tileComponent.tileType.traversable;
			} while (alreadyUsed.contains(startPosition) || !validStart);

			alreadyUsed.add(startPosition);
			tileEntity.add(new MapElementOwnerComponent().set(player, false));
			
			// create cities there
			this.createCities((int)startPosition.x, (int)startPosition.y, player, tileEntity, false);
							
			startPositions.put(player, startPosition);
		}
		
		return startPositions;
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
