package net.matthiasauer.abocr.input;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import net.matthiasauer.abocr.graphics.RenderComponent;
import net.matthiasauer.abocr.graphics.RenderPositionUnitTranslator;
import net.matthiasauer.abocr.graphics.RenderTextureArchiveSystem;
import net.matthiasauer.abocr.graphics.RenderedComponent;

/**
 * Catches touch up events and distributes them to the graphic which was
 * touched - pixel perfect detection on the texture is performed,
 * if multiple textures match - then the one with with the highest
 * layer.order is returned !
 * A InputTouchTriggeredComponent is created and attached to the entity
 * with the matching texture 
 */
public class InputTouchGeneratorSystem extends EntitySystem implements InputProcessor {
	private final InputMultiplexer inputMultiplexer;
	private final OrthographicCamera camera;
	public final Entity inputTouchContainerEntity;
	private PooledEngine engine;
	private ImmutableArray<Entity> targetEntities;
	private ComponentMapper<RenderComponent> renderComponentMapper;
	private ComponentMapper<RenderedComponent> renderedComponentMapper;
	private ComponentMapper<InputTouchTargetComponent> targetComponentMapper;
	private InputTouchEventComponent lastEvent;
	private RenderTextureArchiveSystem archive;
	
	public InputTouchGeneratorSystem(InputMultiplexer inputMultiplexer, OrthographicCamera camera) {
		this.camera = camera;
		this.lastEvent = null;
		this.inputMultiplexer = inputMultiplexer;
		this.inputTouchContainerEntity = new Entity();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void addedToEngine(Engine engine) {
		this.inputMultiplexer.addProcessor(this);
		
		this.engine = (PooledEngine)engine;
		this.engine.addEntity(this.inputTouchContainerEntity);
		this.renderComponentMapper =
				ComponentMapper.getFor(RenderComponent.class);
		this.renderedComponentMapper =
				ComponentMapper.getFor(RenderedComponent.class);
		this.targetComponentMapper =
				ComponentMapper.getFor(InputTouchTargetComponent.class);
		this.targetEntities =
				this.engine.getEntitiesFor(
						Family.all(
								InputTouchTargetComponent.class,
								RenderComponent.class,
								RenderedComponent.class).get());
		this.archive =
				this.engine.getSystem(RenderTextureArchiveSystem.class);
		
		super.addedToEngine(engine);
	}

	@Override
	public void removedFromEngine(Engine engine) {
		this.inputMultiplexer.removeProcessor(this);
		
		super.removedFromEngine(engine);
	}
	
	private boolean touchesVisiblePartOfTarget(
			Entity targetEntity, RenderComponent renderComponent, RenderedComponent renderedComponent) {
		InputTouchTargetComponent targetComponent =
				this.targetComponentMapper.get(targetEntity);
		
		// if in the bounding box
		if (renderedComponent.renderedTarget.contains(this.lastEvent.unprojectedPosition)) {
System.err.println("! " + renderComponent.texture.name);
			if (renderComponent.texture == null) {
				throw new NullPointerException("targetComponent.texture was null !");
			}
			
			if (this.isClickedPixelInvisible(renderedComponent, renderComponent, targetComponent)) {
				return true;
			}					
		}
		
		return false;
	}
	
	@Override
	public void update(float deltaTime) {
		// if there is an event that needs to be processsed
		if (this.lastEvent != null) {
			int orderOfCurrentTarget = -1;
			
			// remove any previous event
			this.inputTouchContainerEntity.remove(InputTouchEventComponent.class);

			// go over all entities
			for (Entity targetEntity : targetEntities) {
				RenderComponent renderComponent =
						this.renderComponentMapper.get(targetEntity);
				RenderedComponent renderedComponent =
						this.renderedComponentMapper.get(targetEntity);

				// search for the one that is touched and has the highest order of the layer
				if (this.touchesVisiblePartOfTarget(targetEntity, renderComponent, renderedComponent)) {
					if (renderComponent.layer.order > orderOfCurrentTarget) {
						orderOfCurrentTarget = renderComponent.layer.order;
						this.lastEvent.target = targetEntity;
System.err.println("oi !!! " + renderComponent.texture.name);
					}
				}
			}
			
			Gdx.app.debug(
					"InputTouchGeneratorSystem",
					lastEvent.inputType + " - " + lastEvent.target + " @" + lastEvent.timestamp);

			// save the event
			this.inputTouchContainerEntity.add(lastEvent);
			this.lastEvent = null;
		}
	}
	
	private boolean isClickedPixelInvisible(RenderedComponent renderedComponent, RenderComponent renderComponent, InputTouchTargetComponent targetComponent) {
		// http://gamedev.stackexchange.com/questions/43943/how-to-detect-a-touch-on-transparent-area-of-an-image-in-a-libgdx-stage
		Pixmap pixmap =
				this.archive.getPixmap(renderComponent.texture.getTexture());
System.err.println(
		
		(int)(renderComponent.texture.getRegionX() + this.lastEvent.unprojectedPosition.x - renderedComponent.renderedTarget.x) + " - " + 
		(int)(renderComponent.texture.getRegionY() + pixmap.getHeight() - (this.lastEvent.unprojectedPosition.y - renderedComponent.renderedTarget.y)));
		int pixel =
				pixmap.getPixel(
						(int)(renderComponent.texture.getRegionX() + this.lastEvent.unprojectedPosition.x - renderedComponent.renderedTarget.x),
						(int)(renderComponent.texture.getRegionY() + pixmap.getHeight() - (renderComponent.texture.getRegionY() - this.lastEvent.unprojectedPosition.y - renderedComponent.renderedTarget.y)));

		return (pixel & 0x000000ff) != 0;
	}

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
	
	private void saveEvent(int screenX, int screenY, InputTouchEventType inputType) {
		Vector3 projected = new Vector3(screenX, screenY, 0);
		Vector3 unprojected = this.camera.unproject(projected);
		this.lastEvent =
				this.engine.createComponent(InputTouchEventComponent.class);
		this.lastEvent.target = null;
		this.lastEvent.inputType = inputType;
		this.lastEvent.timestamp = System.currentTimeMillis();
		this.lastEvent.projectedPosition.x = screenX;
		this.lastEvent.projectedPosition.y = screenY;
		this.lastEvent.unprojectedPosition.x = unprojected.x;
		this.lastEvent.unprojectedPosition.y = unprojected.y;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		this.saveEvent(screenX, screenY, InputTouchEventType.TouchDown);
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		this.saveEvent(screenX, screenY, InputTouchEventType.TouchUp);
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		this.saveEvent(screenX, screenY, InputTouchEventType.Dragged);
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		this.saveEvent(screenX, screenY, InputTouchEventType.Moved);
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		// TODO Auto-generated method stub
		return false;
	}
}
