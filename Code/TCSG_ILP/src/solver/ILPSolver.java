package solver;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;

import data.Graph;
import data.Vertex;
import gurobi.*;

public class ILPSolver {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		Long startTime = System.currentTimeMillis();
		long timeLimit = 300000;
		long endTime = startTime + timeLimit;

		String path = args[0];
		String out_path = args[1];
		double cost_red_blue = Double.parseDouble(args[2]);
		double cost_none_blue = Double.parseDouble(args[3]);

		Graph g = Graph.readFromCSV(path);

		try {

			// Model
			GRBEnv enviroment = new GRBEnv();

			GRBModel model = new GRBModel(enviroment);

			GRBVar[][] v = new GRBVar[g.size][2];

			ArrayList<Integer> ids = new ArrayList<>(g.vertices.keySet());
			for (int i = 0; i < g.size; i++) {

				v[i][0] = model.addVar(0, 1, 0, GRB.BINARY, i + "in_C");
				v[i][1] = model.addVar(0, 1, 0, GRB.BINARY, i + "in_I");

			}

			ArrayList<Vertex> vrts = new ArrayList<>(g.vertices.values());

			GRBVar[][] cliqueEdges = new GRBVar[g.size][g.size];
			GRBVar[][] indepSetEdges = new GRBVar[g.size][g.size];

			for (int i = 0; i < g.size; i++) {

				for (int j = i + 1; j < g.size; j++) {

					int v1ID = ids.get(i);
					int v2ID = ids.get(j);
					Vertex v1 = g.vertices.get(v1ID);
					Vertex v2 = g.vertices.get(v2ID);

					if (v1.blue.containsKey(v2ID)) { // add var for blue edge

						indepSetEdges[i][j] = model.addVar(0, 1, cost_none_blue, GRB.BINARY,
								"blueIS_" + v1ID + "_" + v2ID);

					} else if (v1.red.containsKey(v2ID)) {

						indepSetEdges[i][j] = model.addVar(0, 1, 1, GRB.BINARY, "redIS_" + v1ID + "_" + v2ID);
						cliqueEdges[i][j] = model.addVar(0, 1, cost_red_blue, GRB.BINARY, "redC_" + v1ID + "_" + v2ID);
					} else {

						cliqueEdges[i][j] = model.addVar(0, 1, cost_none_blue, GRB.BINARY, "nonC_" + v1ID + "_" + v2ID);

					}

				}

			}

			model.set(GRB.IntAttr.ModelSense, GRB.MINIMIZE);

			// Constraints

			// Vertex exactly one label
			for (int i = 0; i < g.size; i++) {
				GRBLinExpr expr = new GRBLinExpr();

				expr.addTerm(1, v[i][0]);
				expr.addTerm(1, v[i][1]);

				model.addConstr(expr, GRB.EQUAL, 1, "constr_v" + i);

			}

			// Vertex and edge alignment

			for (int i = 0; i < g.size; i++) {

				for (int j = i + 1; j < g.size; j++) {

					int v1ID = ids.get(i);
					int v2ID = ids.get(j);
					Vertex v1 = g.vertices.get(v1ID);
					Vertex v2 = g.vertices.get(v2ID);

					if (v1.blue.containsKey(v2ID)) { // add constraint for blue edge
														// costVar = 1 iff v1 & v2 in IS
						// if blue edge is present in IS we have to pay for deletion.
						GRBLinExpr expr = new GRBLinExpr();
						expr.addTerm(1, v[i][1]);
						expr.addTerm(1, v[j][1]);
						expr.addConstant(-1);

						GRBLinExpr expr2 = new GRBLinExpr();
						expr2.addTerm(1, indepSetEdges[i][j]);

						model.addConstr(expr2, GRB.GREATER_EQUAL, expr, "blue_in_IS_" + v1ID + "_" + v2ID);

					} else if (v1.red.containsKey(v2ID)) {

						// if red edge is present in IS we have to pay for deletion.
						GRBLinExpr expr = new GRBLinExpr();
						expr.addTerm(1, v[i][1]);
						expr.addTerm(1, v[j][1]);
						expr.addConstant(-1);

						GRBLinExpr expr2 = new GRBLinExpr();
						expr2.addTerm(1, indepSetEdges[i][j]);

						model.addConstr(expr2, GRB.GREATER_EQUAL, expr, "red_in_IS" + v1ID + "_" + v2ID);

						// if red edge is present in Clique we have to pay for recolor.
						GRBLinExpr exprC = new GRBLinExpr();
						exprC.addTerm(1, v[i][0]);
						exprC.addTerm(1, v[j][0]);
						exprC.addConstant(-1);

						GRBLinExpr exprC2 = new GRBLinExpr();
						exprC2.addTerm(1, cliqueEdges[i][j]);

						model.addConstr(exprC2, GRB.GREATER_EQUAL, exprC, "red_in_IS" + v1ID + "_" + v2ID);

					} else {

						GRBLinExpr expr = new GRBLinExpr();
						expr.addTerm(1, v[i][0]);
						expr.addTerm(1, v[j][0]);
						expr.addConstant(-1);

						GRBLinExpr expr2 = new GRBLinExpr();
						expr2.addTerm(1, cliqueEdges[i][j]);

						model.addConstr(expr2, GRB.GREATER_EQUAL, expr, "non_in_IS" + v1ID + "_" + v2ID);

					}

				}

			}

			model.optimize();

			Writer output;
			try {
				output = new BufferedWriter(new FileWriter(out_path, true));

				int status = model.get(GRB.IntAttr.Status);

				if (status == GRB.Status.UNBOUNDED) {
					System.out.println("The model cannot be solved " + "because it is unbounded");

					output.append('\n');
					output.append(path + " is unbound");

				}

				else if (status == GRB.Status.OPTIMAL) {
					System.out.println("The optimal objective is " + model.get(GRB.DoubleAttr.ObjVal));

					output.append('\n');
					output.append(path + " " + model.get(GRB.DoubleAttr.ObjVal) + " "
							+ (System.currentTimeMillis() - startTime));

				}

				else if (status != GRB.Status.INF_OR_UNBD && status != GRB.Status.INFEASIBLE) {
					System.out.println("Optimization was stopped with status " + status);

					output.append('\n');
					output.append(path + " stopped with status " + status);

				}
				output.close();

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} catch (GRBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
