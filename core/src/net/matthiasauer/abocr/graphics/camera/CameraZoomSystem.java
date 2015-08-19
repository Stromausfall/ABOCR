package net.matthiasauer.abocr.graphics.camera;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.OrthographicCamera;

import net.matthiasauer.abocr.input.base.simple.InputSimpleEventComponent;

public class CameraZoomSystem extends IteratingSystem {
	@SuppressWarnings("unchecked")
	private static final Family family =
			Family.all(InputSimpleEventComponent.class).get();
	private final OrthographicCamera camera;
	private final ComponentMapper<InputSimpleEventComponent> inputSimpleEventComponentMapper;
	
	public CameraZoomSystem(OrthographicCamera camera) {
		super(family);
		
		this.camera = camera;
		this.inputSimpleEventComponentMapper =
				ComponentMapper.getFor(InputSimpleEventComponent.class);
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		InputSimpleEventComponent inputSimpleEventComponent =
				this.inputSimpleEventComponentMapper.get(entity);

		this.camera.zoom += 0.25 * inputSimpleEventComponent.argument;
		this.camera.zoom = Math.max(0.05f, this.camera.zoom);
		this.camera.zoom = Math.min(2.0f, this.camera.zoom);
		this.camera.update();
	}
}
