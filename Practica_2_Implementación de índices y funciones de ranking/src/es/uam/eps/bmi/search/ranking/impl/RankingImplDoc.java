package es.uam.eps.bmi.search.ranking.impl;

import es.uam.eps.bmi.search.index.Index;
import es.uam.eps.bmi.search.ranking.SearchRankingDoc;
import java.io.IOException;

public class RankingImplDoc extends SearchRankingDoc {
	
	Index index;
	int docID;
	double score;
	
	
	
    
	RankingImplDoc (Index idx,int docID, double score  ) {
        this.index = idx;
        this.docID = docID;
        this.score = score;
    }
	
	
    public double getScore() {
        return this.score;
    }

    public int getDocID() {
        return this.getDocID();
    }
	
	
	public String getPath() throws IOException {
	   return index.getDocPath( this.docID );
    }

}
