package es.uam.eps.bmi.recsys.recommender.similarity;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import es.uam.eps.bmi.recsys.data.Ratings;

public class CosineUserSimilarity implements Similarity {
	
	private HashMap<Integer, HashMap<Integer, Double>> similitud;

	public CosineUserSimilarity(Ratings ratings) {
		
		this.similitud = new HashMap<>();
		
		Set<Integer> itemsUser, itemsV;
		HashMap<Integer, Double> aux1, aux2;
		double value;
		
		for (Integer user : ratings.getUsers()) {
			
			itemsUser = ratings.getItems(user);
			
			for (Integer v : ratings.getUsers()) {
				
				if (v == user) continue;
				
				aux1 = this.similitud.get(user);
				aux2 = this.similitud.get(v);
				
				if (aux1 == null) {
					
					aux1 = new HashMap<>();

				} else if (aux1.get(v) != null) continue;
				
				if (aux2 == null) {

					aux2 = new HashMap<>();

				}
				
				itemsV = new HashSet<>(ratings.getItems(v));
				itemsV.addAll(itemsUser);
				
				value = coseno(user, v, itemsV, ratings);
				
				aux1.put(v, value);
				aux2.put(user, value);

				this.similitud.put(user, aux1);
				this.similitud.put(v, aux2);
			}
		}
	}

	@Override
	public double sim(int x, int y) {
		return this.similitud.get(x).get(y);
	}
	


	
	private double coseno(int u, int v, Set<Integer> items, Ratings ratings) {
		
		double value = 0, x = 0, y = 0;
		
		Double scoreUser, scoreV;

		for (Integer item : items) {
			
			scoreUser = ratings.getRating(u, item);
			scoreV    = ratings.getRating(v, item);

			if ((scoreUser != null) && (scoreV != null)) value += scoreUser * scoreV;
		}
		
		
		for (Integer item : ratings.getItems(u)) {
			scoreUser = ratings.getRating(u, item);
			if (scoreUser != null) x += scoreUser * scoreUser;
		}
		
		for (Integer item : ratings.getItems(v)) {
			scoreV = ratings.getRating(v, item);
			if (scoreV != null) y += scoreV * scoreV;
		}
		

		if (x == 0 || y == 0) return 0;

		return value / (Math.sqrt(x) * Math.sqrt(y));
	}
	
	
	
	@Override
	public String toString() {
        return "cosine";
    }
}
