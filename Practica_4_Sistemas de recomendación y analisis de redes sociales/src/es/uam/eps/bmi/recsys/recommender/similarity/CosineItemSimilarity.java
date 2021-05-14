package es.uam.eps.bmi.recsys.recommender.similarity;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import es.uam.eps.bmi.recsys.data.Ratings;

public class CosineItemSimilarity  implements Similarity {

    private HashMap<Integer,Double>modulosItems;
	private HashMap<Integer, HashMap<Integer, Double>> similitud;
	private Ratings ratings;
	
    public CosineItemSimilarity(Ratings ratings) {
    
    	this.ratings = ratings;
    	
		
		this.similitud = new HashMap<>();
		this.modulosItems =new HashMap<>();
		
    }

    
    
    private double cosine(Integer item1,Integer item2,Set<Integer> item1Users,Set<Integer> item2Users ,Ratings ratings) {
  
    	
    	Double modulo1 = 0.0;
    	Double modulo2 = 0.0;
    	Double numerador=0.0;
    	Double rat1;
    	
    	for(Integer user : item1Users) {
    		rat1 = ratings.getRating(user, item1); 
    		modulo1 += rat1 * rat1;
    		if(item2Users.contains(user)) numerador+= rat1 * ratings.getRating(user, item2);		
    	}
    	
    	
    	
    	//modulo1
    	modulo1 = Math.sqrt(modulo1);
    	if(!this.modulosItems.containsKey(item1)) {
    		this.modulosItems.put(item1, modulo1);
    	} 
    	if(modulo1 == 0) return 0.0;
    	
    	
    	//modulo2 
    	if(!this.modulosItems.containsKey(item2)) {
    		
    		//lo calculamos
    		for(Integer user : item2Users) {
    			rat1 = ratings.getRating(user, item2); 
        		modulo2 += rat1 * rat1;
    		}
    		modulo2 =  Math.sqrt(modulo2);
    		this.modulosItems.put(item2, modulo2);
    	}else {
    		modulo2 = modulosItems.get(item2);
    	}
    	if(modulo2 == 0) return 0.0;

    	
    	return (double) ( numerador / (modulo1 * modulo2) );
    	
    	
    }
    


	@Override
	public double sim(int x, int y) {
		
		int item1,item2;
		if(x<y) {
			item1 =x;
			item2 =y;
		}else {
			item1 =y;
			item2 =x;
		}
		
		
		if( this.similitud.containsKey(item1) && this.similitud.get(item1).containsKey(item2) ) {
			return  this.similitud.get(item1).get(item2);
		}else {
			
			//Comprobacion de users vacios item1
			Set<Integer> item1Users =  ratings.getUsers(item1);
			if(item1Users.isEmpty()) {
				return 0.0;
			}
			//Comprobacion de users vacios item 2
			Set<Integer> item2Users = ratings.getUsers(item2);
			if(item2Users.isEmpty()) {
				return 0.0;
			}
			
			
			//calculamos el coseno
			Double cosine;
			Set<Integer> inCommonUsers;
			HashMap<Integer, Double> aux;
			
			
			cosine = cosine(item1,item2,item1Users,item2Users,ratings);
			
			if(similitud.containsKey(item1)) {
				aux = this.similitud.get(item1);
			}else {
				aux = new HashMap<>();
			}
			aux.put(item2, cosine);
			this.similitud.put(item1,aux);
			return cosine;
			
		}
	}
	
	
	@Override
	public String toString() {
        return "cosine";
    }

}
