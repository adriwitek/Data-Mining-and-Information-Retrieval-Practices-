package es.uam.eps.bmi.search.ui;

import java.io.IOException;

import es.uam.eps.bmi.search.ranking.SearchRankingDoc;

public class TextResultDocRenderer {

	private SearchRankingDoc result;
	
	public TextResultDocRenderer(SearchRankingDoc result) {
		
		this.result = result;
	}
	
	
	@Override
	public String toString() {

		try {
			return this.result.getScore() + "\t file:" + this.result.getPath();
			
		} catch (IOException e) {
			
			return this.result.getScore() + "\t ERROR\n";
		}
	}

}
