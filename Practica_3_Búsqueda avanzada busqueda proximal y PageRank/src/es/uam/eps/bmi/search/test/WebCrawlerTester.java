package es.uam.eps.bmi.search.test;

import java.io.IOException;

import es.uam.eps.bmi.search.SearchEngine;
import es.uam.eps.bmi.search.StaticEngine;
import es.uam.eps.bmi.search.graph.PageRank;
import es.uam.eps.bmi.search.index.Config;
import es.uam.eps.bmi.search.index.IndexBuilder;
import es.uam.eps.bmi.search.index.WebCrawler;
import es.uam.eps.bmi.search.index.lucene.LuceneBuilder;
import es.uam.eps.bmi.search.ranking.SearchRanking;
import es.uam.eps.bmi.search.ranking.SearchRankingDoc;
import es.uam.eps.bmi.search.ui.TextResultDocRenderer;
import es.uam.eps.bmi.search.util.Timer;

public class WebCrawlerTester {

	
    public static void main (String a[]) throws IOException {
    
    	
    	
    	IndexBuilder builder=  new LuceneBuilder();
    	String collectionPath = "collections/urls.txt";
    	String indexPath = "index/urls/lucene/regular";
    	
    
    	
    	Timer.reset();
    	WebCrawler wc = new WebCrawler(builder, 50, collectionPath, indexPath);
    	wc.start();
        Timer.time("  --> ");
    	System.out.println("Documentos indexados: " + wc.getNumberOfLinksIndexed());
    
    	System.out.println("-----------------------------------------");

    	//System.out.println(wc);//Imprime el grafo
    	//System.out.println(wc.getUrlsExplored());
    	
    	System.out.println("Semillas: " + wc.getNumeroSemillas());
    	System.out.println("Documentos indexados: " + wc.getNumberOfLinksIndexed());
    	System.out.println("Numero de webs en la frontera: " + wc.getUrlsEnFrontera());

    	
    	
    	String grafoPath = indexPath + Config.GRAPH_FILE ;
    	testSearch("toy 1", new StaticEngine(new PageRank(grafoPath, 0.5, 50)), "", 5);
    }

	
    
    
    
    
    static void testSearch (String collName, SearchEngine engine, String query, int cutoff) throws IOException {
        System.out.println("-----------------------");
        System.out.println("Checking search results on " + collName + " collection");
        SearchRanking ranking = engine.search(query, cutoff);
        System.out.println("  " + engine.getClass().getSimpleName() 
                + " + " + engine.getDocMap().getClass().getSimpleName()
                + ": top " + ranking.size() + " of " + ranking.nResults() + " for query '" + query + "'");
        for (SearchRankingDoc result : ranking)
            System.out.println("\t" + new TextResultDocRenderer(result));
    }
	
}
