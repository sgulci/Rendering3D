package com.orangomango.blockworld;

import javafx.application.Application;
import javafx.geometry.Point3D;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import com.orangomango.rendering3d.model.*;
import com.orangomango.rendering3d.Engine3D;
import com.orangomango.blockworld.model.*;

public class MainApplication extends Application{
	private static final int WIDTH = 640; //320;
	private static final int HEIGHT = 360; //180;
	private static final double RENDER_DISTANCE = 5.5;
	private static final int CHUNKS = 7;

	private static final String[] inventoryBlocks = new String[]{"wood", "coal", "dirt", "stone"};
	private int currentBlock = 0;
	
	@Override
	public void start(Stage stage){
		stage.setTitle("BlockWorld");
		
		Engine3D engine = new Engine3D(stage, WIDTH, HEIGHT);

        Player player = new Player(0, 15, 0);
		Camera camera = player.getCamera();
		camera.zNear = 1;
		camera.zFar = 100;
		camera.lookAtCenter();
		
		engine.setCamera(camera);
		Light light = new Light(0, 0, 0); // -5, 3, 5
		light.setFixed(true);
		engine.getLights().add(light);
		//Engine3D.LIGHT_AVAILABLE = false;
		
		World world = new World();

		engine.setOnMousePressed(e -> {
			//world.removeBlockAt((int)player.getX(), (int)(player.getY()+1), (int)player.getZ());
			Block block = null;
			int startX = (int)Math.floor(player.getX());
			int startY = (int)Math.floor(player.getY());
			int startZ = (int)Math.floor(player.getZ());

			double stepX = Math.cos(camera.getRx())*Math.cos(camera.getRy()+Math.PI/2);
			double stepY = -Math.sin(camera.getRx());
			double stepZ = Math.cos(camera.getRx())*Math.sin(camera.getRy()+Math.PI/2);

			int lastX = 0;
			int lastY = 0;
			int lastZ = 0;

			for (double i = 0; i <= 10; i += 0.005){
				int lX = startX+(int)Math.round(i*stepX);
				int lY = startY+(int)Math.round(i*stepY);
				int lZ = startZ+(int)Math.round(i*stepZ);
				block = world.getBlockAt(lX, lY, lZ);
				if (block == null || i == 0){
					lastX = lX;
					lastY = lY;
					lastZ = lZ;
				}
				if (block != null) break;
			}
			if (block != null){
				if (e.getButton() == MouseButton.PRIMARY){
					world.removeBlockAt(block.getX(), block.getY(), block.getZ());
				} else if (e.getButton() == MouseButton.SECONDARY){
					world.setBlockAt(lastX, lastY, lastZ, inventoryBlocks[this.currentBlock]);
					/*int blockX = Integer.compare(lastX, block.getX());
					int blockY = Integer.compare(lastY, block.getY());
					int blockZ = Integer.compare(lastZ, block.getZ());
					world.setBlockAt(block.getX()+blockX, block.getY()+blockY, block.getZ()+blockZ, "wood");*/
				}
				for (int i = -1; i < 2; i++){
					for (int j = -1; j < 2; j++){
						for (int k = -1; k < 2; k++){
							Chunk chunk = world.getChunkAt(block.getX()/Chunk.CHUNK_SIZE+i, block.getY()/Chunk.CHUNK_SIZE+k, block.getZ()/Chunk.CHUNK_SIZE+j);
							if (chunk != null){
								for (MeshGroup mg : engine.getObjects()){
									if (mg.tag != null && mg.tag.equals(String.format("%d %d %d", chunk.getX(), chunk.getY(), chunk.getZ()))){
										mg.updateMesh(chunk.getMesh());
									}
								}
								chunk.setupFaces();
							}
						}
					}
				}
			}
		});

		engine.setOnKey(KeyCode.P, () -> {
			engine.getObjects().clear();
			world.clearChunks();
		});

		engine.setOnKey(KeyCode.O, engine::toggleMouseMovement);
		engine.setOnKey(KeyCode.DIGIT1, () -> this.currentBlock = 0);
		engine.setOnKey(KeyCode.DIGIT2, () -> this.currentBlock = 1);
		engine.setOnKey(KeyCode.DIGIT3, () -> this.currentBlock = 2);
		engine.setOnKey(KeyCode.DIGIT4, () -> this.currentBlock = 3);

		engine.setOnUpdate(gc -> {
			gc.setFill(Color.BLACK);
			int chunkX = player.getChunkX();
			int chunkY = player.getChunkY();
			int chunkZ = player.getChunkZ();
			gc.fillText(String.format("%d %d %d", chunkX, chunkY, chunkZ), 30, 50);
			boolean setupFaces = false;
			for (int i = -CHUNKS/2; i < -CHUNKS/2+CHUNKS; i++){
				for (int j = -CHUNKS/2; j < -CHUNKS/2+CHUNKS; j++){
					for (int k = 0; k < 2; k++){
						if (chunkX+i < 0 || chunkY+k < 0 || chunkZ+j < 0) continue;
						if (world.getChunkAt(chunkX+i, chunkY+k, chunkZ+j) == null){
							setupFaces = true;
							Chunk chunk = world.addChunk(chunkX+i, chunkY+k, chunkZ+j);
							MeshGroup mgroup = new MeshGroup(chunk.getMesh());
							mgroup.tag = String.format("%d %d %d", chunk.getX(), chunk.getY(), chunk.getZ());
							mgroup.skipCondition = cam -> {
								Point3D cpos = new Point3D(cam.getX()/Chunk.CHUNK_SIZE, cam.getY()/Chunk.CHUNK_SIZE, cam.getZ()/Chunk.CHUNK_SIZE);
								Point3D chunkPos = new Point3D(chunk.getX(), chunk.getY(), chunk.getZ());
								return cpos.distance(chunkPos) > RENDER_DISTANCE;
							};
							engine.getObjects().add(mgroup);
						}
					}
				}
			}
			if (setupFaces){
				for (Chunk chunk : world.getChunks()){
					chunk.setupFaces();
				}
			}
		});
		
		stage.setResizable(false);
		stage.setScene(engine.getScene());
		stage.show();
	}
	
	public static void main(String[] args){		
		System.out.println("F1 -> SHOW_LINES");
		System.out.println("F2 -> FOLLOW_LIGHT");
		System.out.println("F3 -> LIGHT_AVAILABLE");
		System.out.println("F4 -> ROTATE_LIGHT");
		System.out.println("F5 -> PLACE_LIGHT_AT_CAMERA");
		System.out.println("F6 -> SHADOWS");

		launch(args);
	}
}
