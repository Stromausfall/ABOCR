package net.matthiasauer.abocr.utils;

import com.badlogic.ashley.core.ComponentMapper;

import net.matthiasauer.abocr.input.click.ClickedComponent;
import net.matthiasauer.abocr.map.player.ActivePlayerComponent;
import net.matthiasauer.abocr.map.player.MapElementOwnerComponent;
import net.matthiasauer.abocr.map.supply.CityComponent;
import net.matthiasauer.abocr.map.tile.TileComponent;
import net.matthiasauer.abocr.map.unit.UnitComponent;
import net.matthiasauer.abocr.map.unit.range.TargetComponent;

public class Mappers {
	public static final ComponentMapper<UnitComponent> unitComponent = 
			ComponentMapper.getFor(UnitComponent.class);
	public static final ComponentMapper<CityComponent> cityComponent =
			ComponentMapper.getFor(CityComponent.class);
	public static final ComponentMapper<MapElementOwnerComponent> mapElementOwnerComponent =
			ComponentMapper.getFor(MapElementOwnerComponent.class);
	public static final ComponentMapper<ActivePlayerComponent> activePlayerComponent =
			ComponentMapper.getFor(ActivePlayerComponent.class);
	public static final ComponentMapper<TileComponent> tileComponent =
			ComponentMapper.getFor(TileComponent.class);
	public static final ComponentMapper<ClickedComponent> clickedComponent =
			ComponentMapper.getFor(ClickedComponent.class);
	public static final ComponentMapper<TargetComponent> targetComponent =
			ComponentMapper.getFor(TargetComponent.class);
	
	private Mappers() {
	}
}
