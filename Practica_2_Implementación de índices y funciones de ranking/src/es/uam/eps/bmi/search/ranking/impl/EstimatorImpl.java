package es.uam.eps.bmi.search.ranking.impl;

import java.io.IOException;

import es.uam.eps.bmi.search.Estimator;
import es.uam.eps.bmi.search.SearchEngine;
import es.uam.eps.bmi.search.index.AbstractIndex;
import es.uam.eps.bmi.search.index.Index;
import es.uam.eps.bmi.search.ranking.SearchRanking;
import es.uam.eps.bmi.search.vsm.SlowVSMCosineEngine;

public class EstimatorImpl implements Estimator {
	
    public long estimateNResults(Index index, String query) {
    	//Supones estimacion como freq total del termino en la collecion,entre el tam de la colecion
    	//Con indep de cada termino de la query y AND
    	String[] qParseada = query.toLowerCase().split("\\P{Alpha}+");
    	long estimacion =0;
    	for(String t: qParseada) {
    		try {
				estimacion += index.getTotalFreq(t)/index.numDocs();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
    	
    	
    	return ((long) estimacion);
    }

    
    
    public long getRealNResults(SearchEngine engine, String query) {
    	
    	try {
    		//SlowVSMCosineEngine engine = new SlowVSMCosineEngine(index);
    		SearchRanking ranking = engine.search(query,1000000000);
    		return ((long)  ranking.nResults());
    	}catch(IOException e){
			e.printStackTrace();

    	}
    	return 0;
    }

    
    
}
