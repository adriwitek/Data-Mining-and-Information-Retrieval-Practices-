package es.uam.eps.bmi.search.vsm;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import es.uam.eps.bmi.search.index.Index;
import es.uam.eps.bmi.search.index.structure.Posting;
import es.uam.eps.bmi.search.index.structure.PostingsList;
import es.uam.eps.bmi.search.ranking.SearchRanking;
import es.uam.eps.bmi.search.ranking.impl.RankingImpl;

public class TermBasedVSMCosineEngine extends AbstractVSMEngine {
	
	public TermBasedVSMCosineEngine(Index index) {

		super(index);
	}

	@Override
	public SearchRanking search(String query, int cutoff) throws IOException {
		
		
		int numDocs = index.numDocs();
		double moduloVectorQuery ;
		double puntuacion = 0;
		
		RankingImpl ranking = new RankingImpl(index, cutoff);
		
		// Parseamos la query.
		String[] terminos = super.parse(query);

		// Creamos una estructura para guardar las puntuaciones.
		HashMap<Integer, Double> frecAcumuladas = new HashMap<>();
		

		// Para cada termino de la query.
		for (String termino : terminos) { //suponemos terminos no repetidos
			// Obtenemos la lista de postings de un termino.
			PostingsList listaPostings = super.index.getPostings(termino);
			long docFreq = index.getDocFreq(termino);
			
			// Para cada posting.
			for (Posting posting : listaPostings) {
				
				if( frecAcumuladas.get(posting.getDocID()) == null  ) {
					frecAcumuladas.put(posting.getDocID(),  AbstractVSMEngine.tfidf(posting.getFreq(), docFreq, numDocs));

				}else {
					frecAcumuladas.put(posting.getDocID(), frecAcumuladas.get(posting.getDocID()) + AbstractVSMEngine.tfidf(posting.getFreq(), docFreq, numDocs));

				} 
			}
		}
		
		// Calculamos modulo de la query
        moduloVectorQuery = Math.sqrt(terminos.length );

		Iterator<Entry<Integer, Double>> it = frecAcumuladas.entrySet().iterator();
		
	    while (it.hasNext()) {
	    	//dato es ya el producto escalar
	    	Entry<Integer, Double> dato = it.next();
	        // TODO - guardar "score" en algo ORDENADO. (O que se pueda ordenar)
	        puntuacion = dato.getValue() / (moduloVectorQuery * super.index.getDocNorm( dato.getKey() ));  
	        if (puntuacion > Double.NEGATIVE_INFINITY) ranking.add(dato.getKey(), puntuacion);    
	        
	    }

	    
	    
		return ranking;
	}
	
	

	
}
