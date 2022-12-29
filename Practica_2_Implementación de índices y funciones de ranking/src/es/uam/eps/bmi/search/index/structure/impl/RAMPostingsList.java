package es.uam.eps.bmi.search.index.structure.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;

import es.uam.eps.bmi.search.index.structure.Posting;
import es.uam.eps.bmi.search.index.structure.PostingsList;

public class RAMPostingsList implements PostingsList, Serializable {
	
	private static final long serialVersionUID = 1L;
	ArrayList<Posting> postings;
	
	public RAMPostingsList(ArrayList<Posting> postings) {
		
		this.postings = postings;
	}
	
	public RAMPostingsList() {
		
		this.postings = new ArrayList<>();
	}
	
	@Override
	public Iterator<Posting> iterator() {
		return this.postings.iterator();
	}

	@Override
	public int size() {
		return this.postings.size();
	}
	
	/*
	 * Suponemos que solo se introducen DocIDs mayores o iguales al ultimo.
	 */
	public void add(int docID) {
		
		int tam = this.postings.size();
		
		if (tam == 0) {
			
			this.postings.add( new Posting(docID, 1));
			
		} else {
		
			Posting p = this.postings.get( tam - 1 );
			
			if (p.getDocID() == docID)  p.add1();
			else                        this.postings.add( new Posting(docID, 1) );
		}
	}
}
