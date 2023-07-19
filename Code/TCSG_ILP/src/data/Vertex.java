package data;

import java.util.HashMap;

public class Vertex {
	
	public Integer id;
	public Integer degree;
	public HashMap<Integer, Vertex> blue, red;
	public double coreCost, periCost;
	public Integer neighborhoodClass;
	
	public Vertex(int v) {
		id = v;
		degree = 0;
		coreCost = 0;
		periCost = 0;
		blue = new HashMap<Integer, Vertex>();
		red = new HashMap<Integer, Vertex>();

	}
	
	
	
	
	public void addBlueEdge(Vertex v) {
		this.blue.put(v.id, v);
	}
	
	public void addRedEdge(Vertex v) {
		this.red.put(v.id, v);
	}

}
