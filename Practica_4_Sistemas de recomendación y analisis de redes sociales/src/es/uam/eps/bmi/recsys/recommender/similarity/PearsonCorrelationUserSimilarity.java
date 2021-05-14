package es.uam.eps.bmi.recsys.recommender.similarity;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import es.uam.eps.bmi.recsys.data.Ratings;

public class PearsonCorrelationUserSimilarity implements Similarity {

	
	private HashMap<Integer, HashMap<Integer, Double>> similitud;

	
	public PearsonCorrelationUserSimilarity(Ratings ratings) {
		
		this.similitud = new HashMap<>();
		HashMap<Integer, Double> medias = this.mediasUsuarios(ratings);
		HashMap<Integer, Double> aux1, aux2;
		double value;
		
		for (Integer user : ratings.getUsers()) {
			
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
				
				value = correlacionPearson(user, v, ratings,medias);

				aux1.put(v, value);
				aux2.put(user, value);
				
				this.similitud.put(user, aux1);
				this.similitud.put(v, aux2);
			}	
		}
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
	
	
	private double correlacionPearson(int u, int v, Ratings ratings,HashMap<Integer, Double> medias) {
		
		double value = 0, x = 0, y = 0;
		HashSet<Integer> itemsInCommon = new HashSet();
		Double scoreU, scoreV;

		
		itemsInCommon.addAll(ratings.getItems(u));
		itemsInCommon.addAll(ratings.getItems(v));

		
		for (Integer item : itemsInCommon) {
			
			scoreU = ratings.getRating(u, item);
			scoreV = ratings.getRating(v, item);

			if ((scoreU != null) && (scoreV != null)) value += (scoreU - medias.get(u)) * (scoreV  - medias.get(v));
			
			if (scoreU != null) x +=  (scoreU - medias.get(u)) *  (scoreU - medias.get(u));
			if (scoreV != null)    y += (scoreV  - medias.get(v)) * (scoreV  - medias.get(v));
		}

		if (x == 0 || y == 0) return 0;

		return value / (Math.sqrt(x) * Math.sqrt(y));
		//return value / Math.sqrt(x * y);
	}
	
	
	
	@Override
	public double sim(int x, int y) {
		return this.similitud.get(x).get(y);
	}
}
