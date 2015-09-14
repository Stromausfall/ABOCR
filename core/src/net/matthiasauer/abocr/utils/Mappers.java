package net.matthiasauer.abocr.utils;

import com.badlogic.ashley.core.ComponentMapper;

import net.matthiasauer.abocr.input.click.ClickedComponent;
import net.matthiasauer.abocr.map.owner.ActivePlayerComponent;
import net.matthiasauer.abocr.map.owner.MapElementOwnerComponent;
import net.matthiasauer.abocr.map.tile.TileComponent;
import net.matthiasauer.abocr.map.unit.UnitComponent;

public class Mappers {
	public static final ComponentMapper<UnitComponent> unitComponent = 
			ComponentMapper.getFor(UnitComponent.class);
	public static final ComponentMapper<MapElementOwnerComponent> mapElementOwnerComponent =
			ComponentMapper.getFor(MapElementOwnerComponent.class);
	public static final ComponentMapper<ActivePlayerComponent> activePlayerComponent =
			ComponentMapper.getFor(ActivePlayerComponent.class);
	public static final ComponentMapper<TileComponent> tileComponent =
			ComponentMapper.getFor(TileComponent.class);
	public static final ComponentMapper<ClickedComponent> clickedComponent =
			ComponentMapper.getFor(ClickedComponent.class);
	
	private Mappers() {
	}
}
