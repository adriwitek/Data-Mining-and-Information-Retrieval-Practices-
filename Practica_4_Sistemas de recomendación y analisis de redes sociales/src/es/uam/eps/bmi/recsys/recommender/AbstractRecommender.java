package es.uam.eps.bmi.recsys.recommender;

import java.util.HashSet;
import java.util.Set;

import es.uam.eps.bmi.recsys.Recommendation;
import es.uam.eps.bmi.recsys.RecommendationImpl;
import es.uam.eps.bmi.recsys.data.Ratings;
import es.uam.eps.bmi.recsys.ranking.Ranking;
import es.uam.eps.bmi.recsys.ranking.RankingImpl;

public abstract class AbstractRecommender implements Recommender {
	
	public Ratings ratings;
 
	public AbstractRecommender(Ratings ratings) {
		
		this.ratings = ratings;
	}
	
	public Recommendation recommend(int cutoff) {
		
		Recommendation recommend = new RecommendationImpl();
		Set<Integer> allItems = ratings.getItems();
		
		Ranking rank;
		Set<Integer> userItems, aux;
		
		double score;

		for (Integer user : ratings.getUsers()) {
		
			rank = new RankingImpl(cutoff);
			
			// Interseccion de los Set
			userItems = ratings.getItems(user);
			aux       = new HashSet<>(allItems); 
			aux.removeAll(userItems);
			
			for (Integer item : aux) {

				score =  score(user, item);
				
				if (score != 0.0) rank.add(item, score);
			}
			
			recommend.add(user, rank);
		}
		
		
		return recommend;
	}
	
	public abstract double score(int user, int item);
	
	public abstract String toString();

}
