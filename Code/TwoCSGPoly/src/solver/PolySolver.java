package solver;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;

import data.Graph;
import data.Vertex;

public class PolySolver {
	
	// args[0] path to input, args[1] path to output

	public static void main(String[] args) {
	
		long start = System.currentTimeMillis();
		
		String path = args[0];
		Graph g = Graph.readFromCSV(path);
		
//		System.out.println("graph read, start calculating");
		
		ArrayList<Vertex> sortedVertices = sortByWeightedDegree(g);
		ArrayList<Integer> costs = new ArrayList();
		
		for(int i=0; i<=g.size; i++) {
			Integer cost = calculateCost(sortedVertices, i);
			costs.add(cost);
		}
		Integer solutionSize = 0;
		Integer solutionCost = 0;
		
		for(int i=0; i<costs.size(); i++) {
			if(i==0) solutionCost = costs.get(i);
			
			if(costs.get(i)<solutionCost) {
				solutionSize = i;
				solutionCost = costs.get(i);
			}
		}
		
		long end = System.currentTimeMillis();
		String outputFile = args[1];

		Writer output;
		try {
			output = new BufferedWriter(new FileWriter(outputFile, true));
			output.append('\n');
			output.append(path + " " + solutionCost + " " + (end - start));

			output.close();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
//		System.out.println("clique size is: " + solutionSize);
//		System.out.println("cost of the solution is: " + solutionCost);

	}

	public static Integer calculateCost(ArrayList<Vertex> sorted, int cliqueSize) {
		Integer cost = 0;
		
		for(int i=0; i<sorted.size(); i++) {
			
			for(int j=i+1; j<sorted.size(); j++) {
				
				if(j<cliqueSize) {			//both in clique since j>i.
					
					Vertex u = sorted.get(i);
					Vertex v = sorted.get(j);
					
					if(u.blue.containsKey(v.id)) continue;		//blue edge & both in clique = 0 cost.
					else if(u.red.containsKey(v.id)) cost += 1;	//red edge recolor cost 1.
					else cost += 2;								//no edge -> blue cost 2.
					
				}else if(i>=cliqueSize) {	//both in independent set.
					
					Vertex u = sorted.get(i);
					Vertex v = sorted.get(j);
					
					if(u.blue.containsKey(v.id)) cost += 2;		
					else if(u.red.containsKey(v.id)) cost += 1;	
					
					
				}
				
			}
			
		}
			
		
		
		return cost;
	}
	
	
	public static ArrayList<Vertex> sortByWeightedDegree(Graph g) {

		Collection<Vertex> values = g.vertices.values();

		ArrayList<Vertex> sorted = new ArrayList<Vertex>(values);

		Collections.sort(sorted, Comparator.comparing(Vertex::getWeightedDegree).reversed());

		return sorted;
	}
	
	
	
}
