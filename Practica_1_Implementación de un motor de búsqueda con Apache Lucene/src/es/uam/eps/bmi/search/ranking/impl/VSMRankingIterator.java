package es.uam.eps.bmi.search.ranking.impl;

import es.uam.eps.bmi.search.index.Index;
import es.uam.eps.bmi.search.ranking.SearchRankingDoc;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

public class VSMRankingIterator implements Iterator<SearchRankingDoc> {

	private List<Entry<Integer, Double>> scores;

    Index index;
    int n = 0;

    public VSMRankingIterator (Index index, List<Entry<Integer, Double>> scores) {
    	
        this.index = index;
        this.scores = scores;
    }
    
    // Empty result list
    public VSMRankingIterator () {
        index = null;
        this.scores = new ArrayList<>();
    }
    
    public boolean hasNext() {
        return n < this.scores.size();
    }

    public SearchRankingDoc next() {

    	VSMRankingDoc aux = new VSMRankingDoc(index, this.scores.get(n));
    	
    	n++;
    	
    	return aux;
    }
}
