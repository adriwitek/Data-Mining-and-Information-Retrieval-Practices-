package es.uam.eps.bmi.search.ranking.impl;

import es.uam.eps.bmi.search.index.Index;
import es.uam.eps.bmi.search.ranking.SearchRankingDoc;
import java.io.IOException;
import java.util.Map.Entry;

public class VSMRankingDoc extends SearchRankingDoc {
    Index index;
    Entry<Integer, Double> entry;
    
    VSMRankingDoc (Index idx, Entry<Integer, Double> entry) {
        index = idx;
        this.entry = entry;
    }

	public double getScore() {
        return this.entry.getValue();
    }

    public int getDocID() {
        return this.entry.getKey();
    }

    public String getPath() throws IOException {
        return index.getDocPath( this.entry.getKey() );
    }
}
