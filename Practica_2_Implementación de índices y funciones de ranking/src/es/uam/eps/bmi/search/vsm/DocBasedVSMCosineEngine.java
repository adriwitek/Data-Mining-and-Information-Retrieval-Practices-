package es.uam.eps.bmi.search.vsm;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.PriorityQueue;

import es.uam.eps.bmi.search.index.Index;
import es.uam.eps.bmi.search.index.structure.Posting;
import es.uam.eps.bmi.search.index.structure.impl.PostingMinHeap;
import es.uam.eps.bmi.search.ranking.SearchRanking;
import es.uam.eps.bmi.search.ranking.impl.RankingImpl;

public class DocBasedVSMCosineEngine extends AbstractVSMEngine {

	public DocBasedVSMCosineEngine(Index index) throws IOException {
        super(index);
    }
	
	
	@Override
	public SearchRanking search(String query, int cutoff) throws IOException {
		
		// Parseamos la query.
		String[] terminos = super.parse(query);
		
		RankingImpl ranking = new RankingImpl(index, cutoff);
		
		Integer nDocumentosIndex = index.numDocs();
		double moduloVectorQuery = Math.sqrt(terminos.length );
		
		
		
		// Para cada doc que iterenmos
		PostingMinHeap pActual;
		int docIDActual = 0;
		double scoreDocActual = 0;
		
		
		//Minheap de tam. numero de terminos
        PriorityQueue<PostingMinHeap> minHeapPostings = new PriorityQueue<>(terminos.length, new PostingMinHeap());

		
		// Por cada termino se inicializamos el iterador.
        HashMap<String, Iterator<Posting>> hmapIterablesPostingsTerminos = new HashMap<>();
        
		for(String termino: terminos) {
			
			hmapIterablesPostingsTerminos.put( termino, super.index.getPostings(termino).iterator()  );
			
			// Tambien llenamos el minheap.
			Posting p = hmapIterablesPostingsTerminos.get(termino).next();
			minHeapPostings.add( new PostingMinHeap(p.getDocID(), p.getFreq(), termino) );
		}

		
		// Recorremos las listas de postings.
		docIDActual = minHeapPostings.peek().getDocID();
		
		while(!minHeapPostings.isEmpty()  ) {
			
			// Extraemos del minheap.
			pActual = minHeapPostings.poll();
			
			if(docIDActual != pActual.getDocID()) {
				
				// Dividimos entre el modulo.
				scoreDocActual = scoreDocActual / (moduloVectorQuery * index.getDocNorm(docIDActual));
				ranking.add(docIDActual, scoreDocActual);
				
				docIDActual = pActual.getDocID();
				scoreDocActual = 0; 
			}
			
			// Equivale al poducto escalar del tf-idf con el vector de la query:
			scoreDocActual += AbstractVSMEngine.tfidf(pActual.getFreq(), index.getDocFreq(pActual.getTerminoOrigen()), nDocumentosIndex);
			
			
			//Anniadimos un POstingMinHeap al Min hepap(si se puede)
			//De la misma lista de terminos
			if(hmapIterablesPostingsTerminos.get(pActual.getTerminoOrigen()).hasNext() ) { // Insetamos otro de la misma lista de postings
				Posting p = hmapIterablesPostingsTerminos.get(pActual.getTerminoOrigen()).next();
				minHeapPostings.add(new PostingMinHeap(p.getDocID(), p.getFreq(), pActual.getTerminoOrigen()));	
			}
				
		}
	
		scoreDocActual = scoreDocActual / (moduloVectorQuery * index.getDocNorm(docIDActual));
		ranking.add(docIDActual, scoreDocActual);
				
		return ranking;
				
	}
	
	
	
	
}
