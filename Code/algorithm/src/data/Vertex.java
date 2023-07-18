package data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;


		//Kanten an zwei stellen ändern

public class Vertex {
	
	public Integer id;
	public Integer degree;
	public HashMap<Integer, Vertex> blue, blue_permanent, red, red_permanent, forbidden;
	public Lable lable;
	public double coreCost, periCost;
	public Integer neighborhoodClass;
	
	public Vertex(int v) {
		id = v;
		lable = Lable.NONE;
		degree = 0;
		coreCost = 0;
		periCost = 0;
		blue = new HashMap<Integer, Vertex>();
		blue_permanent = new HashMap<Integer, Vertex>();			//evtl nicht nötig
		red = new HashMap<Integer, Vertex>();
		red_permanent = new HashMap<Integer, Vertex>();
		forbidden = new HashMap<Integer, Vertex>();
		
	}
	
	
	public void addBlueEdge(Vertex v) {
		this.blue.put(v.id, v);
	}
	
	public void addRedEdge(Vertex v) {
		this.red.put(v.id, v);
	}
	
	public void addPermanentBlue(Vertex v) {
		this.blue_permanent.put(v.id, v);
	}
	
	public void addPermanentRed(Vertex v) {
		this.red_permanent.put(v.id, v);
	}
	
	public void addForbidden(Vertex v) {
		this.forbidden.put(v.id, v);
	}
	
	
	public Boolean isNeighbor(Integer id) {
		return this.blue.containsKey(id) || this.blue_permanent.containsKey(id) || 
				this.red.containsKey(id) || this.red_permanent.containsKey(id);
	}
	
	
	
	
	//Hier direkt abfragen ob noch erlaubt?
	public void calculateCoreCost(Graph g, double cost_red_blue, double cost_none_blue) {
		
		double cost = 0;
		
		for(Integer id : g.core) {
			
			if(this.id == id) continue;
			if(this.blue.containsKey(id) || this.blue_permanent.containsKey(id)) cost += 0;
			else if(this.red.containsKey(id) || this.red_permanent.containsKey(id)) cost += cost_red_blue;
			else cost += cost_none_blue ;
			
			
		}
		
		this.setCoreCost(cost);
		
	}
	
	public void calculatePeriCost(Graph g, double cost_red_blue, double cost_none_blue) {
		
		double cost = 0;
		
		for(Integer id : g.periphery) {
			
			if(this.id == id) continue;
			
			if(this.blue.containsKey(id) || this.blue_permanent.containsKey(id)) cost += cost_none_blue;
			else if(this.red.containsKey(id) || this.red_permanent.containsKey(id)) cost += 1;				//delete red costs 1
			else cost +=0 ;
			
		}
		this.setPeriCost(cost);
	}
	
	

	/**
	 * Changes Lable to CORE. Since changes are permanent
	 * this method is only called if lable was NONE.
	 * All edges to other vertices labled as CORE have to be blue permanent
	 * If there is an edge to another CORE vertex that forbidden or marked as permanent red
	 * two colored split graph editing is not solvable, how do i catch that in the solver?
	 * budget as parameter and true/false if possible? 				return -1
	 * 
	 * @return Returns the budget needed to do the changes. 
	 */
	public double moveToCore(Graph g, double cost_red_blue, double cost_none_blue) {
		double k = 0;
		//HashSet<Integer> removeBlue = new HashSet<Integer>();
		//HashSet<Integer> removeRed = new HashSet<Integer>();
		ArrayList<Edge> changedEdges = new ArrayList<Edge>();
		
		
		
		
		//Edge is (v1, v2, newType, oldType)
		for(Integer id : g.core) {
			Vertex v = g.vertices.get(id);
			
			if(this.blue.containsKey(id)) {
				
				Edge edge = new Edge(this.id, v.id, 2, 2);
				changedEdges.add(edge);
				
				this.blue.remove(id);
				this.blue_permanent.put(v.id, v);
				
				v.blue.remove(this.id);
				v.blue_permanent.put(this.id, this);
				
				continue;
				
			} else if(this.blue_permanent.containsKey(id)) {
				//do nothing
				continue;
			} else if(this.red.containsKey(id)) {
				
				Edge edge = new Edge(this.id, v.id, 2, 1);
				changedEdges.add(edge);
				
				this.red.remove(id);
				this.blue_permanent.put(v.id, v);
				
				v.red.remove(this.id);
				v.blue_permanent.put(this.id, this);
				
				k += cost_red_blue;
				continue;
				
			} else if(this.red_permanent.containsKey(id) || this.forbidden.containsKey(id)) {
				
				g.vertexStack.push(this.id);
				g.edgeStack.push(changedEdges);
				
				g.core.add(this.id);
				this.lable = Lable.CORE;
				return -1;
			} else {
				
				Edge edge = new Edge(this.id, v.id, 2, 0);
				changedEdges.add(edge);
				
				this.blue_permanent.put(v.id, v);
				v.blue_permanent.put(this.id, this);
				
				this.degree += 1;
				v.degree += 1;
				
				k += cost_none_blue;
				
			}
			
		}
		
		
		
		
		
//		for(Vertex v : this.blue.values()) {						//makes edge permanent if both vertices are in the Core.
//			
//			if(v.lable == Lable.CORE) {
//				
//				
//				//Edge is (v1, v2, newType, oldType)
//				Edge edge = new Edge(this.id, v.id, 2, 2);
//				changedEdges.add(edge);
//				
//				removeBlue.add(v.id);
//				blue_permanent.put(v.id, v);
//				v.blue.remove(this.id);
//				v.blue_permanent.put(this.id, this);
//			}
//		}
//		for(Integer id : removeBlue) {
//			this.blue.remove(id);
//		}
//		
//		for(Vertex v : this.red.values()) {
//			
//			if(v.lable == Lable.CORE) {
//				
//				Edge edge = new Edge(this.id, v.id, 2, 1);
//				changedEdges.add(edge);
//				
//				removeRed.add(v.id);
//				this.blue_permanent.put(v.id, v);
//				v.red.remove(this.id);
//				v.blue_permanent.put(this.id, this);
//				k++;
//			}
//		}
//		for(Integer id : removeRed) {
//			this.red.remove(id);
//		}
//		
//		//if perm red or forbbiden edge in core return -1, catch in algorithm
//		for(Vertex v : this.red_permanent.values()) {
//			if(v.lable == Lable.CORE) {
//				g.edgeStack.push(changedEdges);
//				return -1;
//			}
//		}
//		
//		for(Vertex v : this.forbidden.values()) {
//			if(v.lable == Lable.CORE) {
//				g.edgeStack.push(changedEdges);
//				return -1;
//			}
//		}
//		
//		for (Integer id : g.core) {
//			if (!this.blue.containsKey(id) && !this.blue_permanent.containsKey(id)) {
//				Vertex v = g.vertices.get(id);
//				
//				Edge edge = new Edge(this.id, v.id, 2, 0);
//				changedEdges.add(edge);
//				
//				this.blue_permanent.put(id,  v);
//				v.blue_permanent.put(this.id, this);
//				this.degree += 1;
//				v.degree += 1;
//				k += 2;
//			}
//			if (this.red.containsKey(id) || this.red_permanent.containsKey(id)) System.out.println("weird interaction");
//		}
		
		
		g.vertexStack.push(this.id);
		g.core.add(this.id);
		this.lable = Lable.CORE;
		g.edgeStack.push(changedEdges);
		return k;
	}
	

	/**
	 * Changes Lable to PERIPHERY. Since changes are permanent
	 * this method is only called if lable was NONE.
	 */
	public double moveToPeriphery(Graph g, double cost_red_blue, double cost_none_blue) {
		double k = 0;
		
//		HashSet<Integer> removeBlue = new HashSet<Integer>();
//		HashSet<Integer> removeRed = new HashSet<Integer>();
		
		ArrayList<Edge> changedEdges = new ArrayList<Edge>();
		
		//Edge is (v1, v2, newType, oldType)
		for(Integer id : g.periphery) {
			Vertex v = g.vertices.get(id);
			
			if(this.blue.containsKey(id)) {
				
				Edge edge = new Edge(this.id, id, 0, 2);
				changedEdges.add(edge);
				
				this.blue.remove(id);
				this.forbidden.put(id, v);
				
				v.blue.remove(this.id);
				v.forbidden.put(this.id, this);
				
				this.degree -= 1;
				v.degree -= 1;
				
				k += cost_none_blue;
				continue;
			} else if(this.blue_permanent.containsKey(id) || this.red_permanent.containsKey(id)) {
				
				g.vertexStack.push(this.id);
				g.edgeStack.push(changedEdges);
				
				g.periphery.add(this.id);
				this.lable = Lable.PERIPHERY;
				return -1;
				
			} else if(this.red.containsKey(id)) {
				
				Edge edge = new Edge(this.id, id, 0, 1);
				changedEdges.add(edge);
				
				this.red.remove(id);
				this.forbidden.put(id, v);
				
				v.red.remove(this.id);
				v.forbidden.put(this.id, this);
				
				this.degree -= 1;
				v.degree -= 1;
				
				k += 1;
				continue;
			}
			
		}
		
		
//		for(Vertex v : this.blue.values()) {
//			if(v.lable == Lable.PERIPHERY) {
//				
//				Edge edge = new Edge(this.id, v.id, 0, 2);
//				changedEdges.add(edge);
//				
//				removeBlue.add(v.id);
//				forbidden.put(v.id, v);
//				v.blue.remove(this.id);
//				v.forbidden.put(this.id, this);
//				this.degree -= 1;
//				v.degree -= 1;
//				k += 2;									//cost for removing blue edge is 2
//			}
//		}
//		for(Integer id : removeBlue) {
//			this.blue.remove(id);
//		}
//		
//		for(Vertex v : this.red.values()) {
//			if(v.lable == Lable.PERIPHERY) {
//				
//				Edge edge = new Edge(this.id, v.id, 0, 1);
//				changedEdges.add(edge);
//				
//				removeRed.add(v.id);
//				forbidden.put(v.id, v);
//				v.red.remove(this.id);
//				v.forbidden.put(this.id, this);		
//				this.degree -= 1;
//				v.degree -= 1;
//				k++;									
//			}
//		}
//		for(Integer id : removeRed) {
//			this.red.remove(id);
//		}
//		
//		//no permanent edges in peri allowed
//		for(Vertex v : this.red_permanent.values()) {
//			if(v.lable == Lable.PERIPHERY) {
//				g.edgeStack.push(changedEdges);
//				return -1;
//			}
//		}
//		
//		for(Vertex v : this.blue_permanent.values()) {
//			if(v.lable == Lable.PERIPHERY) {
//				g.edgeStack.push(changedEdges);
//				return -1;
//			}
//		}
//		
//		//TODO check if actually all edges removed!!!
//		
//		//add other peri vertices to forbidden
//		for (Integer id : g.periphery) {
//			Vertex v = g.vertices.get(id);
//			if(!this.forbidden.containsKey(id)) {
//				
//				Edge edge = new Edge(this.id, v.id, 0, 0);
//				changedEdges.add(edge);
//				
//				this.forbidden.put(id, v);
//			}
//			
//			
//			
//		}
		
		g.vertexStack.push(this.id);
		this.lable = Lable.PERIPHERY;
		g.periphery.add(this.id);
		g.edgeStack.push(changedEdges);
		return k;
	}
	
	
	public void print() {
		System.out.println(this.id);
	}


	public Integer getDegree() {
		return degree;
	}


	public void setDegree(Integer degree) {
		this.degree = degree;
	}


	public double getCoreCost() {
		return coreCost;
	}


	public void setCoreCost(double coreCost) {
		this.coreCost = coreCost;
	}


	public double getPeriCost() {
		return periCost;
	}


	public void setPeriCost(double periCost) {
		this.periCost = periCost;
	}
	
	
	
	

}
