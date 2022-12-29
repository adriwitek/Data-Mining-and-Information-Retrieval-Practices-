package es.uam.eps.bmi.search.test;

import java.io.IOException;

import es.uam.eps.bmi.search.SearchEngine;
import es.uam.eps.bmi.search.index.Index;
import es.uam.eps.bmi.search.index.lucene.LuceneBuilder;
import es.uam.eps.bmi.search.index.lucene.LuceneForwardIndex;
import es.uam.eps.bmi.search.index.lucene.LuceneForwardIndexBuilder;
import es.uam.eps.bmi.search.index.lucene.LuceneIndex;
import es.uam.eps.bmi.search.ranking.SearchRanking;
import es.uam.eps.bmi.search.ranking.impl.EstimatorImpl;
import es.uam.eps.bmi.search.vsm.SlowVSMCosineEngine;

public class TestEstimatorImpl {

    public static void main (String a[]) throws IOException {
    
    	String query1 = "obama family tree";
    	String query2 = "air travel information";
    	String query3 = "living in india";
    	
    	
    	Integer cutoff = 5;
    	
   
        EstimatorImpl estimatorImpl = new EstimatorImpl();
        
       
    
        System.out.println("------INICIO------");
        
        
        
        System.out.println("--Prueba 1: 1K------------");
        //Build
        new LuceneForwardIndexBuilder().build("collections/docs1k.zip", "index/1k" + "/lucene/forward");
        //Load
        Index i1 = new LuceneForwardIndex("index/1k" + "/lucene/forward");
        SearchEngine e1 = new SlowVSMCosineEngine(i1);
        System.out.println("	>>>>Query: " + query1);
        System.out.println("Resultados REALES Prueba1: " +    estimatorImpl.getRealNResults( e1,  query1)     );
        System.out.println("Resultados ESTIMADOS Prueba1: " +   estimatorImpl.estimateNResults(i1,  query1));

        
        
        System.out.println("--Prueba 2: 10K------------");
        //Build
        new LuceneForwardIndexBuilder().build("collections/docs10k.zip", "index/10k" + "/lucene/forward");
        //Load
        Index i2 = new LuceneForwardIndex("index/10k" + "/lucene/forward");
        SearchEngine e2 = new SlowVSMCosineEngine(i2);
        System.out.println("	>>>>Query: " + query2);
        System.out.println("Resultados REALES Prueba2: " +    estimatorImpl.getRealNResults( e2,  query2)     );
        System.out.println("Resultados ESTIMADOS Prueba2: " +   estimatorImpl.estimateNResults(i2,  query2));
        
        
        
        
        System.out.println("--Prueba 3: 100K------------");
        //Build
        new LuceneForwardIndexBuilder().build("collections/docs100k.zip", "index/100k" + "/lucene/forward");
        //Load
        Index i3 = new LuceneForwardIndex("index/100k" + "/lucene/forward");
    	SearchEngine e3 = new SlowVSMCosineEngine(i3);
        System.out.println("	>>>>Query: " + query3);
        System.out.println("Resultados REALES Prueba3: " +    estimatorImpl.getRealNResults( e3,  query3)     );
        System.out.println("Resultados ESTIMADOS Prueba3: " +   estimatorImpl.estimateNResults(i3,  query3));
    
    }
	
	
	
	
	
	
	
	
}
