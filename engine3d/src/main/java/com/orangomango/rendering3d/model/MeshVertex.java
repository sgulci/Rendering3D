package com.orangomango.rendering3d.model;

import javafx.geometry.Point3D;
import javafx.geometry.Point2D;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

import java.util.HashMap;
import java.util.Objects;

import com.orangomango.rendering3d.Engine3D;

public class MeshVertex{
	private boolean imageVertex;
	private Point3D position, normal;
	private double[] view;

	// Image vertex
	private Point2D textureCoords;
	private Image image;

	// Color vertex
	private Color vertexColor;

	public static HashMap<MeshVertex, ProjectedVertex> VERTICES = new HashMap<>(100);

	private static class ProjectedVertex{
		private double[] view;
		private double[] projection;

		public ProjectedVertex(double[] view, double[] proj){
			this.view = view;
			this.projection = proj;
		}
	}

	public MeshVertex(Point3D position, Point3D normal, Point2D tex, Image image){
		this.imageVertex = true;
		this.position = position;
		this.normal = normal;
		this.textureCoords = tex;
		this.image = image;
	}

	public MeshVertex(Point3D position, Point3D normal, Color color){
		this.imageVertex = false;
		this.position = position;
		this.normal = normal;
		this.vertexColor = color;
	}

	@Override
	public int hashCode(){
		return Objects.hash(this.position);
	}

	@Override
	public boolean equals(Object other){
		if (other instanceof MeshVertex){
			MeshVertex v = (MeshVertex)other;
			return this.position.equals(v.position);
		} else return false;
	}

	public boolean isImageVertex(){
		return this.imageVertex;
	}

	public Image getImage(){
		return this.image;
	}

	public Point3D getPosition(){
		return this.position;
	}

	public Point2D getTextureCoords(){
		return this.textureCoords;
	}

	public Color getColor(){
		return this.vertexColor;
	}

	public Point3D getNormal(){
		return this.normal;
	}

	public void setNormal(Point3D n){
		this.normal = n;
	}

	public Point3D getViewPosition(Camera camera){
		ProjectedVertex projectedVertex = VERTICES.getOrDefault(this, null);
		if (projectedVertex == null || projectedVertex.view == null){
			double[] p = new double[]{this.position.getX(), this.position.getY(), this.position.getZ(), 1};
			this.view = Engine3D.multiply(camera.getViewMatrix(), p);

			if (projectedVertex == null){
				projectedVertex = new ProjectedVertex(this.view, null);
				VERTICES.put(this, projectedVertex);
			} else {
				projectedVertex.view = this.view;
			}

			return new Point3D(this.view[0], this.view[1], this.view[2]);
		} else {
			return new Point3D(projectedVertex.view[0], projectedVertex.view[1], projectedVertex.view[2]);
		}
	}

	public void setInView(){
		this.view = new double[]{this.position.getX(), this.position.getY(), this.position.getZ(), 1};
	}

	public double[] getProjection(Camera camera){
		ProjectedVertex projectedVertex = VERTICES.getOrDefault(this, null);
		if (projectedVertex == null || projectedVertex.projection == null){
			double[] proj = Engine3D.multiply(camera.getProjectionMatrix(), projectedVertex == null ? this.view : projectedVertex.view);
			double px = proj[0]/(proj[3] == 0 ? 1 : proj[3]);
			double py = proj[1]/(proj[3] == 0 ? 1 : proj[3]);
			double pz = proj[2];
			
			px += 1;
			py += 1;
			px *= 0.5*camera.getWidth();
			py *= 0.5*camera.getHeight();

			double[] projection = new double[]{px, py, 1/proj[3]};

			if (projectedVertex == null){
				projectedVertex = new ProjectedVertex(this.view, projection);
				VERTICES.put(this, projectedVertex);
			} else {
				projectedVertex.projection = projection;
			}

			return projection;
		} else {
			return projectedVertex.projection;
		}
	}
}