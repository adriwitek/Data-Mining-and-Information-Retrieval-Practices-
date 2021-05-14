package es.uam.eps.bmi.recsys.data;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Scanner;
import java.util.Set;

public class FeaturesImpl<T> implements Features<T> {
	
	private HashMap<Integer, HashMap<T, Double>> features;

	public FeaturesImpl(String dataset, String separator, Parser<T> featureParser) throws FileNotFoundException {

		this.features = loadData(dataset, separator, featureParser);
	}

	@Override
	public Set<T> getFeatures(int id) {
		
		if (this.features.containsKey(id) == false) return null;
		
		return this.features.get(id).keySet();
	}

	@Override
	public Double getFeature(int id, T feature) {
		
	    if (features.containsKey(id) && features.get(id).containsKey(feature)) {
			return this.features.get(id).get(feature);

	    }else {
	    	return null;
	    }
          
	}

	@Override
	public void setFeature(int id, T feature, double value) {
		
		this.features.get(id).get(feature);
		
		HashMap<T, Double> Suser = this.features.get(id);
		
		if (Suser == null) Suser = new HashMap<>();
		
		Suser.put(feature, value);
		
		this.features.put(id, Suser);
	}

	@Override
	public Set<Integer> getIDs() {
		return this.features.keySet();
	}
	
	private HashMap<Integer, HashMap<T, Double>> loadData(String dataset, String separator, Parser<T> featureParser) throws FileNotFoundException {
		
		HashMap<Integer, HashMap<T, Double>> scores = new HashMap<>();
		HashMap<T, Double> Suser;
		
		Integer item;
		T user;
		double rate;
		
		File file = new File(dataset);
		Scanner reader = new Scanner(file);
		
		while (reader.hasNextLine()) {
			
			String[] line = reader.nextLine().split(separator);

			// Guarda los datos.
			item = Integer.parseInt(    line[0] );
			user = featureParser.parse( line[1] );
			rate = Double.parseDouble(  line[2] );
			
			Suser = scores.get(item);
			
			if (Suser == null) Suser = new HashMap<>();
			
			Suser.put(user, rate);
			
			scores.put(item, Suser);
		}

		reader.close();

		return scores;
	}

}
