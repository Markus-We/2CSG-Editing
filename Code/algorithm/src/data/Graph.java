package data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Stack;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;


/**
 * 
 * @author Markus
 *
 *         Questions:        
 *         				list of unlabled vertices for easier selection -> later for reduction rules
 *         				
 * 
 */

public class Graph {

	public HashMap<Integer, Vertex> vertices; 
	public HashMap<Integer, Vertex> unlabled; 	//würde Hashset mit ids reichen
	public HashSet<Integer> core, periphery;	//core für fehlende kanten, periphery probably unnötig.
	Integer size; 								//#of vertices
	
	public Stack<Integer> vertexStack;			//vertices always are unlable before change. so id is enoguh.
	public Stack<ArrayList<Edge>> edgeStack;
	
	
	public Graph() {
		vertices = new HashMap<Integer, Vertex>();
		unlabled = new HashMap<Integer, Vertex>();
		core = new HashSet<Integer>();
		periphery = new HashSet<Integer>();
		size = 0;
		vertexStack = new Stack<Integer>();
		edgeStack = new Stack<ArrayList<Edge>>();
	}
	
	
	
	/**
	 * Copy for first algorithm implementation
	 * @param g
	 */
	public Graph copyGraph() {
		Graph g_new = new Graph();
		g_new.size = this.size;
		//get all vertices + copy lables
		for(Vertex v : this.vertices.values()) {
			Vertex v_new = new Vertex(v.id);
			v_new.lable = v.lable;
			g_new.addVertex(v_new);
			v_new.degree = v.degree;										//vernünftige copy oder referenzeirte?
			if(this.core.contains(v.id)) g_new.core.add(v_new.id);
			if(this.periphery.contains(v.id)) g_new.periphery.add(v_new.id);
			
		}
		
		//copy edges
		for(Vertex v : g_new.vertices.values()) {
			Integer id = v.id;
			Vertex v_old = this.vertices.get(id);
			
			for(Vertex u : v_old.blue.values()) {
				Integer u_id = u.id;
				Vertex u_new = g_new.vertices.get(u_id);
				v.blue.put(u_id, u_new);
			}
			for(Vertex u : v_old.red.values()) {
				Integer u_id = u.id;
				Vertex u_new = g_new.vertices.get(u_id);
				v.red.put(u_id, u_new);
			}
			for(Vertex u : v_old.blue_permanent.values()) {
				Integer u_id = u.id;
				Vertex u_new = g_new.vertices.get(u_id);
				v.blue_permanent.put(u_id, u_new);
			}
			for(Vertex u : v_old.red_permanent.values()) {
				Integer u_id = u.id;
				Vertex u_new = g_new.vertices.get(u_id);
				v.red_permanent.put(u_id, u_new);
			}
			for(Vertex u : v_old.forbidden.values()) {
				Integer u_id = u.id;
				Vertex u_new = g_new.vertices.get(u_id);
				v.forbidden.put(u_id, u_new);
			}
			
			
		}
		
		for (Integer id : this.unlabled.keySet()) {
			Vertex v = g_new.vertices.get(id);
			g_new.unlabled.put(id, v);
		}
		
		return g_new;
		
	}
	
	
	//kommentare abfangen
	public static Graph readFromCSV(String f) {
		Graph g = new Graph();
		
		String line = "";
		
		try {
			BufferedReader reader = new BufferedReader(new FileReader(f));
			
			while((line = reader.readLine()) != null) {
				
				if(line.charAt(0) == '%') {
					
					continue;
				}
				String[] edge = line.split(" ");
				Integer v = Integer.valueOf(edge[0]);
				Integer u = Integer.valueOf(edge[1]);
				Integer type = Integer.valueOf(edge[2]);
				
				if(!g.vertices.containsKey(v)) {
					Vertex v1 = new Vertex(v);
					g.vertices.put(v, v1);
					g.unlabled.put(v, v1);
					g.size += 1;
				}
				if(!g.vertices.containsKey(u)) {
					Vertex v2 = new Vertex(u);
					g.vertices.put(u, v2);
					g.unlabled.put(u, v2);
					g.size += 1;
				}
				Vertex v1 = g.vertices.get(v);
				Vertex v2 = g.vertices.get(u);
				
				if(v1.blue.containsKey(u) || v1.red.containsKey(u)) {
					System.out.println("double edge in input");
					continue;
				}
				
				if(type <= 1) g.addRedEdge(v1, v2);
				if(type > 1) g.addBlueEdge(v1, v2);
				
				
				//if(type == 1) g.addRedEdge(v1, v2);
				//if(type == 2) g.addBlueEdge(v1, v2);
				v1.degree += 1;
				v2.degree += 1;
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
		return g;
	}
	
	
	public double restore(double cost_red_blue, double cost_none_blue) {
		
		double cost = 0;
		Integer id = this.vertexStack.pop();
		Vertex v = this.vertices.get(id);
		Boolean wasCore = false;
		Boolean wasPeri = false;
		
		if(this.core.contains(id)) {
			
			this.core.remove(id);
			this.unlabled.put(id, v);
			wasCore = true;
		}
		if(this.periphery.contains(id)) {
		
			this.periphery.remove(id);
			this.unlabled.put(id, v);
			wasPeri =true;
		}
		
		v.lable = Lable.NONE;
		
		ArrayList<Edge> changes = this.edgeStack.pop();
		
		for(Edge edge : changes) {
			
			Integer id1 = edge.getV1();
			Integer id2 = edge.getV2();
			Integer oldType = edge.getOldEdgeType();
			Integer newType = edge.getNewEdgeType();
			
			if(id != id1) System.out.println("fehler in edge-changes.");
			
			Vertex v1 = this.vertices.get(id1);
			Vertex v2 = this.vertices.get(id2);
			
			
			
			//new type is never 1 (for red)
			//newType 0 means forbidden.
			if(newType == 0) {
				
				if(oldType == 0) {

					v1.forbidden.remove(id2);
					v2.forbidden.remove(id1);
					cost += 0;							//does not cost anything.
					
				}else if(oldType == 1) {
					
					v1.forbidden.remove(id2);
					v1.red.put(id2, v2);
					v1.degree += 1;
					
					v2.forbidden.remove(id1);
					v2.red.put(id1, v1);
					v2.degree += 1;
					
					cost += 1;							//red edge was deleted, cost 1
					
				} else if(oldType == 2) {
					
					v1.forbidden.remove(id2);
					v1.blue.put(id2, v2);
					v1.degree += 1;
					
					v2.forbidden.remove(id1);
					v2.blue.put(id1, v1);
					v2.degree += 1;
					cost += cost_none_blue;								//blue edge was deleted, cost_none_blue
					
					
				}
				
			//newType 2 means blue_permanent	
			} else if(newType == 2) {
				
				if(oldType == 0) {
					
					v1.blue_permanent.remove(id2);
					v1.degree -= 1;
					
					v2.blue_permanent.remove(id1);
					v2.degree -= 1;
					cost += cost_none_blue;								//blue edge was added, cost 2
					
				}else if(oldType == 1) {
					
					v1.blue_permanent.remove(id2);
					v1.red.put(id2, v2);
					
					v2.blue_permanent.remove(id1);
					v2.red.put(id1, v1);
					cost += cost_red_blue;
					
				} else if(oldType == 2) {
					
					v1.blue_permanent.remove(id2);
					v1.blue.put(id2, v2);
					
					v2.blue_permanent.remove(id1);
					v2.blue.put(id1, v1);
					cost += 0;								//edge was blue before, just made permanent.
				}
				
			}
			
		}
		
		if(wasCore) {
			this.decreaseCoreCosts(v, cost_red_blue, cost_none_blue);
		} else if(wasPeri) {
			this.decreasePeriCost(v, cost_none_blue);
		}
		
		return cost;
	}
	
	
	public void initializeCosts(double cost_red_blue, double cost_none_blue) {
		
		for(Vertex v : vertices.values()) {
			
			v.calculateCoreCost(this, cost_red_blue, cost_none_blue);
			v.calculatePeriCost(this, cost_red_blue, cost_none_blue);
			
		}
		
	}
	
	//only called after labeling v as core
	public void increaseCoreCosts(Vertex v, double cost_red_blue, double cost_none_blue) {
		
		for(Vertex u:this.unlabled.values()) {
			
			if(u.id == v.id) continue;
			if(!v.blue.containsKey(u.id) && !v.blue_permanent.containsKey(u.id)) {
				
				if(v.red.containsKey(u.id) || v.red_permanent.containsKey(u.id)) {
					double current = u.getCoreCost();
					u.setCoreCost(current + cost_red_blue);
				} else {
					double current = u.getCoreCost();
					u.setCoreCost(current + cost_none_blue);
				}
				
			}
		}
		
	}
	
	//called after v was moved out of core
	public void decreaseCoreCosts(Vertex v, double cost_red_blue, double cost_none_blue) {
		
		for(Vertex u:this.unlabled.values()) {
			if(u.id == v.id) continue;
			
			if(!v.blue.containsKey(u.id) && !v.blue_permanent.containsKey(u.id)) {
				
				if(v.red.containsKey(u.id) || v.red_permanent.containsKey(u.id)) {
					double current = u.getCoreCost();
					u.setCoreCost(current - cost_red_blue);
							
				} else {
					double current = u.getCoreCost();
					u.setCoreCost(current - cost_none_blue);
				}
				
			}
			
		}
	}
	
	public void increasePeriCosts(Vertex v, double cost_none_blue) {
		
		for(Vertex u:this.unlabled.values()) {
			if(u.id == v.id) continue;
			
			if(v.blue.containsKey(u.id) || v.blue_permanent.containsKey(u.id)) {
				double current = u.getPeriCost();
				u.setPeriCost(current + cost_none_blue);
			} else if(v.red.containsKey(u.id)||v.red_permanent.containsKey(u.id)) {
				double current = u.getPeriCost();
				u.setPeriCost(current + 1);
			}
			
		}
		
	}
	
	public void decreasePeriCost(Vertex v, double cost_none_blue) {
		
		for(Vertex u:this.unlabled.values()) {
			if(u.id == v.id) continue;
			
			if(v.blue.containsKey(u.id) || v.blue_permanent.containsKey(u.id)) {
				
				double current = u.getPeriCost();
				u.setPeriCost(current - cost_none_blue);
				
			} else if(v.red.containsKey(u.id) || v.red_permanent.containsKey(u.id)) {
				
				double current = u.getPeriCost();
				u.setPeriCost(current - 1);
				
			}
		}
		
	}
	
	//this works bc cost to move never decreases.
	//only call if initialized
	public double lowerBoundFromVertices() {
		double lb = 0;
		
		for(Vertex v : vertices.values()) {
			if(v.coreCost < v.periCost) lb += v.coreCost;
			else lb += v.periCost;
		}
		
		return lb;
	}
	
	
	public void addVertex(Vertex v) {
		vertices.put(v.id, v);
	}
	
	/**
	 * Adding blue edges for graph initialization
	 * @param u
	 * @param v
	 */
	public void addBlueEdge(Vertex u, Vertex v) {
		u.addBlueEdge(v);
		v.addBlueEdge(u);
	}
	
	/**
	 * Adding red edges for graph initialization
	 * @param u
	 * @param v
	 */
	public void addRedEdge(Vertex u, Vertex v) {
		u.addRedEdge(v);
		v.addRedEdge(u);
	}
	
	//TODO adding/changing edges. Done in Vertex
	
	
}
