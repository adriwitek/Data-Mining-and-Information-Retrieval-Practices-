package es.uam.eps.bmi.search.graph;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Scanner;

import es.uam.eps.bmi.search.index.DocumentFeatureMap;

public class PageRank implements DocumentFeatureMap {
	
	HashMap<Integer, Double> score;
	HashMap<Integer, String> terms;
	
	public PageRank(String graphPath, double r, int k) throws FileNotFoundException {

		this.terms = new HashMap<>();
		
		Graph<Integer> graph = loadGraph(graphPath);
		
		this.score = calculateScore(graph, r, k);
	}

	@Override
	public String getDocPath(int docID) throws IOException {
		return this.terms.get(docID);
	}

	@Override
	public double getDocNorm(int docID) throws IOException {
		throw new UnsupportedOperationException("Operación no soportada.");
	}

	@Override
	public int numDocs() {
		return this.score.size();
	}

	@Override
	public double getValue(int docId) {
		return this.score.get(docId);
	}
	
	
	private Graph<Integer> loadGraph(String graphPath) throws FileNotFoundException {
		
		Graph<Integer> graph = new Graph<>();
		HashMap<String, Integer> terms = new HashMap<>();
		
		Integer source, destination;
		
		File file = new File(graphPath);
		Scanner reader = new Scanner(file);
		
		while (reader.hasNextLine()) {
			
			// TODO - toLowerCase() ?
			String[] line = reader.nextLine().split("\t");
			
			// Lineas con menos de 2 elementos ya estan introducidos.
			if (line.length != 2) continue;
			
			source      = terms.get(line[0]);
			
			if (source == null) {
				source = terms.size();
				terms.put(line[0], source);
			}
			
			
			destination = terms.get(line[1]);
			
			if (destination == null) {
				
				destination = terms.size();
				terms.put(line[1], destination);
			}
			
			graph.addEdge(source, destination);
		}
		
		for (String key: terms.keySet()) {
			
			this.terms.put(terms.get(key), key);
		}

		reader.close();
		
		return graph;
	}
	
	
	private HashMap<Integer, Double> calculateScore(Graph<Integer> graph, double r, int maxK) {
		
		HashMap<Integer, Double> score = new HashMap<>();
		HashMap<Integer, Double> copy;
		
		int k, size = graph.numVertices();
		double value = 1.0 / size;
		
		// Inicializamos (k = 1)
		for (k = 0; k < size; k++) {
			
			score.put(k, value);
		}
		
		// Precalculamos valores.
		double calc = r / size;
		double Rinv = 1 - r;
		double media;

		for (k = 2; k < maxK; k++) {
			
			copy = new HashMap<>(score);
			
			for (Integer docID : score.keySet()) {
				
				value = 0;
				
				// Calculamos la suma
				for (Integer key : graph.getEdgesTo(docID)) {
					
					value += copy.get(key) / graph.getSalientes(key);
				}

				// Calculamos el resultado.
				score.put(docID, calc + Rinv * value);
			}
			
			// Normalizamos para dejar la suma a 1.
			media = 0;

			for (Integer docID : score.keySet()) {
				media += score.get(docID);
			}
			
			media = (1 - media) / size;
			
			// Aplicamos la normalización.
			for (Integer docID : score.keySet()) {
				score.put(docID, score.get(docID) + media);
			}
		}
		
		return score;
	}

}
