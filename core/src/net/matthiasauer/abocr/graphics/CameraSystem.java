package net.matthiasauer.abocr.graphics;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.math.Vector2;

import net.matthiasauer.abocr.TextureLoader;
import net.matthiasauer.abocr.input.InputTouchEventComponent;
import net.matthiasauer.abocr.input.InputTouchGeneratorSystem;

public class CameraSystem extends EntitySystem {
	private final float cameraMoveSpeedPercentagePerSecond = 50;
	private final float borderCameraMovePercentage = 10;
	private final ComponentMapper<InputTouchEventComponent> inputTouchEventComponentMapper;
	private final ComponentMapper<RenderedComponent> renderedComponentMapper;
	private final ComponentMapper<RenderComponent> renderComponentMapper;
	private final OrthographicCamera camera;
	private final AtlasRegion arrow;
	private Entity inputTouchEntity;
	private Entity topArrow;
	private Entity bottomArrow;
	private Entity leftArrow;
	private Entity rightArrow;
	private PooledEngine engine;
	private ImmutableArray<Entity> renderedEntities;
	
	public CameraSystem(OrthographicCamera camera) {
		this.arrow = TextureLoader.getInstance().getTexture("screenMoveArrow");
		this.camera = camera;
		this.inputTouchEventComponentMapper =
				ComponentMapper.getFor(InputTouchEventComponent.class);
		this.renderComponentMapper =
				ComponentMapper.getFor(RenderComponent.class);
		this.renderedComponentMapper =
				ComponentMapper.getFor(RenderedComponent.class);
		
		this.camera.position.set(0, 0, 0);
		this.camera.update();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void addedToEngine(Engine engine) {
		this.engine = (PooledEngine)engine;
		this.renderedEntities =
				engine.getEntitiesFor(
						Family.all(RenderComponent.class, RenderedComponent.class).get());
		InputTouchGeneratorSystem inputTouchGeneratorSystem =
				engine.getSystem(InputTouchGeneratorSystem.class);
				
		this.inputTouchEntity =
				inputTouchGeneratorSystem.inputTouchContainerEntity;
		this.topArrow = this.engine.createEntity();
		this.bottomArrow = this.engine.createEntity();
		this.leftArrow = this.engine.createEntity();
		this.rightArrow = this.engine.createEntity();
		
		this.engine.addEntity(this.topArrow);
		this.engine.addEntity(this.bottomArrow);
		this.engine.addEntity(this.leftArrow);
		this.engine.addEntity(this.rightArrow);
	};
	
	private final Vector2 lastPosition = new Vector2();
	
	@Override
	public void update(float deltaTime) {
		InputTouchEventComponent inputTouchEvent =
				this.inputTouchEventComponentMapper.get(this.inputTouchEntity);
		
		if (inputTouchEvent != null) {
			lastPosition.set(inputTouchEvent.projectedPosition);
		}
		
		this.removeArrows();
		
		// if there is an event
		//if (inputTouchEvent != null) {		
		{
			Vector2 translateCamera = new Vector2();
			translateCamera.add(
					this.checkLeftBorder(lastPosition.x, deltaTime));
			translateCamera.add(
					this.checkRightBorder(lastPosition.x, deltaTime));
			translateCamera.add(
					this.checkBottomBorder(lastPosition.y, deltaTime));
			translateCamera.add(
					this.checkTopBorder(lastPosition.y, deltaTime));
			translateCamera.add(
					this.limitCameraPos());
			
			this.camera.translate(translateCamera);
			this.camera.update();
		}
	}
	
	private float getMoveSpeed(float deltaTime) {
		// get the longer of the two axis
		float maxAxis =
				Math.max(
						Gdx.graphics.getWidth(),
						Gdx.graphics.getHeight());
		float totalSpeed =
				(maxAxis * cameraMoveSpeedPercentagePerSecond) / 100;
		
		return totalSpeed * deltaTime;
	}
	
	private void removeArrows() {
		this.topArrow.remove(RenderComponent.class);
		this.bottomArrow.remove(RenderComponent.class);
		this.leftArrow.remove(RenderComponent.class);
		this.rightArrow.remove(RenderComponent.class);
	}
	
	private Vector2 checkLeftBorder(float actualX, float deltaTime) {
		double minX = 0;
		double maxX = (int)(Gdx.graphics.getWidth() * borderCameraMovePercentage / 100);
		
		if ((actualX >= minX) && (actualX <= maxX)) {
			this.leftArrow.add(
					this.engine.createComponent(RenderComponent.class).set(
							-Gdx.graphics.getWidth() / 2 + this.arrow.getRegionWidth() / 2,
							0,
							90,
							RenderPositionUnit.Pixels,
							this.arrow,
							RenderLayer.UI));
			
			return new Vector2(-this.getMoveSpeed(deltaTime), 0);
		}
		
		return new Vector2();
	}
	
	private Vector2 checkRightBorder(float actualX, float deltaTime) {
		double minX = (int)(Gdx.graphics.getWidth() * (100 - borderCameraMovePercentage) / 100);
		double maxX = Gdx.graphics.getWidth();
		
		if ((actualX >= minX) && (actualX <= maxX)) {			
			this.rightArrow.add(
					this.engine.createComponent(RenderComponent.class).set(
							Gdx.graphics.getWidth()/2 - this.arrow.getRegionWidth() / 2,
							0,
							270,
							RenderPositionUnit.Pixels,
							this.arrow,
							RenderLayer.UI));
			
			return new Vector2(this.getMoveSpeed(deltaTime), 0);
		}
		
		return new Vector2();
	}
	
	private Vector2 checkBottomBorder(float actualY, float deltaTime) {
		double minY = (int)(Gdx.graphics.getHeight() * (100 - borderCameraMovePercentage) / 100);
		double maxY = Gdx.graphics.getHeight();
		
		if ((actualY >= minY) && (actualY <= maxY)) {			
			this.bottomArrow.add(
					this.engine.createComponent(RenderComponent.class).set(
							0,
							- Gdx.graphics.getHeight()/2 + this.arrow.getRegionHeight() / 2,
							180,
							RenderPositionUnit.Pixels,
							this.arrow,
							RenderLayer.UI));
			
			return new Vector2(0, -this.getMoveSpeed(deltaTime));
		}
		
		return new Vector2();
	}
	
	private Vector2 checkTopBorder(float actualY, float deltaTime) {
		double minY = 0;
		double maxY = (int)(Gdx.graphics.getHeight() * borderCameraMovePercentage / 100);
		
		if ((actualY >= minY) && (actualY <= maxY)) {
			this.topArrow.add(
					this.engine.createComponent(RenderComponent.class).set(
							0,
							Gdx.graphics.getHeight()/2 - this.arrow.getRegionHeight() / 2,
							0,
							RenderPositionUnit.Pixels,
							this.arrow,
							RenderLayer.UI));
			
			return new Vector2(0, this.getMoveSpeed(deltaTime));
		}
		
		return new Vector2();
	}
	
	private Vector2 limitCameraPos() {
		double minX = Double.MAX_VALUE;
		double minY = Double.MAX_VALUE;
		double maxX = Double.MIN_VALUE;
		double maxY = Double.MIN_VALUE;
		double collectedEntities = 0;
		Vector2 translateCamera = new Vector2();
		
		// get values
		for (Entity entity : this.renderedEntities) {
			RenderComponent renderComponent =
					this.renderComponentMapper.get(entity);
			RenderedComponent renderedComponent =
					this.renderedComponentMapper.get(entity);
			
			// it only makes sense to collect the positions of projected
			// textures !
			if (renderComponent.layer.projected == true) {				
				minX = Math.min(minX, renderedComponent.renderedTarget.x);
				minY = Math.min(minY, renderedComponent.renderedTarget.y);
	
				maxX = Math.max(maxX, renderedComponent.renderedTarget.x + renderComponent.texture.getRegionWidth());
				maxY = Math.max(maxY, renderedComponent.renderedTarget.y + renderComponent.texture.getRegionHeight());
				
				collectedEntities += 1;
			}
		}
		
		// if there is no entity to render - then the min and max values
		// would be too large/too little - hence cover that case
		// otherwise we can get a bug when rendering starts
		if (collectedEntities == 0) {
			minX = -10;
			minY = -10;
			maxX = 10;
			maxY = 10;
		}
		
		if (this.camera.position.x > maxX) {
			translateCamera.add(
					new Vector2(
							- (float)(this.camera.position.x - maxX),
							0));
		}
		if (this.camera.position.y > maxY) {
			translateCamera.add(
					new Vector2(
							0,
							- (float)(this.camera.position.y - maxY)));
		}
		if (this.camera.position.x < minX) {
			translateCamera.add(
					new Vector2(
							- (float)(this.camera.position.x - minX),
							0));
		}
		if (this.camera.position.y < minY) {
			translateCamera.add(
					new Vector2(
							0,
							- (float)(this.camera.position.y - minY)));
		}
		
		return translateCamera;
	}
}
