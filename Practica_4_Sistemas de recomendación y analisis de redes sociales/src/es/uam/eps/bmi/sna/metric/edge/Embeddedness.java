package es.uam.eps.bmi.sna.metric.edge;

import java.util.HashSet;
import java.util.Set;

import es.uam.eps.bmi.sna.metric.LocalMetric;
import es.uam.eps.bmi.sna.ranking.Ranking;
import es.uam.eps.bmi.sna.ranking.RankingImpl;
import es.uam.eps.bmi.sna.structure.Edge;
import es.uam.eps.bmi.sna.structure.UndirectedSocialNetwork;

public class Embeddedness<U extends Comparable<U>> implements LocalMetric<Edge<U>, U> {  // Arraigo

	private int topK;
	
	public Embeddedness(int topK) {

		this.topK = topK;
	}

	@Override
	public Ranking<Edge<U>> compute(UndirectedSocialNetwork<U> network) {
		
		Ranking<Edge<U>> rank = new RankingImpl<>(this.topK);
		int connect = 0, all = 0;
		
		Set<U> users = network.getUsers();
		Set<U> rest  = new HashSet<>(users);
		
		for (U u : users) {
			
			rest.remove(u);
			
			for (U v : rest) {

				for (U c : network.getContacts(u)) {
				
					if (c.equals(v)) continue;
					else if (network.getContacts(v).contains(c)) connect++;

					all++;
				}
				
				for (U c : network.getContacts(v)) {
					
					if (c.equals(u)) continue;
					else if (network.getContacts(u).contains(c)) continue;

					all++;
				}

				if (all == 0) rank.add(new Edge<U>(u, v), 0);
				else          rank.add(new Edge<U>(u, v), ((double) connect) / ((double) all));
	
				connect = 0;
				all     = 0;
			}
		}
		
		return rank;
	}

	@Override
	public double compute(UndirectedSocialNetwork<U> network, Edge<U> element) {
		
		int connect = 0, all = 0;
		
		U u, v;
		
		u = element.getFirst();
		v = element.getSecond();

		for (U c : network.getContacts(u)) {
			
			if (c.equals(v)) continue;
			else if (network.getContacts(v).contains(c)) connect++;

			all++;
		}
		
		for (U c : network.getContacts(v)) {
			
			if (c.equals(u)) continue;
			else if (network.getContacts(u).contains(c)) continue;

			all++;
		}
		
		if (all == 0) return 0;
		
		return ((double) connect) / ((double) all);
	}
	
	@Override
	public String toString() {
		return "Embeddedness";
	}
}
