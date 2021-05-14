package es.uam.eps.bmi.sna.metric.user;

import java.util.HashSet;
import java.util.Set;

import es.uam.eps.bmi.sna.metric.LocalMetric;
import es.uam.eps.bmi.sna.ranking.Ranking;
import es.uam.eps.bmi.sna.ranking.RankingImpl;
import es.uam.eps.bmi.sna.structure.UndirectedSocialNetwork;

public class UserClusteringCoefficient<U extends Comparable<U>> implements LocalMetric<U, U> {

	private int topK;
	
	public UserClusteringCoefficient(int topK) {
		
		this.topK = topK;
	}

	public UserClusteringCoefficient() {
		
		this.topK = -1;
	}

	@Override
	public Ranking<U> compute(UndirectedSocialNetwork<U> network) {
		
		Ranking<U> rank;
		
		if (this.topK == -1) rank = new RankingImpl<>();
		else                 rank = new RankingImpl<>(this.topK);
		
		Set<U> users = network.getUsers();
		Set<U> contacts;
		
		Set<U> auxCont;
		int cont = 0, size;
		double posible;

		for (U u : users) {
			
			contacts = network.getContacts(u);
			size = contacts.size();
			
			auxCont = new HashSet<>(contacts);
			
			cont = 0;
			
			for (U v : contacts) {
				
				auxCont = new HashSet<>(network.getContacts(v));
				
				auxCont.retainAll(contacts);

				cont += auxCont.size();
			}
			
			// Se divide entre 2 ya que se cuenta el doble.
			/*
			cont = cont / 2;
			
			posible = (size * (size - 1)) / 2.0;
			*/
			
			// Para ahorar operaciones en vez dividir cont, no dividimos en posible.
			
			posible = (size * (size - 1));
			
			if (posible == 0)  rank.add(u, 0);
			else               rank.add(u, cont / posible);
		}

		return rank;
	}

	@Override
	public double compute(UndirectedSocialNetwork<U> network, U u) {
		
		Set<U> contacts;
		Set<U> auxCont;
		
		int cont = 0, size;
		double posible;

		contacts = network.getContacts(u);
		size = contacts.size();
		
		auxCont = new HashSet<>(contacts);
		
		cont = 0;
		
		for (U v : contacts) {
			
			auxCont = new HashSet<>(network.getContacts(v));
			
			auxCont.retainAll(contacts);

			cont += auxCont.size();
		}
		
		// Se divide entre 2 ya que se cuenta el doble.
		/*
		cont = cont / 2;
		
		posible = (size * (size - 1)) / 2.0;
		*/
		
		// Para ahorar operaciones en vez dividir cont, no dividimos en posible.
		
		posible = (size * (size - 1));
		
		return cont / posible;
	}
	
	
	@Override
	public String toString() {
		return "UserClusteringCoefficient";
	}
}
