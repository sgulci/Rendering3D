package com.orangomango.blockworld.model;

import javafx.geometry.Point2D;
import javafx.geometry.Point3D;
import javafx.scene.image.Image;

import com.orangomango.rendering3d.model.Mesh;

public class Block{
	private int x, y, z;
	private World world;
	private Mesh mesh;
	private String type;
	private int id;
	private boolean transparent;
	private boolean sprite;
	private double yOffset;
	private boolean liquid;
	
	public Block(Chunk chunk, int x, int y, int z, String type){
		this.world = chunk.getWorld();
		this.x = x+chunk.getX()*Chunk.CHUNK_SIZE;
		this.y = y+chunk.getY()*Chunk.CHUNK_SIZE;
		this.z = z+chunk.getZ()*Chunk.CHUNK_SIZE;
		this.type = type;
		setupSettings();
	}
	
	public Block(World world, int gx, int gy, int gz, String type){
		this.world = world;
		this.x = gx;
		this.y = gy;
		this.z = gz;
		this.type = type;
		setupSettings();
	}
	
	private void setupSettings(){
		this.id = Chunk.atlas.getBlockId(this.type);
		this.transparent = Chunk.atlas.isTransparent(this.type);
		this.sprite = Chunk.atlas.isSprite(this.type);
		this.liquid = Chunk.atlas.isLiquid(this.type);
	}
	
	public int getId(){
		return this.id;
	}
	
	public boolean isTransparent(){
		return this.transparent;
	}
	
	public boolean isLiquid(){
		return this.liquid;
	}
	
	public void setYOffset(double value){
		this.yOffset = value;
	}

	/*
	 * glass and water can "connect" as it's forced to the hiding process
	 */
	public void setupFaces(){
		if (this.sprite) return;
		mesh.clearHiddenFaces();
		Block block = this.world.getBlockAt(this.x+1, this.y, this.z);
		if (block != null && (!block.isTransparent() || (Chunk.atlas.isForceHiding(this.type) && block.getType().equals(this.type)))){
			mesh.addHiddenFace(2);
			mesh.addHiddenFace(3);
		}
		block = this.world.getBlockAt(this.x, this.y+1, this.z);
		if (block != null && (!block.isTransparent() || (Chunk.atlas.isForceHiding(this.type) && block.getType().equals(this.type))) && block.yOffset == 0){
			mesh.addHiddenFace(8);
			mesh.addHiddenFace(9);
		}
		block = this.world.getBlockAt(this.x, this.y, this.z+1);
		if (block != null && (!block.isTransparent() || (Chunk.atlas.isForceHiding(this.type) && block.getType().equals(this.type)))){
			mesh.addHiddenFace(4);
			mesh.addHiddenFace(5);
		}
		block = this.world.getBlockAt(this.x-1, this.y, this.z);
		if (block != null && (!block.isTransparent() || (Chunk.atlas.isForceHiding(this.type) && block.getType().equals(this.type)))){
			mesh.addHiddenFace(6);
			mesh.addHiddenFace(7);
		}
		block = this.world.getBlockAt(this.x, this.y-1, this.z);
		if (block != null && (!block.isTransparent() || (Chunk.atlas.isForceHiding(this.type) && block.getType().equals(this.type))) && this.yOffset == 0){
			mesh.addHiddenFace(10);
			mesh.addHiddenFace(11);
		}
		block = this.world.getBlockAt(this.x, this.y, this.z-1);
		if (block != null && (!block.isTransparent() || (Chunk.atlas.isForceHiding(this.type) && block.getType().equals(this.type)))){
			mesh.addHiddenFace(0);
			mesh.addHiddenFace(1);
		}
	}
	
	/**
	 * Block is declared like this:
	 * F F R R B B L L D D U  U
	 * 0 1 2 3 4 5 6 7 8 9 10 11
	 * 
	 * Texture coordinates are 1-y because the y-axis is inverted
	 */
	public Mesh getMesh(){
		if (this.mesh != null) return this.mesh;
		if (this.sprite){
			this.mesh = new Mesh(this.id, Chunk.atlas.getImages().get(this.type), new Point3D[]{
				new Point3D(this.x, this.y+this.yOffset, this.z), new Point3D(this.x, 1+this.y, this.z), new Point3D(1+this.x, 1+this.y, this.z),
				new Point3D(1+this.x, this.y+this.yOffset, this.z), new Point3D(this.x, this.y+this.yOffset, 1+this.z), new Point3D(this.x, 1+this.y, 1+this.z),
				new Point3D(1+this.x, 1+this.y, 1+this.z), new Point3D(1+this.x, this.y+this.yOffset, 1+this.z)}, new int[][]{
					{0, 1, 6}, {0, 6, 7}, {4, 5, 2}, {4, 2, 3}
				}, new Point2D[]{
					new Point2D(0, 1-1), new Point2D(0, 1-0), new Point2D(1, 1-0), new Point2D(1, 1-1)
				}, new int[][]{
					{0, 1, 2}, {0, 2, 3}, {0, 1, 2}, {0, 2, 3}
				}, Chunk.atlas.getBlockFaces().get(this.type), null, null, null);
			this.mesh.showAllFaces(true);
		} else {
			this.mesh = new Mesh(this.id, Chunk.atlas.getImages().get(this.type), new Point3D[]{
				new Point3D(this.x, this.y+this.yOffset, this.z), new Point3D(this.x, 1+this.y, this.z), new Point3D(1+this.x, 1+this.y, this.z),
				new Point3D(1+this.x, this.y+this.yOffset, this.z), new Point3D(this.x, this.y+this.yOffset, 1+this.z), new Point3D(this.x, 1+this.y, 1+this.z),
				new Point3D(1+this.x, 1+this.y, 1+this.z), new Point3D(1+this.x, this.y+this.yOffset, 1+this.z)}, new int[][]{
					{0, 1, 2}, {0, 2, 3}, {3, 2, 6},
					{3, 6, 7}, {7, 6, 5}, {7, 5, 4},
					{4, 5, 1}, {4, 1, 0}, {1, 5, 6},
					{1, 6, 2}, {4, 0, 3}, {4, 3, 7}
			}, new Point2D[]{
				new Point2D(0, 1-1), new Point2D(0, 1-0), new Point2D(1, 1-0), new Point2D(1, 1-1)
			}, new int[][]{
				{0, 1, 2}, {0, 2, 3}, {0, 1, 2}, {0, 2, 3},
				{0, 1, 2}, {0, 2, 3}, {0, 1, 2}, {0, 2, 3},
				{0, 1, 2}, {0, 2, 3}, {0, 1, 2}, {0, 2, 3},
				{0, 1, 2}, {0, 2, 3}, {0, 1, 2}, {0, 2, 3}
			}, Chunk.atlas.getBlockFaces().get(this.type), null, null, null);
			this.mesh.skipCondition = cam -> {
				Point3D pos = new Point3D(this.x, this.y, this.z);
				Point3D camPos = new Point3D(cam.getX(), cam.getY(), cam.getZ());
				return pos.distance(camPos) > ChunkManager.RENDER_DISTANCE*Chunk.CHUNK_SIZE;
			};
		}
		this.mesh.setTransparentProcessing(this.transparent);
		return this.mesh;
	}
	
	public int getX(){
		return this.x;
	}
	
	public int getY(){
		return this.y;
	}
	
	public int getZ(){
		return this.z;
	}
	
	public String getType(){
		return this.type;
	}

	@Override
	public String toString(){
		return String.format("Block at %d %d %d of type %s", this.x, this.y, this.z, this.type);
	}
}
