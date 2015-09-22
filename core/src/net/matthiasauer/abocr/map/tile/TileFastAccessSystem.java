package net.matthiasauer.abocr.map.tile;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.math.Vector2;

import net.matthiasauer.abocr.map.player.MapElementOwnerComponent;
import net.matthiasauer.abocr.map.player.Player;
import net.matthiasauer.abocr.utils.Mappers;

public class TileFastAccessSystem extends EntitySystem {
	@SuppressWarnings("unchecked")
	private static final Family tileComponentFamily =
			Family.all(
					TileComponent.class).get();
	private ComponentMapper<TileComponent> tileComponentMapper;
	private ImmutableArray<Entity> tileEntities;
	private PooledEngine pooledEngine;
	private final Map<Vector2, Entity> fastAccessTiles =
			new HashMap<Vector2, Entity>();
	private final Map<Player, Set<Vector2>> ownedTiles =
			new HashMap<Player, Set<Vector2>>();
	
	@Override
	public void addedToEngine(Engine engine) {
		super.addedToEngine(engine);
		
		this.pooledEngine = (PooledEngine) engine;
		this.tileEntities =
				this.pooledEngine.getEntitiesFor(tileComponentFamily);
		this.tileComponentMapper =
				ComponentMapper.getFor(TileComponent.class);
		
		for (Player player : Player.values()) {
			this.ownedTiles.put(player, new HashSet<Vector2>());
		}
	}
	
	@Override
	public void update(float deltaTime) {
		super.update(deltaTime);
		
		this.fastAccessTiles.clear();
		
		for (Set<Vector2> values : this.ownedTiles.values()) {
			values.clear();
		}
		
		for (Entity tileEntity : this.tileEntities) {
			TileComponent tileComponent =
					this.tileComponentMapper.get(tileEntity);
			MapElementOwnerComponent mapOwner =
					Mappers.mapElementOwnerComponent.get(tileEntity);
			
			this.fastAccessTiles.put(
					new Vector2(
							tileComponent.x,
							tileComponent.y),
					tileEntity);
			
			if (mapOwner != null) {
				// only if it is owned by someone !
				this.ownedTiles.get(mapOwner.owner).add(
						new Vector2(
								(int) tileComponent.x,
								(int) tileComponent.y));
			}
		}
	}
	
	public Set<Vector2> getOwnedTiles(Player player) {
		return Collections.unmodifiableSet(this.ownedTiles.get(player));
	}
	
	public TileComponent getTileComponent(Entity entity) {
		return this.tileComponentMapper.get(entity);
	}
	
	public Entity getTile(Vector2 position) {
		return this.getTile(
				(int) position.x,
				(int) position.y);
	}
	
	public Entity getTile(int x, int y) {
		Vector2 key =
				new Vector2(x, y);
		
		return this.fastAccessTiles.get(key);
	}
	
	public Collection<Entity> getSurroundingTiles(Vector2 position) {
		return this.getSurroundingTiles(
				(int)position.x,
				(int)position.y);
	}
	
	public Collection<Entity> getSurroundingTiles(int x, int y) {
		Collection<Entity> surrounding =
				new ArrayList<Entity>();
		
		this.addTileEntityToCollection(x, y+1, surrounding);
		this.addTileEntityToCollection(x, y-1, surrounding);
		this.addTileEntityToCollection(x-1, y, surrounding);
		this.addTileEntityToCollection(x+1, y, surrounding);
		
		if ((y % 2) == 1) {
			this.addTileEntityToCollection(x+1, y+1, surrounding);
			this.addTileEntityToCollection(x+1, y-1, surrounding);
		} else {
			this.addTileEntityToCollection(x-1, y-1, surrounding);
			this.addTileEntityToCollection(x-1, y+1, surrounding);
		}
		
		return surrounding;
	}
	
	private void addTileEntityToCollection(int x, int y, Collection<Entity> entities) {
		Entity entity = this.getTile(x, y);
		
		if (entity != null) {
			entities.add(entity);
		}
	}
}
