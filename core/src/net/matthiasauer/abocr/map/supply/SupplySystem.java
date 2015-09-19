package net.matthiasauer.abocr.map.supply;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.math.Vector2;

import net.matthiasauer.abocr.map.player.MapElementOwnerComponent;
import net.matthiasauer.abocr.map.player.Player;
import net.matthiasauer.abocr.map.tile.TileComponent;
import net.matthiasauer.abocr.map.tile.TileFastAccessSystem;
import net.matthiasauer.abocr.map.unit.UnitComponent;
import net.matthiasauer.abocr.utils.ILateInitialization;
import net.matthiasauer.abocr.utils.Mappers;
import net.matthiasauer.abocr.utils.Systems;

public class SupplySystem extends IteratingSystem implements ILateInitialization {
	@SuppressWarnings("unchecked")
	private static final Family unitFamily =
			Family.all(UnitComponent.class).get();
	@SuppressWarnings("unchecked")
	private static final Family nextTurnFamily =
			Family.all(NextTurnComponent.class).get();
	@SuppressWarnings("unchecked")
	private static final Family supplyUnitsFamily =
			Family.all(CityComponent.class).get();
	private final Map<Player, Set<Vector2>> supplied;
	private ImmutableArray<Entity> unitEntities;
	private ImmutableArray<Entity> supplyUnits;
	private TileFastAccessSystem tileFastAccess;
	private PooledEngine pooledEngine;
	private Set<Vector2> suppliedByPlayer;
	private Queue<Vector2> workQueue;

	public SupplySystem() {
		super(nextTurnFamily);
		
		this.supplied = new HashMap<Player, Set<Vector2>>();
		this.workQueue = new LinkedList<Vector2>();
		
		for (Player player : Player.values()) {
			this.supplied.put(player, new HashSet<Vector2>());
		}
	}
	
	@Override
	public void addedToEngine(Engine engine) {
		super.addedToEngine(engine);
		
		this.unitEntities = engine.getEntitiesFor(unitFamily);
		this.supplyUnits = engine.getEntitiesFor(supplyUnitsFamily);
	}

	@Override
	public void lateInitialization(Systems systems) {
		this.tileFastAccess = systems.tileFastAccess;
		this.pooledEngine = (PooledEngine) this.getEngine();
	}
	
	private void clearSupplied() {
		for (Set<Vector2> suppliedForPlayer : this.supplied.values()) {
			suppliedForPlayer.clear();
		}
	}
	
	private void addSupplyUnits() {
		for (Entity supplyUnit : this.supplyUnits) {
			CityComponent cityComponent =
					Mappers.cityComponent.get(supplyUnit);
			MapElementOwnerComponent owner =
					Mappers.mapElementOwnerComponent.get(supplyUnit);
			
			this.supplied.get(owner.owner).add(
					new Vector2(cityComponent.x, cityComponent.y));
		}
	}
	
	private void tileIsSupplied(Entity surroundingTileEntity) {
		TileComponent tile =
				Mappers.tileComponent.get(surroundingTileEntity);
		Vector2 surroundingTilePos =
				new Vector2(tile.x, tile.y);
		
		if (!this.suppliedByPlayer.contains(surroundingTilePos)) {
			this.suppliedByPlayer.add(surroundingTilePos);
			this.workQueue.add(surroundingTilePos);
		}
	}
	
	private void handleTile(Vector2 element, Player player) {
		Collection<Entity> surroundingTileEntities =
				this.tileFastAccess.getSurroundingTiles(element);
		
		for (Entity surroundingTileEntity : surroundingTileEntities) {
			MapElementOwnerComponent tileOwner =
					Mappers.mapElementOwnerComponent.get(surroundingTileEntity);
			
			if (tileOwner.owner == player) {
				this.tileIsSupplied(surroundingTileEntity);
			}
		}
	}
	
	private void expandUponSupplyUnits() {
		for (Player player : this.supplied.keySet()) {
			this.suppliedByPlayer =
					this.supplied.get(player);
			this.workQueue.clear();
			this.workQueue.addAll(this.suppliedByPlayer);
			
			while (!workQueue.isEmpty()) {
				Vector2 element = workQueue.poll();
				
				this.handleTile(element, player);
			}
		}
	}
	
	@Override
	public void update(float deltaTime) {
		super.update(deltaTime);
	}
	
	private boolean isSupplied(Entity unitEntity, UnitComponent unit) {
		MapElementOwnerComponent unitEntityOwner =
				Mappers.mapElementOwnerComponent.get(unitEntity);
		Set<Vector2> suppliedPositions =
				this.supplied.get(unitEntityOwner.owner);
		Vector2 unitPosition =
				new Vector2(unit.x, unit.y);
		
		return suppliedPositions.contains(unitPosition);
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		// this will only be triggered once there is a NextTurn element		
		this.clearSupplied();
		this.addSupplyUnits();
		this.expandUponSupplyUnits();
		
		for (Entity unitEntity : this.unitEntities) {
			UnitComponent unit =
					Mappers.unitComponent.get(unitEntity);
			unitEntity.remove(UnsuppliedComponent.class);
			
			// only get movement if supplied
			if (this.isSupplied(unitEntity, unit)) {
				unit.movement = unit.type.maxMovement;
			} else {
				unit.movement = 0;
				unitEntity.add(
						this.pooledEngine.createComponent(UnsuppliedComponent.class));
			}
		}
	}
}
