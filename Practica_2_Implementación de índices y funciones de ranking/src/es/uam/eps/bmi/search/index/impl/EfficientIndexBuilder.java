package es.uam.eps.bmi.search.index.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.PriorityQueue;

import es.uam.eps.bmi.search.index.AbstractIndexBuilder;
import es.uam.eps.bmi.search.index.Config;
import es.uam.eps.bmi.search.index.Index;
import es.uam.eps.bmi.search.index.structure.Posting;
import es.uam.eps.bmi.search.index.structure.impl.PartPostingsFile;
import es.uam.eps.bmi.search.index.structure.impl.PostingHeapExtended;
import es.uam.eps.bmi.search.index.structure.impl.RAMPostingsList;
import es.uam.eps.bmi.search.ranking.SearchRankingDoc;

public class EfficientIndexBuilder extends AbstractIndexBuilder {
	
	HashMap<String, RAMPostingsList> index;//En RAM
	ArrayList<String> paths;
	
	ArrayList<PartPostingsFile> files; //Lista de las particiones del indice
	ArrayList<Posting> auxArray;
	
	private long maxRam;
	private long actualRam;
	
	FileOutputStream dict;
	FileOutputStream post;
	
	int posSize;
	
	String indexPath;
	
	public EfficientIndexBuilder(long maxPostings) throws IOException {
		
		this.maxRam = maxPostings;
	}

	
	@Override
	public void build(String collectionPath, String indexPath) throws IOException {
		
		this.indexPath = indexPath;
		
		clear(indexPath);
		
		this.index = new HashMap<>();
		this.paths = new ArrayList<>();
		this.files = new ArrayList<>();
		this.auxArray = new ArrayList<>();
		
		this.dict = new FileOutputStream( indexPath + "/" + Config.DICTIONARY_FILE );
		this.post = new FileOutputStream( indexPath + "/" + Config.POSTINGS_FILE );
		
		this.posSize = 0;
		
		this.actualRam = 0;
		
		// Buscar coleccion y acceder a ella.
        File f = new File(collectionPath);
        if (f.isDirectory()) indexFolder(f);                // A directory containing text files.
        else if (f.getName().endsWith(".zip")) indexZip(f); // A zip file containing compressed text files.
        else indexURLs(f);                                  // A file containing a list of URLs.
		
        savePaths(indexPath);
        serializarIndex();

        saveDocNorms(indexPath);
	}

	
	@Override
	protected void indexText(String text, String path) throws IOException {
		
		String[] terms = text.toLowerCase().split("\\P{Alpha}+");
		
		// Le damos un ID al documento y lo guardamos junto con su path.
		int docID = this.paths.size();
		this.paths.add(path);
		
		RAMPostingsList termList;
		
		for (String term : terms) {
			
			if (this.actualRam == this.maxRam) {
				
				this.files.add( new PartPostingsFile(this.indexPath, this.files.size(), this.index) );
				
				this.index = new HashMap<>();
				this.actualRam = 0;
			}
			
			termList = this.index.get(term);
			
			if (termList == null) {
				termList = new RAMPostingsList();
				this.index.put(term, termList);
			}
				
			termList.add( docID );
			this.actualRam++;
		}
	}

	
	@Override
	protected Index getCoreIndex() throws IOException {
		return new DiskIndex(this.indexPath);
	}
	
	private void serializarIndex() throws IOException  {
		

		PriorityQueue<PostingHeapExtended> minHeapPostings = new PriorityQueue<>(this.files.size(), new PostingHeapExtended());

		
		Posting p;
		PostingHeapExtended pHeap;
		
		
	
		
		for (PartPostingsFile file : this.files) {
			
			p = file.getNextPosting();
			
			minHeapPostings.add( new PostingHeapExtended(p.getDocID(), p.getFreq(), file.getTerm(), file.getID()) );
		}
		
		String actualTerm = minHeapPostings.peek().getTerminoOrigen();
		int actualDocID = minHeapPostings.peek().getDocID();
		
		long acumFreq = 0;

		
		
		while ( !minHeapPostings.isEmpty() ) {
			
			pHeap = minHeapPostings.poll();

			if (actualTerm.equals(pHeap.getTerminoOrigen()) == false) {

				
				addPosting( new Posting (actualDocID, acumFreq) );
				
				actualDocID = pHeap.getDocID();
				acumFreq = 0;
				
				// Guardas el termino actual.
				saveTerm(actualTerm);
				
				// Actualizas el termino.
				actualTerm = pHeap.getTerminoOrigen();


				
			} else if (actualDocID != pHeap.getDocID()) {

				addPosting( new Posting (actualDocID, acumFreq) );
				
				actualDocID = pHeap.getDocID();
				acumFreq = 0;
			}
			
			
			acumFreq += pHeap.getFreq();
			
			p = this.files.get( pHeap.getFileID() ).getNextPosting();
			
			if (p != null) {
				
				minHeapPostings.add( new PostingHeapExtended(p.getDocID(), p.getFreq(), this.files.get( pHeap.getFileID() ).getTerm(), pHeap.getFileID()) );
			}
				
		}
		
		addPosting( new Posting (actualDocID, acumFreq) );
		saveTerm(actualTerm);
	}
	
	
	
	
	
	
	private void saveTerm(String term) throws IOException {
		
    	byte[] termino = term.getBytes();

    	this.dict.write( int2byte(termino.length) );
    	this.dict.write( termino );
    	this.dict.write( long2byte(this.posSize) );
    		
		this.post.write( int2byte(this.auxArray.size()) );
		
		for (Posting p : this.auxArray) {
			
			this.post.write( int2byte( p.getDocID()));
			this.post.write( long2byte( p.getFreq()));
			
			this.posSize += Integer.BYTES + Long.BYTES;
		}
		
		this.posSize += Integer.BYTES;
    	
    	this.auxArray.clear();
	}
	
	
	private void savePaths(String indexPath) throws FileNotFoundException {
		
		int numDocs = paths.size();
		
        PrintStream out = new PrintStream(indexPath + "/" + Config.PATHS_FILE);
        
        out.println( numDocs );
        
        for (String doc : this.paths) {
            out.println( doc );
        }
        
        out.close();
	}
	
	
	private void addPosting(Posting p) throws IOException {
		
		this.auxArray.add(p);
	}
	
	private byte[] int2byte (int number) {
		
		return ByteBuffer.allocate(Integer.BYTES).putInt(number).array();
	}
	
	private byte[] long2byte (long number) {
		
		return ByteBuffer.allocate(Long.BYTES).putLong(number).array();
	}
}
