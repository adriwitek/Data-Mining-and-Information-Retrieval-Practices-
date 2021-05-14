package es.uam.eps.bmi.recsys.recommender;

import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Set;

import es.uam.eps.bmi.recsys.data.Ratings;
import es.uam.eps.bmi.recsys.ranking.RankingElement;
import es.uam.eps.bmi.recsys.ranking.RankingElementImpl;
import es.uam.eps.bmi.recsys.recommender.similarity.Similarity;

public class UserKNNRecommender extends AbstractRecommender {

	HashMap<Integer, HashMap<Integer, Double>> ratingSum;
	
	public UserKNNRecommender(Ratings ratings, Similarity similarity, int k) {

		super(ratings);
		
		this.ratingSum = new HashMap<>();
		
		Set<Integer> allItems, aux;
		PriorityQueue<RankingElement> rankingHeap;
		Double score;
		double finalScore;
		
		HashMap<Integer, Double> userScore;
		
		allItems = ratings.getItems();


		for (Integer user : ratings.getUsers()) {

			userScore   = new HashMap<>();
			rankingHeap = new PriorityQueue<RankingElement>(Comparator.reverseOrder());
			
			// Calcula los k vecinos proximos.
			for (Integer v : ratings.getUsers()) {
				
				if (v == user) continue;
			
				score = similarity.sim(user, v);

				rankingHeap.add(new RankingElementImpl(v, score));
				
				if (rankingHeap.size() > k) rankingHeap.poll();
			}
			
			
			// Items no calificados por el usuario.
			aux       = new HashSet<>(allItems); 
			aux.removeAll(ratings.getItems(user));

			
			// Calcula los scores.
			for (Integer item : aux) {

				finalScore = 0;

				for (RankingElement sim : rankingHeap)  {
					
					score = ratings.getRating(sim.getID(), item);
					
					if (score == null) continue;
					
					finalScore += sim.getScore() * score;
				}
				
				userScore.put(item, finalScore);
			}
			
			this.ratingSum.put(user, userScore);
		}
	}

	@Override
	public double score(int user, int item) {
		return ratingSum.get(user).get(item);
	}
	
	@Override
	public String toString() {
        return "user-based kNN (cosine)";
    }

}
