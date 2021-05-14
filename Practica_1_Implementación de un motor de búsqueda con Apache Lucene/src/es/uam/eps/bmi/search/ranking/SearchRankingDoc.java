package es.uam.eps.bmi.search.ranking;

import java.io.IOException;

public abstract class SearchRankingDoc implements Comparable<SearchRankingDoc> {

	@Override
	public int compareTo(SearchRankingDoc arg0) {
		
		return (int) (this.getScore() - arg0.getScore());
	}
	
	public abstract double getScore();
	
	public abstract int getDocID();
	
	public abstract String getPath() throws IOException;
}
