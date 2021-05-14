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

public class NormUserKNNRecommender extends AbstractRecommender {

	HashMap<Integer, HashMap<Integer, Double>> ratingSum;
	
	public NormUserKNNRecommender(Ratings ratings, Similarity similarity, int k, int min) {

		super(ratings);
		
		this.ratingSum = new HashMap<>();
		
		Set<Integer> allItems, userItems, aux;
		PriorityQueue<RankingElement> rankingHeap;
		Double score;
		double C, finalScore;
		int count;
		
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
			userItems = ratings.getItems(user);
			aux       = new HashSet<>(allItems); 
			aux.removeAll(userItems);
			

			// Calcula los scores.
			for (Integer item : aux) {

				finalScore = 0;
				count = 0;
				C = 0;
				
				if (rankingHeap.size() <= min) {
					
					userScore.put(item, 0.0);

				} else {
				
					for (RankingElement sim : rankingHeap)  {
						
						score = ratings.getRating(sim.getID(), item);
						
						if (score == null) continue;
	
						C += sim.getScore();
						finalScore += sim.getScore() * score;
		
						count++;
					}

					if (C == 0)             userScore.put(item, 0.0);
					else if (count >= min)  userScore.put(item, finalScore / C);
					else                    userScore.put(item, 0.0);
				}
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
        return "normalized  user-based kNN (cosine)";
    }

}
