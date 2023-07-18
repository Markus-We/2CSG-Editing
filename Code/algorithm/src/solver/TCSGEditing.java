package solver;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

import data.Graph;
import data.Lable;
import data.Vertex;

/**
 * 
 * @author Markus
 *
 * 
 *         datenerzeugen
 * 
 *         zeitlimit + output
 * 
 * 
 * 
 * 
 */

public class TCSGEditing {

	// args[0] path to input, args[1] path to output, args[2] cost for recoloring
	// (x), args[3] cost for blue edge (y), args[4] timelimit, args[5] increment
	public static void main(String[] args) {

		// "C:\\Users\\Markus\\master-markus\\Code\\data\\synthetic\\k10_synthetic_001.txt"
		// "C:\\Users\\Markus\\master-markus\\Code\\data\\synthetic\\splitgraph\\random\\test_graph.txt"
		// "./tests/data/first_tests/g4.txt"
		// ./tests/data/lesmis.txt
		// C:/Users/Markus/master-markus/Code/data/synthetic/random_split_375_8_k40_1.txt
		
		
		//C:\Users\Markus\master-markus\Code\data\synthetic\polytime_cost\all_k\random_split_25_1_k70.txt

		Long startTime = System.currentTimeMillis();

		String path = args[0];
		Graph g = Graph.readFromCSV(path);
		

		double k = 0;

		// cost definition

		// cost_nr stays 1

		double cost_rb = Double.parseDouble(args[2]);
		double cost_nb = Double.parseDouble(args[3]);

		double increment = 1; // vielfache von 0.5 oder 1

		Long timeLimit = Long.parseLong(args[4]);

		Long endtime = startTime + timeLimit;

		if (args.length >= 6)
			increment = Double.parseDouble(args[5]);

		int nbrVert = g.vertices.size();

		double upperBound;
		if (cost_rb >= cost_nb) {
			upperBound = nbrVert * nbrVert * cost_rb;
		} else {
			upperBound = nbrVert * nbrVert * cost_nb;
		}
		Boolean done = false;

		// do initial data reduction

		while (k < upperBound && !done) {

			done = vertexBranch(k, g, cost_rb, cost_nb, endtime);

			if (System.currentTimeMillis() > endtime)
				break;

			k = k + increment;
		}

		Long endTime = System.currentTimeMillis();

		Long time = (endTime - startTime);
		String outputFile = args[1];

		Writer output;
		try {
			output = new BufferedWriter(new FileWriter(outputFile, true));
			output.append('\n');
			output.append(path + " " + g.vertices.size() + " " + (k - 1) + " " + g.core.size() + " " + time);

			output.close();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

//		System.out.println("upperbound is " + upperBound);
//		System.out.print(done + " k=" + (k - 1));

	}

	//
	public static Boolean vertexBranch(double k, Graph g, double cost_red_blue, double cost_none_blue, Long timeLimit) {

		if (System.currentTimeMillis() > timeLimit)
			return false;

		Boolean applyRuleOneDegree = false;
		Boolean applyRuleDomination = false;
		Boolean applyOnlyClique = false;
		Boolean applyOnlyPeri = false;
		

		if (g.unlabled.size() == 0) {
			if (k >= 0) {
				// System.out.println("k= " + (k));

				return isSolution(g);
			}

		}

		if (k < 0)
			return false;

		if (k == 0) {
			return isSolution(g);
		}


		// System.out.println(unlabled);
		// System.out.println("k = " + k);

		// needed if no reduction

		// Reduction Rules
//		if (!solvableMinCost(g, cost_red_blue, cost_none_blue, g.unlabled, k))
//			return false;

		
		
		if (!solvableTotalCost(g, cost_red_blue, cost_none_blue, g.unlabled, k))
			return false;
		
		int reductionCost = 0;
		
		int onlyCliqueApplied = 0;
		if(applyOnlyClique) {
			ArrayList<Vertex> toClique = applyOnlyClique(g, k, cost_red_blue, cost_none_blue);
			for(Vertex v:toClique) {
				reductionCost += v.moveToCore(g, cost_red_blue, cost_none_blue);
				g.unlabled.remove(v.id);
				g.core.add(v.id);
				g.increaseCoreCosts(v, cost_red_blue, cost_none_blue);
				onlyCliqueApplied += 1;
			}
		}
		
		int onlyPeriApplied = 0;
		if(applyOnlyPeri) {
			ArrayList<Vertex> toPeri = applyOnlyPeri(g, k, cost_red_blue, cost_none_blue);
			for(Vertex v:toPeri) {
				reductionCost += v.moveToPeriphery(g, cost_red_blue, cost_none_blue);
				g.unlabled.remove(v.id);
				g.periphery.add(v.id);
				g.increasePeriCosts(v, cost_none_blue);
				onlyPeriApplied += 1;
			}
		}
		
		
		
		if (g.unlabled.size() == 0) {
			if ((k-reductionCost) >= 0) {
				System.out.println("k= " + (k-reductionCost) + " " + onlyPeriApplied + " " + onlyCliqueApplied);

				return isSolution(g);
			} else {
				
				for(int i = 0; i<onlyCliqueApplied + onlyPeriApplied; i++) {
					g.restore(cost_red_blue, cost_none_blue);
						
				}
				for(Vertex u:g.unlabled.values()) {
					u.calculateCoreCost(g, cost_red_blue, cost_none_blue);
					u.calculatePeriCost(g, cost_red_blue, cost_none_blue);
				}
					
				
				return false;
			}

		}
		

		//

		 int id = selectNextMaxCost(g.unlabled.values()); //select highest cost.
//		 int id = selectNextAvgCost(g.unlabled); //select avg cost.

		
//		List<Vertex> list = new ArrayList<Vertex>(g.unlabled.values());
//		int unlabled = list.size();
//		Random rand = new Random();
//		int index = rand.nextInt(list.size());
//		Vertex next = list.get(index);
//		Integer id = next.id; // random vertex selection

		// Vertex v = g_copy.vertices.get(id);
		Vertex v = g.vertices.get(id);

		if (v == null) {
			for (Vertex u : g.unlabled.values()) {
//				System.out.println("vertex " + u.id);
//				System.out.println(id);
			}
		}

//		v.calculateCoreCost(g, cost_red_blue, cost_none_blue);										need
//		v.calculatePeriCost(g, cost_red_blue, cost_none_blue);

		double coreCost = v.getCoreCost();
		double periCost = v.getPeriCost();
		
//		System.out.println(coreCost + " " + periCost);

		if (coreCost >= periCost) {

			// CORE-branch---------------------------------------------------------------
			double cost;
			if (coreCost <= k) {
				cost = 0;
				// cost = v.moveToCore(g_copy);
				cost = v.moveToCore(g, cost_red_blue, cost_none_blue);
				
				g.core.add(v.id);
				g.unlabled.remove(v.id);
				
				g.increaseCoreCosts(v, cost_red_blue, cost_none_blue);
				


				if (cost == -1) {
					System.out.println("edited permanent or forbidden edge.");
					g.restore(cost_red_blue, cost_none_blue);
					
					v.calculateCoreCost(g, cost_red_blue, cost_none_blue);										
					v.calculatePeriCost(g, cost_red_blue, cost_none_blue);
					
					return false;
				}

				int appliedOneDegree = 0;
				if (applyRuleOneDegree) {
					
					ArrayList<Vertex> toPeri = applyDegreeOneNeighbor(v, g);
					appliedOneDegree = toPeri.size();
					for (Vertex u : toPeri) {
						
						cost += u.moveToPeriphery(g, cost_red_blue, cost_none_blue); // should be 0
						g.increasePeriCosts(v, cost_none_blue);
						g.periphery.add(u.id);
						g.unlabled.remove(u.id);
					}
				}

				int appliedDomination = 0;
				if (applyRuleDomination) {
					
					ArrayList<Vertex> toCore = applyDominatedNeighborhood(v, g);
					appliedDomination = toCore.size();
					for (Vertex u : toCore) {
						
						cost += u.moveToCore(g, cost_red_blue, cost_none_blue);
						g.increaseCoreCosts(v, cost_red_blue, cost_none_blue);
						g.core.add(u.id);
						g.unlabled.remove(u.id);
					}

				}

				if (vertexBranch(k - cost - reductionCost, g, cost_red_blue, cost_none_blue, timeLimit))
					return true;

				if (appliedDomination > 0) {
					for (int i = 0; i < appliedDomination; i++) {
						g.restore(cost_red_blue, cost_none_blue);
					}
				}
				if (appliedOneDegree > 0) {
					for (int i = 0; i < appliedOneDegree; i++) {
						g.restore(cost_red_blue, cost_none_blue);

					}
				}

				g.restore(cost_red_blue, cost_none_blue);
				
				if (applyRuleDomination || applyRuleOneDegree ) {

					for (Vertex u : g.unlabled.values()) {
						u.calculateCoreCost(g, cost_red_blue, cost_none_blue);
						u.calculatePeriCost(g, cost_red_blue, cost_none_blue);
					}

				}

				
//				for (Vertex u : g.unlabled.values()) {
//					u.calculateCoreCost(g, cost_red_blue, cost_none_blue);
//					u.calculatePeriCost(g, cost_red_blue, cost_none_blue);
//				}
				
				
				v.calculateCoreCost(g, cost_red_blue, cost_none_blue);
				v.calculatePeriCost(g, cost_red_blue, cost_none_blue);

			}
			// PERI-Branch-------------------------------------------------------
			// g_copy = g.copyGraph();
			// v = g_copy.vertices.get(id);

			// cost = v.moveToPeriphery(g_copy);
			cost = 0;
			cost = v.moveToPeriphery(g, cost_red_blue, cost_none_blue);

			g.periphery.add(v.id);
			g.unlabled.remove(v.id);
			g.increasePeriCosts(v, cost_none_blue);


			if (cost == -1) {
				System.out.println("edited permanent or forbidden edge.");
				
				
				g.restore(cost_red_blue, cost_none_blue);
				
				if(onlyPeriApplied > 0) {
					for (int i = 0; i < onlyPeriApplied; i++) {
						g.restore(cost_red_blue, cost_none_blue);

					}
					
				}
				
				if(onlyCliqueApplied > 0) {
					for (int i = 0; i < onlyCliqueApplied; i++) {
						g.restore(cost_red_blue, cost_none_blue);

					}
					
				}
				
				if(applyOnlyClique || applyOnlyPeri) {
					for(Vertex u:g.unlabled.values()) {
						u.calculateCoreCost(g, cost_red_blue, cost_none_blue);
						u.calculatePeriCost(g, cost_red_blue, cost_none_blue);
					}
					
				}
				
				v.calculateCoreCost(g, cost_red_blue, cost_none_blue);										
				v.calculatePeriCost(g, cost_red_blue, cost_none_blue);
				return false;
			}

			// g.updatePeriCost(v, cost_none_blue);

			if (vertexBranch(k - cost - reductionCost, g, cost_red_blue, cost_none_blue, timeLimit))
				return true;

			g.restore(cost_red_blue, cost_none_blue);

			if(onlyPeriApplied > 0) {
				for (int i = 0; i < onlyPeriApplied; i++) {
					g.restore(cost_red_blue, cost_none_blue);

				}
				
			}
			
			if(onlyCliqueApplied > 0) {
				for (int i = 0; i < onlyCliqueApplied; i++) {
					g.restore(cost_red_blue, cost_none_blue);

				}
				
			}
			
			if(applyOnlyClique || applyOnlyPeri) {
				for(Vertex u:g.unlabled.values()) {
					u.calculateCoreCost(g, cost_red_blue, cost_none_blue);
					u.calculatePeriCost(g, cost_red_blue, cost_none_blue);
				}
				
			}
			
//			for (Vertex u : g.unlabled.values()) {
//				u.calculateCoreCost(g, cost_red_blue, cost_none_blue);
//				u.calculatePeriCost(g, cost_red_blue, cost_none_blue);
//			}
			
			
			v.calculateCoreCost(g, cost_red_blue, cost_none_blue);
			v.calculatePeriCost(g, cost_red_blue, cost_none_blue);

			return false;

		} else {

			double cost = 0;
			
				// PERI-Branch----------------------------------------------------------------------
				// g_copy = g.copyGraph();
				// v = g_copy.vertices.get(id);

				// cost = v.moveToPeriphery(g_copy);
				cost = v.moveToPeriphery(g, cost_red_blue, cost_none_blue);
				g.periphery.add(id);
				g.unlabled.remove(id);

				g.increasePeriCosts(v, cost_none_blue);

				if (cost == -1) {
					System.out.println("edited permanent or forbidden edge.");
					g.restore(cost_red_blue, cost_none_blue);
					
					v.calculateCoreCost(g, cost_red_blue, cost_none_blue);										
					v.calculatePeriCost(g, cost_red_blue, cost_none_blue);
					
					return false;
				}

				// g.updatePeriCost(v, cost_none_blue);

				// g_copy

				if (vertexBranch(k - cost -reductionCost, g, cost_red_blue, cost_none_blue, timeLimit))
					return true;

				g.restore(cost_red_blue, cost_none_blue);

				
//				for (Vertex u : g.unlabled.values()) {
//					u.calculateCoreCost(g, cost_red_blue, cost_none_blue);
//					u.calculatePeriCost(g, cost_red_blue, cost_none_blue);
//				}
				
				v.calculateCoreCost(g, cost_red_blue, cost_none_blue);
				v.calculatePeriCost(g, cost_red_blue, cost_none_blue);

			

			// CORE-branch------------------------------------------------------------------------------

			// cost = v.moveToCore(g_copy);
			cost = v.moveToCore(g, cost_red_blue, cost_none_blue);
			g.core.add(id);
			g.unlabled.remove(id);
			g.increaseCoreCosts(v, cost_red_blue, cost_none_blue);


			if (cost == -1) {
				System.out.println("edited permanent or forbidden edge.");
				g.restore(cost_red_blue, cost_none_blue);
				
				if(onlyPeriApplied > 0) {
					for (int i = 0; i < onlyPeriApplied; i++) {
						g.restore(cost_red_blue, cost_none_blue);

					}
					
				}
				
				if(onlyCliqueApplied > 0) {
					for (int i = 0; i < onlyCliqueApplied; i++) {
						g.restore(cost_red_blue, cost_none_blue);

					}
					
				}
				
				if (applyRuleDomination || applyRuleOneDegree || applyOnlyClique || applyOnlyPeri) {

					for (Vertex u : g.unlabled.values()) {
						u.calculateCoreCost(g, cost_red_blue, cost_none_blue);
						u.calculatePeriCost(g, cost_red_blue, cost_none_blue);
					}

				}
				
				v.calculateCoreCost(g, cost_red_blue, cost_none_blue);										
				v.calculatePeriCost(g, cost_red_blue, cost_none_blue);
				return false;
			}

			int appliedOneDegree = 0;
			if (applyRuleOneDegree) {
				
				ArrayList<Vertex> toPeri = applyDegreeOneNeighbor(v, g);
				appliedOneDegree = toPeri.size();
				for (Vertex u : toPeri) {
					
					cost += u.moveToPeriphery(g, cost_red_blue, cost_none_blue);
					g.increasePeriCosts(v, cost_none_blue);
					g.periphery.add(u.id);
					g.unlabled.remove(u.id);
				}
			}

			int appliedDomination = 0;
			if (applyRuleDomination) {
				
				ArrayList<Vertex> toCore = applyDominatedNeighborhood(v, g);
				appliedDomination = toCore.size();
				for (Vertex u : toCore) {
					
					cost += u.moveToCore(g, cost_red_blue, cost_none_blue);
					g.increaseCoreCosts(v, cost_red_blue, cost_none_blue);
					g.core.add(u.id);
					g.unlabled.remove(u.id);
				}

			}

			// g.updateCoreCosts(v, cost_red_blue, cost_none_blue);

			// g_copy

			if (vertexBranch(k - cost - reductionCost, g, cost_red_blue, cost_none_blue, timeLimit))
				return true;

			if (appliedDomination > 0) {
				for (int i = 0; i < appliedDomination; i++) {
					g.restore(cost_red_blue, cost_none_blue);
				}
			}
			if (appliedOneDegree > 0) {
				for (int i = 0; i < appliedOneDegree; i++) {
					g.restore(cost_red_blue, cost_none_blue);

				}
			}

			g.restore(cost_red_blue, cost_none_blue);

			if(onlyPeriApplied > 0) {
				for (int i = 0; i < onlyPeriApplied; i++) {
					g.restore(cost_red_blue, cost_none_blue);

				}
				
			}
			
			if(onlyCliqueApplied > 0) {
				for (int i = 0; i < onlyCliqueApplied; i++) {
					g.restore(cost_red_blue, cost_none_blue);

				}
				
			}
			
			if (applyRuleDomination || applyRuleOneDegree || applyOnlyClique || applyOnlyPeri) {

				for (Vertex u : g.unlabled.values()) {
					u.calculateCoreCost(g, cost_red_blue, cost_none_blue);
					u.calculatePeriCost(g, cost_red_blue, cost_none_blue);
				}

			}

			
//			for (Vertex u : g.unlabled.values()) {
//				u.calculateCoreCost(g, cost_red_blue, cost_none_blue);
//				u.calculatePeriCost(g, cost_red_blue, cost_none_blue);
//			}
			
			
			v.calculateCoreCost(g, cost_red_blue, cost_none_blue);
			v.calculatePeriCost(g, cost_red_blue, cost_none_blue);
			return false;

		}

	}

	// Vertex selection methods
	public static int selectNextMaxCost(Collection<Vertex> collection) {
		int index = -1;
		double maxCost = -1;
		for (Vertex v : collection) {

			if (v.getCoreCost() > maxCost) {
				index = v.id;
				maxCost = v.getCoreCost();
			} else if (v.getPeriCost() > maxCost) {
				index = v.id;
				maxCost = v.getPeriCost();
			}

		}

		return index;
	}

	public static int selectNextAvgCost(HashMap<Integer, Vertex> unlabled) {
		int index = -1;
		double avgCost = -1;

		for (Vertex v : unlabled.values()) {
			if (v.getCoreCost() + v.getPeriCost() > 2 * avgCost) {
				index = v.id;
				avgCost = (v.getCoreCost() + v.getPeriCost()) / 2;
			}
		}

		if (index == -1) {
			for (Vertex v : unlabled.values()) {
//				System.out.println(v.id);
//				System.out.println("Core " + v.getCoreCost() + "Peri " + v.getPeriCost());
			}
		}

		
		return index;
	}

	// reduction rule methods
	public static boolean solvableMinCost(Graph g, double cost_red_blue, double cost_none_blue,
			HashMap<Integer, Vertex> unlabled, double k) {
		boolean solvable = true;

		for (Vertex v : unlabled.values()) {

//			v.calculateCoreCost(g, cost_red_blue, cost_none_blue);
//			v.calculatePeriCost(g, cost_red_blue, cost_none_blue);
			if (v.getCoreCost() > k && v.getPeriCost() > k)
				return false;
		}

		return solvable;
	}

	public static boolean solvableTotalCost(Graph g, double cost_red_blue, double cost_none_blue,
			HashMap<Integer, Vertex> unlabled, double k) {

		double minCost = 0;
		for (Vertex v : unlabled.values()) {
//			v.calculateCoreCost(g, cost_red_blue, cost_none_blue);
//			v.calculatePeriCost(g, cost_red_blue, cost_none_blue);
			double coreCost = v.getCoreCost();
			double periCost = v.getPeriCost();
			if (coreCost < periCost)
				minCost += coreCost;
			else
				minCost += periCost;
		}

		return minCost <= k;
	}

	// TODO change to splitgraph check:
	/*
	 * degreeSequenz decreasing maximize clique size, check if only blue edges if
	 * not check if one v covers all red edges then move to I (if possible) if only
	 * one red edges try both
	 */

	// TODO change costs here!! nothing to do right?
	public static Boolean isSolution(Graph g) {

		ArrayList<Vertex> sorted = sortByDegree(g);

		Integer split = isSplitGraph(sorted);
		Integer cliqueSize = split + 1;
		if (split == -1) {
			// System.out.println("no splitgraph");
			return false;
		}

		// System.out.println(split);

		Integer endDegree = sorted.get(split).degree;

		ArrayList<Integer> redNeighbors = new ArrayList<Integer>();
		Boolean redEdge = false;
		Boolean anyMovable = true;

		if (endDegree > cliqueSize - 1) // can only move 1 vertex out of C iff degree=enddegree=|C|-1
			anyMovable = false;

		for (Integer i = 0; i <= split; i++) {
			Vertex v = sorted.get(i);

			for (Integer j = i + 1; j <= split; j++) {

				Vertex u = sorted.get(j);

				// since the graph is splitgraph, the edges exist, only need to check if red.

				if (v.red.containsKey(u.id) || v.red_permanent.containsKey(u.id)) {
					redEdge = true;
					if (!anyMovable)
						return false;
					else if (v.degree > cliqueSize - 1 && u.id > cliqueSize - 1)
						return false;
					else if (redNeighbors.isEmpty()) {
						if (v.degree == cliqueSize - 1)
							redNeighbors.add(v.id);
						if (u.degree == cliqueSize - 1)
							redNeighbors.add(u.id);

					} else if (!redNeighbors.contains(v.id) && !redNeighbors.contains(u.id)) {
						return false; // only 1 red edge allowed
					} else if (redNeighbors.contains(v.id)) { // every edge only visited once so cant contain v & u
						redNeighbors.clear(); // clear incase of 2 entrys
						redNeighbors.add(v.id); // readd v since its the only movable vertex
					} else if (redNeighbors.contains(u.id)) {
						redNeighbors.clear();
						redNeighbors.add(u.id);
					}

				}
			}

		}

		System.out.println("Degree of the last Vertex in C " + endDegree);
		System.out.println("Index of the last Vertex in C " + split);
		if(!redNeighbors.isEmpty()) System.out.println("Vertex that has to be moved to I " + redNeighbors.get(0));
		for (Vertex v : sorted) {
			System.out.println(v.id + " " + v.lable);
		}
		return true;
	}

	/**
	 * vertices ordered by descending degree. v1 >= v2 >= vn.. let m be the biggest
	 * possible i so that d(vi) >= i-1 : g is splitgraph iff sum of degrees upto m =
	 * m*(m-1) + sum of degrees after m.
	 */

	/**
	 * 
	 * @param sortedVertices
	 * @param g
	 * @return -1 if not split graph, index last clique vertex else
	 */
	public static Integer isSplitGraph(ArrayList<Vertex> sortedVertices) {

		Integer m = -1; // index of last vertex in Core
		Integer currentSum = 0;
		Integer followingSum = 0;

		for (Integer i = 1; i <= sortedVertices.size(); i++) {
			Integer degree = sortedVertices.get(i - 1).degree; // v has index i-1

			if (degree < i - 1) {
				m = i - 1;

				for (Integer j = i; j <= sortedVertices.size(); j++) {
					followingSum += sortedVertices.get(j - 1).degree;
				}
				if (currentSum == (m * (m - 1) + followingSum))
					return m - 1;
				else
					return -1;
			}
			currentSum += degree;

		}

		return -1;
	}

	public static ArrayList<Vertex> sortByDegree(Graph g) {

		Collection<Vertex> values = g.vertices.values();

		ArrayList<Vertex> sorted = new ArrayList<Vertex>(values);

		Collections.sort(sorted, Comparator.comparing(Vertex::getDegree).reversed());

		return sorted;
	}

	
	
	public static ArrayList<Vertex> applyOnlyClique(Graph g, double k, double cost_red_blue, double cost_none_blue){
		ArrayList<Vertex> moveToClique = new ArrayList<Vertex>();
		
		for(Vertex v:g.unlabled.values()) {
//			v.calculatePeriCost(g, cost_red_blue, cost_none_blue);
			if(v.getPeriCost()>k) moveToClique.add(v);
		}
		
		return moveToClique;
	}
	
	public static ArrayList<Vertex> applyOnlyPeri(Graph g, double k, double cost_red_blue, double cost_none_blue){
		ArrayList<Vertex> moveToPeri = new ArrayList<Vertex>();
		
		for(Vertex v:g.unlabled.values()) {
//			v.calculateCoreCost(g, cost_red_blue, cost_none_blue);
			if(v.getCoreCost()>k) moveToPeri.add(v);
		}
		
		return moveToPeri;
	}
	
	
	public static ArrayList<Vertex> applyDegreeOneNeighbor(Vertex v, Graph g) {
		ArrayList<Vertex> moveToPeri = new ArrayList<Vertex>();
		Boolean highDegreeVertex = false;

		for (Vertex u : g.vertices.values()) {
			if (u.id != v.id && u.degree > 1) {
				highDegreeVertex = true;
				break;
			}
		}
		if (!highDegreeVertex)
			return moveToPeri;

		for (Vertex u : v.blue.values()) {
			if (!g.unlabled.containsKey(u.id))
				continue;
			if (u.degree == 1) {

				moveToPeri.add(u);

			}
		}

		for (Vertex u : v.red.values()) {
			if (!g.unlabled.containsKey(u.id))
				continue;
			if (u.degree == 1) {

				moveToPeri.add(u);

			}
		}

		return moveToPeri;
	}

	public static ArrayList<Vertex> applyDominatedNeighborhood(Vertex u, Graph g) {

		ArrayList<Vertex> toCore = new ArrayList<Vertex>();

		for (Vertex v : g.unlabled.values()) {
			boolean vDominates = true;

			for (Vertex w : u.blue.values()) {
				if (w.id == u.id || w.id == v.id)
					continue;
				if (!v.blue.containsKey(w.id)) {
					vDominates = false;
					break;
				}
			}
			for (Vertex w : u.blue_permanent.values()) {
				if (w.id == u.id || w.id == v.id)
					continue;
				if (!vDominates)
					break;
				if (!v.blue.containsKey(w.id)) {
					vDominates = false;
				}
			}
			for (Vertex w : u.red.values()) {
				if (w.id == u.id || w.id == v.id)
					continue;
				if (!vDominates)
					break;
				if (!v.blue.containsKey(w.id)) {
					vDominates = false;
				}
			}
			for (Vertex w : u.red_permanent.values()) {
				if (w.id == u.id || w.id == v.id)
					continue;
				if (!vDominates)
					break;
				if (!v.blue.containsKey(w.id)) {
					vDominates = false;
				}
			}

			if (vDominates)
				toCore.add(v);
		}

		return toCore;

	}

//	/**
//	 * Checks if Graph g is a TCSG.
//	 * 
//	 * @param g
//	 * @return
//	 */
//	public static Boolean isSolution(Graph g) {
//
//		Graph g_copy = g.copyGraph();
//
//		if (g_copy.unlabled.isEmpty()) {
//			System.out.println("No unlabled vertices left");
//			return true;
//		}
//
//		HashSet<Integer> decidable = new HashSet<Integer>();
//
//		for (Vertex v : g_copy.unlabled.values()) {
//			if ((v.blue_permanent.isEmpty() && v.blue.isEmpty()) || v.degree == 0) {
//				decidable.add(v.id);
//				System.out.println("decidable vertex: " + v.id);
//			}
//		}
//
//		HashSet<Integer> removeUnlabled = new HashSet<Integer>();
//
//		// MAKE SURE TO CHECK IF CORE IS POSSIBLE
//		for (Integer id : decidable) {
//
//			Vertex v = g_copy.unlabled.get(id);
//
//			if (v.degree == 0) {
//				System.out.println("this should not happen yet");
//				// TODO check periphery set for no edges
//				v.lable = Lable.PERIPHERY;
//				removeUnlabled.add(v.id);
//			}
//
//			// If only red neighbors v is in I & and all its neighbors are in C unless |C| =
//			// 1..
//			if (v.blue_permanent.isEmpty() && v.blue.isEmpty()) {
//				v.lable = Lable.PERIPHERY;
//				removeUnlabled.add(v.id);
//				for (Vertex u : v.red.values()) {
//					if (u.lable == Lable.PERIPHERY)
//						return false;
//					if (u.lable == Lable.NONE) {
//						u.lable = Lable.CORE;
//						removeUnlabled.add(u.id);
//						g_copy.core.add(u.id);
//					}
//				}
//				for (Vertex u : v.red_permanent.values()) {
//					if (u.lable == Lable.PERIPHERY)
//						return false;
//					if (u.lable == Lable.NONE) {
//						u.lable = Lable.CORE;
//						removeUnlabled.add(u.id);
//						g_copy.core.add(u.id);
//					}
//				}
////				for (Integer core_id : g_copy.core) {
////					if(!v.blue.containsKey(core_id) && !v.blue_permanent.containsKey(core_id)) return false;
////				}
//			}
//
//			// what else todo with unlabled?
//
//		}
//
//		for (Integer id : removeUnlabled) {
//			g_copy.unlabled.remove(id);
//		}
//
//		// Lableing of neighbors of labled vertices
//		// evtl better to store labled nodes somewhere?
//		for (Vertex v : g_copy.vertices.values()) {
//
//			if (v.lable == Lable.NONE) {
//				
////TODO core wenn nachbarn in periph. && roter nachbar in core -> periph
//				
//				continue;
//
//			}
//
//			// red edges are only allowed between C/I
//			for (Vertex u : v.red.values()) {
//
//				if (v.lable == Lable.CORE) {
//					if (u.lable == Lable.CORE)
//						return false;
//					if (u.lable == Lable.NONE) {
//						u.lable = Lable.PERIPHERY;
//						g_copy.unlabled.remove(u.id);
//						g_copy.periphery.add(u.id);
//					}
//				}
//
//				if (v.lable == Lable.PERIPHERY) {
//					if (u.lable == Lable.PERIPHERY)
//						return false;
//					if (u.lable == Lable.NONE) {
//						u.lable = Lable.CORE;
//						g_copy.unlabled.remove(u.id);
//						g_copy.core.add(u.id);
//					}
//				}
//
//			}
//
//			for (Vertex u : v.red_permanent.values()) {
//				if (v.lable == Lable.CORE) {
//					if (u.lable == Lable.CORE)
//						return false;
//					if (u.lable == Lable.NONE) {
//						u.lable = Lable.PERIPHERY;
//						g_copy.unlabled.remove(u.id);
//						g_copy.periphery.add(u.id);
//					}
//				}
//
//				if (v.lable == Lable.PERIPHERY) {
//					if (u.lable == Lable.PERIPHERY)
//						return false;
//					if (u.lable == Lable.NONE) {
//						u.lable = Lable.CORE;
//						g_copy.unlabled.remove(u.id);
//						g_copy.core.add(u.id);
//					}
//				}
//
//			}
//
//			// blue edges in C or between C/I
//			for (Vertex u : v.blue.values()) {
//				if (v.lable == Lable.CORE) {
//					// TODO
//				}
//				if (v.lable == Lable.PERIPHERY) {
//					if (u.lable == Lable.PERIPHERY)
//						return false;
//					if (u.lable == Lable.NONE) {
//						u.lable = Lable.CORE;
//						g_copy.unlabled.remove(u.id);
//						g_copy.core.add(u.id);
//					}
//				}
//			}
//
//			for (Vertex u : v.blue_permanent.values()) {
//				if (v.lable == Lable.CORE) {
//					// TODO
//				}
//				if (v.lable == Lable.PERIPHERY) {
//					if (u.lable == Lable.PERIPHERY)
//						return false;
//					if (u.lable == Lable.NONE) {
//						u.lable = Lable.CORE;
//						g_copy.unlabled.remove(u.id);
//						g_copy.core.add(u.id);
//					}
//				}
//			}
//
//			for (Vertex u : v.forbidden.values()) {
//				if (v.lable == Lable.CORE) {
//					if (u.lable == Lable.CORE)
//						return false;
//					if (u.lable == Lable.NONE) {
//						u.lable = Lable.PERIPHERY;
//						g_copy.unlabled.remove(u.id);
//						g_copy.periphery.add(u.id);
//					}
//				}
//				if (v.lable == Lable.PERIPHERY) {
//					// TODO
//				}
//
//			}
//
//		}
//
//		// hier durch alle durch oder lieber oben drin?
//		for (Integer v_id : g_copy.core) {
//			Vertex v = g_copy.vertices.get(v_id);
//			for (Integer u_id : g_copy.core) {
//				if (u_id == v_id)
//					continue;
//				if (!v.blue.containsKey(u_id) && !v.blue_permanent.containsKey(u_id))
//					return false;
//
//			}
//
//		}
//
//		for (Integer v_id : g_copy.periphery) {
//			Vertex v = g_copy.vertices.get(v_id);
//			for (Integer u_id : g_copy.periphery) {
//				if (u_id == v_id)
//					continue;
//				if (v.blue.containsKey(u_id) || v.blue_permanent.containsKey(u_id) || v.red.containsKey(u_id)
//						|| v.red_permanent.containsKey(u_id))
//					return false;
//			}
//
//		}
//
//		/**
//		 * nur rote nachbarn /wenig blauen nachbarn ggf roten grad zählen grad von
//		 * independent set <= |C|
//		 * 
//		 * 
//		 * 
//		 * split graph algo anschauen
//		 */
//
//		
//		
//		if (!g_copy.unlabled.isEmpty())
//			return false;
//
//		for (Vertex v : g_copy.vertices.values()) {
//			System.out.println(v.id + " " + v.lable.name());
//		}
//		return true;
//	}

}
