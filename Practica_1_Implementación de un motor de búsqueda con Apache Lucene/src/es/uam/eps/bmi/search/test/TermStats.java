package es.uam.eps.bmi.search.test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import es.uam.eps.bmi.search.index.Index;
import es.uam.eps.bmi.search.index.IndexBuilder;
import es.uam.eps.bmi.search.index.lucene.LuceneBuilder;
import es.uam.eps.bmi.search.index.lucene.LuceneIndex;

public class TermStats {

	public static void main (String a[]) throws IOException {

		String collectionPath = "collections/urls.txt";
		String indexPath = "index/urls";
		
		IndexBuilder builder = new LuceneBuilder();
        builder.build(collectionPath, indexPath);

        Index index = new LuceneIndex(indexPath);
        
        List<String> terms = new ArrayList<String>(index.getAllTerms());

        
		// a) las frecuencias totales en la colección de los términos, ordenadas de mayor a menor
        
        Collections.sort(terms, new Comparator<String>() {
            public int compare(String t1, String t2) {
                try {
                    return (int) Math.signum(index.getTotalFreq(t2)- index.getTotalFreq(t1));
                } catch (IOException ex) {
                    ex.printStackTrace();
                    return 0;
                }
            }
        });
        
        System.out.println("\n  Términos más frecuentes:");
        for (String term : terms)
            System.out.println("\t" + term + "\t" + index.getTotalFreq(term));




        System.out.println("\n\n\n\n");
        

        
        
		// b) el número de documentos que contiene cada término, igualmente de mayor a menor.

        Collections.sort(terms, new Comparator<String>() {
            public int compare(String t1, String t2) {
                try {
                    return (int) Math.signum(index.getDocFreq(t2)- index.getDocFreq(t1));
                } catch (IOException ex) {
                    ex.printStackTrace();
                    return 0;
                }
            }
        });

        System.out.println("\n  Documentos en los que aparece:");
        for (String term : terms)
            System.out.println("\t" + term + "\t" + index.getDocFreq(term));
		
    }
	
}
