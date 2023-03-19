package com.orangomango.rendering3d;

import javafx.application.Application;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import javafx.scene.image.Image;
import javafx.geometry.Point2D;
import javafx.geometry.Point3D;

import java.io.File;
import java.util.Random;

import com.orangomango.rendering3d.model.Camera;
import com.orangomango.rendering3d.model.Light;
import com.orangomango.rendering3d.model.Mesh;
import com.orangomango.rendering3d.model.MeshGroup;

public class MainApplication extends Application{
	private static final int WIDTH = 320;
	private static final int HEIGHT = 180;
	
	@Override
	public void start(Stage stage){		
		stage.setTitle("3D Graphics");
		
		Engine3D engine = new Engine3D(stage, WIDTH, HEIGHT);
		Camera camera = new Camera(0, 7.5, -5);
		camera.lookAtCenter();
		
		engine.setCamera(camera);
		
		//engine.getLights().add(new Light(-5, -1, -5));
		engine.getLights().add(new Light(-15, 0, 30));
		
		/*Random random = new Random();
		for (int i = 0; i < 1; i++){
			for (int j = 0; j < 1; j++){
				for (int k = 0; k < 1; k++){
					engine.getObjects().add(new Mesh(switch(random.nextInt(3)){
						case 0 -> COAL_IMAGE;
						case 1 -> DIRT_IMAGE;
						case 2 -> STONE_IMAGE;
						default -> null;
					}, new Point3D[]{
						new Point3D(i, k, j), new Point3D(i, 1+k, j), new Point3D(1+i, 1+k, j),
						new Point3D(1+i, k, j), new Point3D(i, k, 1+j), new Point3D(i, 1+k, 1+j), 
						new Point3D(1+i, 1+k, 1+j), new Point3D(1+i, k, 1+j)}, new int[][]{
							{0, 1, 2}, {0, 2, 3}, {3, 2, 6},
							{3, 6, 7}, {7, 6, 5}, {7, 5, 4},
							{4, 5, 1}, {4, 1, 0}, {1, 5, 6},
							{1, 6, 2}, {4, 0, 3}, {4, 3, 7}
					}, new Point2D[]{
						new Point2D(0, 1), new Point2D(0, 0), new Point2D(1, 0), new Point2D(1, 1)
					}, new int[][]{
						{0, 1, 2}, {0, 2, 3}, {0, 1, 2}, {0, 2, 3},
						{0, 1, 2}, {0, 2, 3}, {0, 1, 2}, {0, 2, 3},
						{0, 1, 2}, {0, 2, 3}, {0, 1, 2}, {0, 2, 3},
						{0, 1, 2}, {0, 2, 3}, {0, 1, 2}, {0, 2, 3}
					}, null, null, null));
				}
			}
		}*/

		double speed = 0.7;
		engine.setOnKey(KeyCode.W, () -> camera.move(speed*Math.cos(camera.getRy()+Math.PI/2), 0, speed*Math.sin(camera.getRy()+Math.PI/2)), false);
		engine.setOnKey(KeyCode.A, () -> camera.move(-speed*Math.cos(camera.getRy()), 0, -speed*Math.sin(camera.getRy())), false);
		engine.setOnKey(KeyCode.S, () -> camera.move(-speed*Math.cos(camera.getRy()+Math.PI/2), 0, -speed*Math.sin(camera.getRy()+Math.PI/2)), false);
		engine.setOnKey(KeyCode.D, () -> camera.move(speed*Math.cos(camera.getRy()), 0, speed*Math.sin(camera.getRy())), false);
		engine.setOnKey(KeyCode.SPACE, () -> camera.move(0, -speed, 0), false);
		engine.setOnKey(KeyCode.SHIFT, () -> camera.move(0, speed, 0), false);
		
		try {
			//Mesh model = Mesh.loadFromFile(new File(MainApplication.class.getResource("/model.obj").toURI()), 0, 0, 0, 0.05, null, false);
			//model.setRotation(Math.PI/2, 0, 0);
			//engine.getObjects().add(model);
			//engine.getObjects().add(Mesh.loadFromFile(new File(MainApplication.class.getResource("/plane3.obj").toURI()), 0, 0.5, 0, 0.5, null, false));
			
			/*Mesh model = Mesh.loadFromFile(new File(MainApplication.class.getResource("/truck.obj").toURI()), 0, 0, 0, 0.05, null, true);
			model.setRotation(Math.PI, 0, 0);
			engine.getObjects().add(model);*/
			
			Mesh model = Mesh.loadFromFile(new File(MainApplication.class.getResource("/plane3.obj").toURI()), 0, 0.5, 0, 0.5, null, false);
			engine.getObjects().add(new MeshGroup(model));
			
			//Mesh model = Mesh.loadFromFile(new File(MainApplication.class.getResource("/chess.obj").toURI()), 0, 0, 0, 10, null, false);
			//model.setRotation(0, 0, Math.PI);
			//engine.getObjects().add(model);
		} catch (Exception ex){
			ex.printStackTrace();
		}
		
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