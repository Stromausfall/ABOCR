package net.matthiasauer.abocr.input.base.gestures;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.input.GestureDetector.GestureListener;
import com.badlogic.gdx.math.Vector2;

public class InputGestureEventGenerator extends EntitySystem implements GestureListener {
	private final InputMultiplexer inputMultiplexer;
	private final GestureDetector gestureDetector;
	private PooledEngine engine;
	private InputGestureEventComponent lastEvent;
	private Entity inputGestureEventContainerEntity;
	private Float lastDistance = null;
	private Float currentDistance = null;

	public InputGestureEventGenerator(InputMultiplexer inputMultiplexer) {
		this.lastEvent = null;
		this.inputMultiplexer = inputMultiplexer;
		this.gestureDetector =
				new GestureDetector(20, 0.5f, 2, 0.15f, this);
		
		this.inputMultiplexer.addProcessor(this.gestureDetector);
	}
		
	@Override
	public void update(float deltaTime) {
		// remove any previous event
		this.inputGestureEventContainerEntity.remove(InputGestureEventComponent.class);
		
		if (this.lastEvent != null) {
			this.inputGestureEventContainerEntity.add(this.lastEvent);
			this.lastEvent = null;
		}
		
		this.lastDistance = this.currentDistance;
		this.currentDistance = null;
	}
	
	@Override
	public void addedToEngine(Engine engine) {
		this.engine = (PooledEngine)engine;
		this.inputGestureEventContainerEntity = this.engine.createEntity();		
		this.engine.addEntity(this.inputGestureEventContainerEntity);

		super.addedToEngine(engine);
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
		Gdx.app.error("pan!",
		" x:" + x
		+ "| y:" + y
		+ "| deltaX:" + deltaX
		+ "| deltaY:" + deltaY);
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

		if ((this.lastDistance != null)
				&& (this.currentDistance != null)) {
			float zoomFactor = 0;
			
			if (this.currentDistance > this.lastDistance) {
				zoomFactor = -1;
			}
			if (this.currentDistance < this.lastDistance) {
				zoomFactor = 1;
			}

			this.lastEvent =
					this.engine.createComponent(InputGestureEventComponent.class).set(
							InputGestureEventType.Zoom, zoomFactor);
		}
		
		return false;
	}

	@Override
	public boolean pinch(Vector2 initialPointer1, Vector2 initialPointer2, Vector2 pointer1, Vector2 pointer2) {
		// TODO Auto-generated method stub
		return false;
	}
	

}
