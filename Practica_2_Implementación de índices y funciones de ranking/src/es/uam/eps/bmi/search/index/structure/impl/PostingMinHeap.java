package es.uam.eps.bmi.search.index.structure.impl;

import java.util.Comparator;

import es.uam.eps.bmi.search.index.structure.Posting;


public class PostingMinHeap extends Posting implements Comparator<PostingMinHeap> {

	private static final long serialVersionUID = 1L;
	
	private String terminoOrigen;
	
	public PostingMinHeap () {
		super(0, 0);
	}
	
	public PostingMinHeap (int id, long f, String termino) {
        super(id,f);
        this.terminoOrigen = termino;
    }
	
	public String getTerminoOrigen() {
		return this.terminoOrigen;
	}

	@Override
	public int compare(PostingMinHeap o1, PostingMinHeap o2) {
		
		int compare = o1.getDocID() - o2.getDocID();
		
		return compare;
	}

}
	
	

