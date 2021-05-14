package es.uam.eps.bmi.recsys.recommender;

import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import es.uam.eps.bmi.recsys.data.Ratings;
import es.uam.eps.bmi.recsys.ranking.RankingElement;
import es.uam.eps.bmi.recsys.ranking.RankingElementImpl;
import es.uam.eps.bmi.recsys.recommender.similarity.Similarity;

public class UserKNNMeanCenteredRecommender extends AbstractRecommender {

	HashMap<Integer, HashMap<Integer, Double>> ratingSum;
	
	public UserKNNMeanCenteredRecommender(Ratings ratings, Similarity similarity, int k) {

		super(ratings);
		
		this.ratingSum = new HashMap<>();
		
		Set<Integer> allItems, userItems, aux;
		//Map<Integer, Double> rankingHeap, sortedByCount;
		PriorityQueue<RankingElement> rankingHeap;

		Double score;
		double finalScore;
		int count;
		
		HashMap<Integer, Double> medias = this.mediasUsuarios(ratings);

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
				//rankingHeap.put(v, score);
			}
			
			
			// Items no calificados por el usuario.
			userItems = ratings.getItems(user);
			aux       = new HashSet<>(allItems); 
			aux.removeAll(userItems);
			
			
			
			// Calcula los scores.
			for (Integer item : aux) {

				finalScore = 0;
				count = 0;
				
				for (RankingElement sim : rankingHeap)  {
				
					if (count >= k) break;
					
					score = ratings.getRating(sim.getID(), item);//restamos al rating del vecino su media
					
					if (score == null) continue;
					
					finalScore += sim.getScore()  * (score  - medias.get(sim.getID()));
					
					count++;
				}
				
				finalScore += medias.get(user);//Sumamos la media del usuario
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
        return "user-based kNN(MEAN CENTERED) (cosine)";
    }

	
	
	private HashMap<Integer, Double> mediasUsuarios(Ratings ratings){
		
		HashMap<Integer, Double> medias = new HashMap();
		double media = 0.0;
		
		for (Integer u : ratings.getUsers()) {
			for(Integer item :ratings.getItems(u)) {
				media += ratings.getRating(u, item);
			}
			if(ratings.getItems(u).size()==0) {
				medias.put(u, 0.0);

			}else {
				medias.put(u, media/ratings.getItems(u).size());

			}
			media = 0.0;
		}
		return medias;
	}
}
