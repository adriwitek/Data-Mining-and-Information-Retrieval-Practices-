package es.uam.eps.bmi.search.vsm;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.apache.lucene.index.IndexReader;

import es.uam.eps.bmi.search.AbstractEngine;
import es.uam.eps.bmi.search.index.lucene.LuceneIndex;
import es.uam.eps.bmi.search.ranking.SearchRanking;
import es.uam.eps.bmi.search.ranking.impl.VSMRanking;

public class VSMDotProductEngine extends AbstractEngine {

	private IndexReader indexReader;
	
	public VSMDotProductEngine(LuceneIndex luceneIndex) throws IOException {

		super(luceneIndex);
		
		this.indexReader = luceneIndex.getLuceneIndexReader();
	}

	@Override
	public SearchRanking search(String query, int cutoff) throws IOException {
		
        String[] consulta =  query.toLowerCase().split(" ");
        List<String> palabrasConsulta = Arrays.asList(consulta);
        
        TreeMap<Integer, Double> Scores = new TreeMap<>();
        // List<Entry<Integer, Double>> Scores = new ArrayList<>(map.entrySet());
        
        double score;

        for(int docNumber = 0; docNumber < this.indexReader.numDocs(); docNumber++) {
        	
        	score = this.productoEscalar(palabrasConsulta, docNumber, this.indexReader.numDocs());
        	
        	Scores.put(docNumber, score);
        }
        
        List<Entry<Integer, Double>> lista = new ArrayList<>(Scores.entrySet());

        return new VSMRanking(lista, super.index, cutoff);
	}
	

	private double productoEscalar(List<String> query, int docID, int numDocs) throws IOException {

		double sum = 0;
		
		for(String word : query) {

			double idf = idf(word, numDocs);
			double tf = tf(word, docID);
			
			sum += 1 * (idf * tf);
		}

		return sum;
	}
	
	
	private double tf(String word, int docID) throws IOException {
		
		long frec = super.index.getTermFreq(word, docID);
		
		if (frec > 0) return 1 + (Math.log(frec) / Math.log(2));

		return 0;
	}
	
	
	private double idf(String word, int numDocs) throws IOException {
		
		 if(super.index.getDocFreq(word) ==0) {
			 return 0;
		 }else{
			return Math.log( numDocs / super.index.getDocFreq(word)); 
		 }
		
	}

}
