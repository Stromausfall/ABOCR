package net.matthiasauer.abocr.desktop;

import com.badlogic.gdx.tools.texturepacker.TexturePacker;

public class Tools {
	public static void main(String[] args) {
		String inputDir = "C:\\Users\\mail_000\\SkyDrive\\LibGDX\\ABOCR\\gfxSource\\data1.atlas";
		String outputDir = "C:\\Users\\mail_000\\SkyDrive\\LibGDX\\ABOCR\\android\\assets";
		String packFileName = "data1.atlas";

        TexturePacker.process(inputDir, outputDir, packFileName);
	}
}
