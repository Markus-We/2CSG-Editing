package data;

import java.util.HashMap;

public class Vertex {
	

	public Integer id;
	public Integer weightedDegree;
	public HashMap<Integer, Vertex> blue, red;
	
	public Vertex(Integer v) {
		id = v;
		weightedDegree = 0;
		blue = new HashMap<Integer, Vertex>();
		red = new HashMap<Integer, Vertex>();
	}

	public void addBlueEdge(Vertex v) {
		this.blue.put(v.id, v);
		this.weightedDegree += 2;
	}

	public void addRedEdge(Vertex v) {
		this.red.put(v.id, v);
		this.weightedDegree += 1;
	}

	public Integer getWeightedDegree() {
		return weightedDegree;
	}

	public void setWeightedDegree(Integer weightedDegree) {
		this.weightedDegree = weightedDegree;
	}
}
