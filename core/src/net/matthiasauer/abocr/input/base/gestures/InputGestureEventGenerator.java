package net.matthiasauer.abocr.input.base.gestures;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.input.GestureDetector.GestureListener;
import com.badlogic.gdx.math.Vector2;

public class InputGestureEventGenerator extends EntitySystem implements GestureListener {
	private final InputMultiplexer inputMultiplexer;
	private final GestureDetector gestureDetector;
	private final OrthographicCamera camera;
	private PooledEngine engine;
	private InputGestureEventComponent lastEvent;
	private Entity inputSimpleEventContainerEntity;
	private Float lastDistance = null;
	private Float currentDistance = null;

	public InputGestureEventGenerator(InputMultiplexer inputMultiplexer, OrthographicCamera camera) {
		this.lastEvent = null;
		this.inputMultiplexer = inputMultiplexer;
		this.camera = camera;
		this.gestureDetector =
				new GestureDetector(20, 0.5f, 2, 0.15f, this);
		
		this.inputMultiplexer.addProcessor(this.gestureDetector);
	}
		
	@Override
	public void update(float deltaTime) {
		// remove any previous event
		this.inputSimpleEventContainerEntity.remove(InputGestureEventComponent.class);
		
		if (this.lastEvent != null) {
			this.inputSimpleEventContainerEntity.add(this.lastEvent);
			this.lastEvent = null;
		}

		if ((this.lastDistance != null)
				&& (this.currentDistance != null)) {
			if (this.currentDistance > this.lastDistance) {
				this.camera.zoom -= 0.01;
			}
			if (this.currentDistance < this.lastDistance) {
				this.camera.zoom += 0.01;
			}

			this.camera.zoom =
					Math.max(0.05f, this.camera.zoom);
			this.camera.zoom =
					Math.min(2.0f, this.camera.zoom);
			this.camera.update();
		}
		
		this.lastDistance = this.currentDistance;
		this.currentDistance = null;
	}
	
	@Override
	public void addedToEngine(Engine engine) {
		this.engine = (PooledEngine)engine;
		this.inputSimpleEventContainerEntity = this.engine.createEntity();		
		this.engine.addEntity(this.inputSimpleEventContainerEntity);

		super.addedToEngine(engine);
	}
	
	private void saveEvent(InputGestureEventComponent inputType, int argument) {
		/*
		this.lastEvent =
				this.engine.createComponent(InputSimpleEventComponent.class).set(
						inputType,
						System.currentTimeMillis(),
						argument);*/
	}

	@Override
	public void removedFromEngine(Engine engine) {
		this.inputMultiplexer.removeProcessor(this.gestureDetector);
		
		super.removedFromEngine(engine);
	}

	@Override
	public boolean touchDown(float x, float y, int pointer, int button) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean tap(float x, float y, int count, int button) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean longPress(float x, float y) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean fling(float velocityX, float velocityY, int button) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean pan(float x, float y, float deltaX, float deltaY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean panStop(float x, float y, int pointer, int button) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean zoom(float initialDistance, float distance) {
		this.currentDistance = distance;
		return false;
	}

	@Override
	public boolean pinch(Vector2 initialPointer1, Vector2 initialPointer2, Vector2 pointer1, Vector2 pointer2) {
		// TODO Auto-generated method stub
		return false;
	}
	

}
