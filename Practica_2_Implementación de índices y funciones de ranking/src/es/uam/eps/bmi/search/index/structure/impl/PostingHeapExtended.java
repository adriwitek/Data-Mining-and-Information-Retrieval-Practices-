package es.uam.eps.bmi.search.index.structure.impl;

public class PostingHeapExtended extends PostingMinHeap {

	private static final long serialVersionUID = 1L;

	int fileID;
	
	public PostingHeapExtended() {
		super(0, 0, "");
	}

	public PostingHeapExtended(int id, long f, String termino, int fileID) {
		
		super(id, f, termino);
		
		this.fileID = fileID;
	}

	public int getFileID() {
		return this.fileID;
	}
	


	@Override
	public int compare(PostingMinHeap o1, PostingMinHeap o2) {
		
		int compare = o1.getTerminoOrigen().compareTo(o2.getTerminoOrigen());
		
		if (compare == 0)  compare = o1.getDocID() - o2.getDocID();
		
		return compare;
	}
	
	
}
