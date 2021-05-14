
package es.uam.eps.bmi.search.vsm;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.HashMap;

import org.apache.lucene.index.IndexReader;

import es.uam.eps.bmi.search.AbstractEngine;
import es.uam.eps.bmi.search.index.lucene.LuceneIndex;
import es.uam.eps.bmi.search.ranking.SearchRanking;
import es.uam.eps.bmi.search.ranking.impl.VSMRanking;




public class VSMCosineEngine extends AbstractEngine{
	
	
	private IndexReader indexReader;
	
	public VSMCosineEngine(LuceneIndex luceneIndex) throws IOException {

		super(luceneIndex);
		
		this.indexReader = luceneIndex.getLuceneIndexReader();
	}

	

	@Override
	public SearchRanking search(String query, int cutoff) throws IOException {
		
        String[] consulta =  query.toLowerCase().split(" ");
        List<String> palabrasConsulta = Arrays.asList(consulta);
        HashMap<Integer, Double> hmap = new HashMap<Integer, Double>();
        TreeMap<Integer, Double> Scores = new TreeMap<>();
        
        double score;
        double moduloVectorQuery = 0;
        
        try
        {
        //Deserializamos el hasmap con los docIDs y los modulos de los vectores
        FileInputStream fis = new FileInputStream("docsVectorModules.ser");
        ObjectInputStream ois = new ObjectInputStream(fis);
        hmap = (HashMap) ois.readObject();
        
        ois.close();
        fis.close();
        }catch(IOException ioe)
        {
        	ioe.printStackTrace();
        	return null;
        }catch(ClassNotFoundException c)
        {
        	System.out.println("Class not found");
        	c.printStackTrace();
        	return null;
        }
        
        
        
        
        //Calculamos modulo de la query
        moduloVectorQuery = Math.sqrt(palabrasConsulta.size() );
        
        
        
        for(int docNumber = 0; docNumber < this.indexReader.numDocs(); docNumber++) {
        	
        	score = this.productoEscalar(palabrasConsulta, docNumber, this.indexReader.numDocs());
        	score = score /Double.valueOf( (hmap.get(docNumber)) * moduloVectorQuery);
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
			//1*dvector
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
		
		return Math.log( numDocs / super.index.getDocFreq(word));
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

}

