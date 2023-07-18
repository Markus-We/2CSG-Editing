package data;


//class used for storage of changes
public class Edge {

	public Integer v1, v2, newEdgeType, oldEdgeType;

	/**
	 *
	 * @param v1 vertex 1
	 * @param v2 vertex 2
	 * @param edgeType 0 = none-Edge, 1 = red Edge, 2 = blue Edge
	 */
	public Edge(Integer v1, Integer v2, Integer newEdgeType, Integer oldEdgeType) {
		this.v1 = v1;
		this.v2 = v2;
		this.newEdgeType = newEdgeType;
		this.oldEdgeType = oldEdgeType;
	}

	public Integer getV1() {
		return v1;
	}

	public void setV1(Integer v1) {
		this.v1 = v1;
	}

	public Integer getV2() {
		return v2;
	}

	public void setV2(Integer v2) {
		this.v2 = v2;
	}

	public Integer getNewEdgeType() {
		return newEdgeType;
	}

	public void setNewEdgeType(Integer newEdgeType) {
		this.newEdgeType = newEdgeType;
	}

	public Integer getOldEdgeType() {
		return oldEdgeType;
	}

	public void setOldEdgeType(Integer oldEdgeType) {
		this.oldEdgeType = oldEdgeType;
	}



}
