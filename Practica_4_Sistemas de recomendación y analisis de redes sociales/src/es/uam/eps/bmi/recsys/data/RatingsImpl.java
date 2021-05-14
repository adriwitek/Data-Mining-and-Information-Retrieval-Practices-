package es.uam.eps.bmi.recsys.data;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.Set;

public class RatingsImpl implements Ratings {

	//     ( usuario : (pelicula : rating)
	private HashMap<Integer, HashMap<Integer, Double>> scores;
    private HashMap<Integer ,HashSet<Integer>> items;//para mantener el orden 1 de complejidad en todos los metodos

	
	public RatingsImpl(String dataset, String separator) throws FileNotFoundException {
		
		this.items = new HashMap<>();
		this.scores = loadData(dataset, separator);
	}
	
	public RatingsImpl(HashMap<Integer, HashMap<Integer, Double>> scores,    HashMap<Integer ,HashSet<Integer>> items) {
		this.items = items;
		this.scores = scores;
	}

	@Override
	public void rate(int user, int item, Double rating) {
		
		this.scores.get(user).put(item, rating);
	}

	@Override
	public Double getRating(int user, int item) {
		
        if (scores.containsKey(user)) return this.scores.get(user).get(item);

        return null;
	}

	@Override
	public Set<Integer> getUsers(int item) {
		
		if(this.items.get(item) != null) {
			return items.get(item);

		}else {
			return new HashSet<Integer>();

		}

		
	}

	@Override
	public Set<Integer> getItems(int user) {
		
		  if(scores.containsKey(user)) return   this.scores.get(user).keySet();
		  
		  return new HashSet<>();
	}

	@Override
	public Set<Integer> getUsers() {
		return this.scores.keySet();
		
	}

	@Override
	public Set<Integer> getItems() {
		
		return this.items.keySet();
	}

	@Override
	public int nRatings() {

		int cont = 0;
		
		for (HashMap<Integer, Double> user : this.scores.values()) {
			
			cont += user.size();
		}
		
		return cont;
	}

	@Override
	public Ratings[] randomSplit(double ratio) {

		HashMap<Integer, HashMap<Integer, Double>> train = new HashMap<>();
		HashMap<Integer, HashMap<Integer, Double>> test  = new HashMap<>();
		
	    HashMap<Integer ,HashSet<Integer>> itemsTrain = new HashMap<>();
	    HashMap<Integer ,HashSet<Integer>> itemsTest = new HashMap<>();

		
		HashMap<Integer, Double> Suser;
		int t = 0, r = 0;
		
		for ( Entry<Integer, HashMap<Integer, Double>> user : this.scores.entrySet()) {
			
			for ( Entry<Integer, Double> item : user.getValue().entrySet()) {
				
				// TEST
				if (Math.random() > ratio) {
					
					Suser = test.get(user.getKey());
					
					if (Suser == null) Suser = new HashMap<>();
					
					Suser.put(item.getKey(), item.getValue());
					
					test.put(user.getKey(), Suser);
					t++;
					
					//itemsEstructure
					if(!itemsTest.containsKey(item.getKey()) ) {
						itemsTest.put(item.getKey(), new HashSet<>());
					}
					itemsTest.get(item.getKey()).add(user.getKey());
					
					

				// TRAIN
				} else {
					
					Suser = train.get(user.getKey());
					
					if (Suser == null) Suser = new HashMap<>();
					
					Suser.put(item.getKey(), item.getValue());
					
					train.put(user.getKey(), Suser);
					r++;
					
					//itemsEstructure
					if(!itemsTrain.containsKey(item.getKey()) ) {
						itemsTrain.put(item.getKey(), new HashSet<>());
					}
					itemsTrain.get(item.getKey()).add(user.getKey());
					
				}
			}
		}
		
		//System.out.println("train: " + r + " ,   test: " + t);
		
		Ratings[] rat = {new RatingsImpl(train,itemsTrain), new RatingsImpl(test, itemsTest)};
		
		return rat;
	}
	
	
	
	
	private HashMap<Integer, HashMap<Integer, Double>> loadData(String dataset, String separator) throws FileNotFoundException {
		
		HashMap<Integer, HashMap<Integer, Double>> scores = new HashMap<>();
		HashMap<Integer, Double> Suser;
		
		int user, film;
		double rate;
		
		File file = new File(dataset);
		Scanner reader = new Scanner(file);
		
		while (reader.hasNextLine()) {
			
			String[] line = reader.nextLine().split(separator);

			// Guarda los datos.
			user = Integer.parseInt(   line[0] );
			film = Integer.parseInt(   line[1] );
			rate = Double.parseDouble( line[2] );
			
			Suser = scores.get(user);
			if (Suser == null) Suser = new HashMap<>();
			
			Suser.put(film, rate);
			
			scores.put(user, Suser);
			
			if(!this.items.containsKey(film) ) {
				this.items.put(film, new HashSet<>());
			}
			this.items.get(film).add(user);
			
		}

		reader.close();

		return scores;
	}
	
	
	 public HashMap<Integer ,HashSet<Integer>> getitemsEstructure(){
		 return this.items;
	 }

}
