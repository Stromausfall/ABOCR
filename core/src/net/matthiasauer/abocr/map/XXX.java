package net.matthiasauer.abocr.map;

import java.awt.Color;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;

import net.matthiasauer.ecstools.graphics.RenderComponent;
import net.matthiasauer.ecstools.graphics.RenderLayer;
import net.matthiasauer.ecstools.graphics.RenderPositionUnit;
import net.matthiasauer.ecstools.input.base.touch.InputTouchEventComponent;
import net.matthiasauer.ecstools.input.base.touch.InputTouchEventType;

public class XXX extends IteratingSystem implements InputProcessor {
private final InputMultiplexer inputMultiplexer;
	public XXX(InputMultiplexer inputMultiplexer) {
		super(
				Family.all(InputTouchEventComponent.class).get());
		// TODO Auto-generated constructor stub
		this.inputMultiplexer = inputMultiplexer;
	}
	private Entity entity1;
	private Entity entity2;
	@Override
	public void addedToEngine(Engine engine) {
		super.addedToEngine(engine);
		

		this.inputMultiplexer.addProcessor(this);
		
		
		
		this.entity1 = ((PooledEngine)engine).createEntity();
		engine.addEntity(this.entity1);
		
		this.entity2 = ((PooledEngine)engine).createEntity();
		engine.addEntity(this.entity2);
	}
int counter = 0;
	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		InputTouchEventComponent xxx =
				entity.getComponent(InputTouchEventComponent.class);
		
		System.err.println(
				xxx.projectedPosition.x + ":" + xxx.projectedPosition.y + " - " +
						xxx.unprojectedPosition.x + ":" + xxx.unprojectedPosition.y);
		if (xxx.inputType == InputTouchEventType.TouchUp) {
			counter++;
		}
		
		this.max = Math.max(this.max, this.count);
		this.count = 0;
		
		RenderComponent xx1=
				new RenderComponent().setText(
						0f, 0f, 0f, RenderPositionUnit.Pixels, RenderLayer.UI, this.max + " - " + Gdx.input.isTouched() + " : " + counter + " - " + xxx.inputType, null, com.badlogic.gdx.graphics.Color.WHITE);

		RenderComponent xx2=
				new RenderComponent().setText(
						0f, -150f, 0f, RenderPositionUnit.Pixels, RenderLayer.UI, "Bug = only one of the up to many many events is handled - checkall input processors in ecstools... !", null, com.badlogic.gdx.graphics.Color.WHITE);
		
		this.entity1.add(xx1);
		this.entity2.add(xx2);
	}
	
	int count = 0;
	int max = 0;
	
	
	@Override
	public boolean keyDown(int keycode) {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public boolean keyUp(int keycode) {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public boolean keyTyped(char character) {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		// TODO Auto-generated method stub
		this.count++;
		return false;
	}
	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		// TODO Auto-generated method stub
		this.count++;
		return false;
	}
	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		// TODO Auto-generated method stub
		this.count++;
		return false;
	}
	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		// TODO Auto-generated method stub
		this.count++;
		return false;
	}
	@Override
	public boolean scrolled(int amount) {
		// TODO Auto-generated method stub
		return false;
	}

}
