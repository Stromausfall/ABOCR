package net.matthiasauer.abocr.graphics;

public enum RenderLayer {
	Tiles(1, true),
	UI(2, false);

	public final Integer order;
	public final boolean projected;
	
	private RenderLayer(int order, boolean projected) {
		this.order = order;
		this.projected = projected;
	}
}
