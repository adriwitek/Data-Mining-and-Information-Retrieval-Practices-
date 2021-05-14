package es.uam.eps.bmi.recsys;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;

import es.uam.eps.bmi.recsys.ranking.Ranking;
import es.uam.eps.bmi.recsys.ranking.RankingElement;

public class RecommendationImpl implements Recommendation {

	private HashMap<Integer, Ranking> recommend;
	
	public RecommendationImpl() {
		this.recommend = new HashMap<>();
	}
	
	@Override
	public Set<Integer> getUsers() {
		return this.recommend.keySet();
	}

	@Override
	public Ranking getRecommendation(int user) {
		return this.recommend.get(user);
	}

	@Override
	public void add(int user, Ranking ranking) {
		this.recommend.put(user, ranking);
	}

	@Override
	public void print(PrintStream out) {
		
		for ( Entry<Integer, Ranking> user : this.recommend.entrySet()) {
			
			Iterator<RankingElement> iter = user.getValue().iterator();
			
			while (iter.hasNext()) {
				
				RankingElement elem = iter.next();
				
				out.println(user.getKey() + "\t" + elem.getID() + "\t" + elem.getScore());
			}
		}
	}

	@Override
	public void print(PrintStream out, int userCutoff, int itemCutoff) {
		
		int item;
		
		for ( Entry<Integer, Ranking> user : this.recommend.entrySet()) {
			
			if (userCutoff <= 0) break;
			
			Iterator<RankingElement> iter = user.getValue().iterator();
			
			item = itemCutoff;
			
			while (iter.hasNext() && (item > 0)) {
				
				RankingElement elem = iter.next();
				
				out.println(user.getKey() + "\t" + elem.getID() + "\t" + elem.getScore());
				
				item--;
			}
			
			userCutoff--;
		}
	}

}
