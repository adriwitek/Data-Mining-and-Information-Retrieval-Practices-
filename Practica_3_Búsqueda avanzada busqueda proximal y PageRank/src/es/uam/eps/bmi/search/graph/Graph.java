package es.uam.eps.bmi.search.graph;


import java.util.*; 

public class Graph<T> { 

	// Estructura del grafo.
	private Map<T, List<T>> map;
	private Map<T, Integer> size;
	
	private int numEdges;
	
	public Graph() {
		
		this.map  = new HashMap<>();
		this.size = new HashMap<>();

		this.numEdges = 0;
	}


	// Aniade un vertice al grafo. 
	public void addVertice(T key) {
		
		map.put(key, new LinkedList<T>());
		size.put(key, 0);
	} 
	
	
	// Aniade una conexion entre vertices.
	public void addEdge(T source, T destination) {

		// Comprobación de errores.
		if (!map.containsKey(source)) addVertice(source);
		if (!map.containsKey(destination)) addVertice(destination);

		map.get(destination).add(source);
		size.put(source, size.get(source) + 1);
	} 
	
	
	// This function gives the count of vertices 
	public int numVertices() {
		return this.map.keySet().size();
	} 
	

	// Devuelve el numero de edges.
	public int numEdges() {
		return this.numEdges;
	} 


	// Devuelve los nodos que le conectan.
	public List<T> getEdgesTo(T destination) {
		return this.map.get(destination);
	}
	
	
	// Devuelve los salientes del nodo.
	public int getSalientes(T source) {
		return this.size.get(source);
	}


	// Imprime el grafo. 
	@Override
	public String toString() 
	{ 
		StringBuilder builder = new StringBuilder(); 

		for (T v : map.keySet()) { 
			builder.append(v.toString() + "\t"); 
			for (T w : map.get(v)) { 
				builder.append(w.toString() + " "); 
			} 
			builder.append("\n"); 
		} 

		return (builder.toString()); 
	} 
} 
