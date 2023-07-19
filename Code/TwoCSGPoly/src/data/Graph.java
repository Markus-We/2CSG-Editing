package data;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

public class Graph {
	



	public HashMap<Integer, Vertex> vertices;
	public Integer size;
	
	public Graph() {
		size = 0;
		vertices = new HashMap<Integer, Vertex>();
	}
	
	public static Graph readFromCSV(String f) {
		Graph g = new Graph();
		
		String line = "";
		
		try {
			BufferedReader reader = new BufferedReader(new FileReader(f));
			
			while((line = reader.readLine()) != null) {
				
				if(line.charAt(0) == '%') {
//					System.out.println("comment detected");
					continue;
				}
				String[] edge = line.split(" ");
				Integer v = Integer.valueOf(edge[0]);
				Integer u = Integer.valueOf(edge[1]);
				Integer type = Integer.valueOf(edge[2]);
				
				if(!g.vertices.containsKey(v)) {
					Vertex v1 = new Vertex(v);
					g.vertices.put(v, v1);
					g.size += 1;
				}
				if(!g.vertices.containsKey(u)) {
					Vertex v2 = new Vertex(u);
					g.vertices.put(u, v2);
					g.size += 1;
				}
				Vertex v1 = g.vertices.get(v);
				Vertex v2 = g.vertices.get(u);
				
				if(type <= 1) g.addRedEdge(v1, v2);
				if(type > 1) g.addBlueEdge(v1, v2);
				
//				if(type == 1) g.addRedEdge(v1, v2);
//				if(type == 2) g.addBlueEdge(v1, v2);
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
		return g;
	}

	
	public void addBlueEdge(Vertex u, Vertex v) {
		u.addBlueEdge(v);
		v.addBlueEdge(u);
	}
	

	public void addRedEdge(Vertex u, Vertex v) {
		u.addRedEdge(v);
		v.addRedEdge(u);
	}
	
}
