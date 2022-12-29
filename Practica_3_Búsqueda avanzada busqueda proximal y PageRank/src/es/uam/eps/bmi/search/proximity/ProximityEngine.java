package es.uam.eps.bmi.search.proximity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import es.uam.eps.bmi.search.AbstractEngine;
import es.uam.eps.bmi.search.index.Index;
import es.uam.eps.bmi.search.index.structure.Posting;
import es.uam.eps.bmi.search.index.structure.positional.PositionalPosting;
import es.uam.eps.bmi.search.index.structure.positional.PositionsIterator;
import es.uam.eps.bmi.search.ranking.SearchRanking;
import es.uam.eps.bmi.search.ranking.impl.RankingImpl;

public class ProximityEngine extends AbstractEngine {

	private String termino;
	private boolean literal;
	private boolean litRes;

	private String[] parse;
	
	public ProximityEngine(Index idx) {
		super(idx);
	}

	@Override
	public SearchRanking search(String query, int cutoff) throws IOException {

		this.literal = false;
		
		// Comprueba si el indice es literal.
		if (query.startsWith("\"") && query.endsWith("\"")) {
			
			literal = true;
			
			query = query.substring(1);
		}
		
		this.parse = query.toLowerCase().split("\\P{Alpha}+");
		
		RankingImpl ranking = new RankingImpl(super.index, cutoff);
		
		HashMap<String, Iterator<Posting>> postings = new HashMap<>();
		HashMap<String, PositionsIterator> listaPosiciones = new HashMap<>();
		
		HashMap<String, ArrayList<Posting>> small = new HashMap<>();
		
		for (String term : parse) {

			postings.put(term, super.index.getPostings(term).iterator() );
		}
		
		
		int docID = -1;
		int cont = 0;
		boolean end = false;

		while(true) {

			while (true) {
				
				if (cont == postings.size()) break;
				
				for (String key : postings.keySet()) {
					
					if (!postings.get(key).hasNext()) {
						end = true;
						break;
					}
					
					PositionalPosting comp = (PositionalPosting) postings.get(key).next();
					
					while (comp.getDocID() < docID) {
						comp = (PositionalPosting) postings.get(key).next();
					}
					
					// Si el posting es del documento que se esta analizando.
					if (comp.getDocID() == docID) {
					
						cont++;
					
						listaPosiciones.put(key, (PositionsIterator) comp.iterator());

						if (cont == postings.size()) break;
					}
					
					// Si es otro DocID.
					else if (comp.getDocID() > docID) {
						
						cont = 1;
						docID = comp.getDocID();
						
						listaPosiciones.put(key, (PositionsIterator) comp.iterator());
					}
				}
				
				if (end) break;
			}
			
			if (end) break;
			
			// Algoritmo posicional
		
			int a, b;
			double check, score = 0;
			
			b = getMaxPosition(listaPosiciones);
			litRes = true;
			
			while(true) {
				
				a = avanzaHastaValor(listaPosiciones, b);
				check = calculaScore(a, b, parse.length);
				
				if (!literal || (check == 1 && litRes)) score += check;
				
				b = listaPosiciones.get( this.termino ).nextAfter(a);
				litRes = true;
				
				if (listaPosiciones.get( this.termino ).hasNext() == false) break;
			}
			
			if (score != 0) ranking.add(docID, score);
			
			cont = 0;
		}
		
		return ranking;
	}
	
	
	
	
	private int getMaxPosition(HashMap<String, PositionsIterator> listaPosiciones) {
		
		int max = -1;
		int vActual = -1;
		
		for(PositionsIterator p : listaPosiciones.values()) {
			
			if (!p.hasNext()) return -1;

			vActual = p.next();

			if (vActual > max) max = vActual;
		}

		return max;
	}
	
	
	
	
	private int avanzaHastaValor(HashMap<String, PositionsIterator> listaPosiciones, int valor) {
		
		int min = -1;
		String minTerm = null;
		int aux, anterior = -1;
		
		for (String key : this.parse) {
			
			PositionsIterator p = listaPosiciones.get(key);
			
			aux = p.nextBefore(valor);
			
			if (min == -1) {
				
				min = aux;
				minTerm = key;
				anterior = aux;
				
			} else if (aux < min) {
				
				min = aux;
				minTerm = key;
				
				if (this.literal) this.litRes = false;
				
			} else if (this.literal) {
				
				if (aux < anterior) this.litRes = false;

				anterior = aux;
			}
		}
		
		this.termino = minTerm;
		
		return min;
	}
	
	
	private double calculaScore(int a, int b, int size) {
		
		return 1 / ((double)(b - a - size + 2));
	}
	
	
}
