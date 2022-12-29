package es.uam.eps.bmi.search.index.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Scanner;

import es.uam.eps.bmi.search.index.AbstractIndex;
import es.uam.eps.bmi.search.index.Config;
import es.uam.eps.bmi.search.index.NoIndexException;
import es.uam.eps.bmi.search.index.structure.Posting;
import es.uam.eps.bmi.search.index.structure.PostingsList;
import es.uam.eps.bmi.search.index.structure.impl.RAMPostingsList;

public class DiskIndex extends AbstractIndex {
	
	HashMap<String, Long> index;
	ArrayList<String> paths;
	
	String indexFolder;
	
	public DiskIndex(String indexFolder) throws IOException { // NoIndexException
		
		this.index = new HashMap<>();
		this.paths = new ArrayList<>();
		
		this.indexFolder = indexFolder;
		
		try {
			// Cargamos el indice
			loadIndex(indexFolder);
	        
			// Cargamos el fichero de paths.
			loadPathFile(indexFolder);
			
			// Cargamos los modulos
			loadNorms(indexFolder);
        
		} catch (Exception ex) {
            throw new NoIndexException(indexFolder);
        }
	}

	@Override
	public int numDocs() {
		return this.paths.size();
	}

	@Override
	public PostingsList getPostings(String term) throws IOException {
		return loadPostingsList(term);
	}

	@Override
	public Collection<String> getAllTerms() throws IOException {
		return this.index.keySet();
	}

	@Override
	public long getTotalFreq(String term) throws IOException {
		
		long totalFreq = 0;
		
		for (Posting posting : this.getPostings(term)) {
			
			totalFreq += posting.getFreq();
		}
		
		return totalFreq;
	}

	@Override
	public long getDocFreq(String term) throws IOException {

		return loadPLSize(term);
	}

	@Override
	public String getDocPath(int docID) throws IOException {
		return paths.get(docID);
	}
	
	
	private long loadPLSize(String term) throws IOException {
		
		FileInputStream dict = new FileInputStream(this.indexFolder + "/" + Config.POSTINGS_FILE);

		Long skip = this.index.get(term);
		
		if (skip == null) {
			
			dict.close();
			return 0;
		}
		
		dict.skip( skip );
		
		byte[] int_b  = new byte[Integer.BYTES];
		
		dict.read(int_b);
		
		dict.close();
		
		return byte2int(int_b);
	}
	
	
	private PostingsList loadPostingsList(String term) throws IOException {
		
		FileInputStream dict = new FileInputStream(this.indexFolder + "/" + Config.POSTINGS_FILE);
		
		ArrayList<Posting> postings = new ArrayList<>();
		
		Long skip = this.index.get(term);
		
		if (skip == null) {
			
			dict.close();
			return null;
		}
		
		dict.skip( skip );
		
		byte[] int_b  = new byte[Integer.BYTES];
		byte[] long_b = new byte[Long.BYTES];
		
		dict.read(int_b);
		
		int size = byte2int(int_b);
		
		while (size-- > 0) {
			
			dict.read(int_b);
			dict.read(long_b); 
			
			postings.add( new Posting(byte2int(int_b), byte2long(long_b)) );
		}
		
		dict.close();
		
		return new RAMPostingsList(postings);
	}
	
	
	private void loadIndex(String indexFolder) throws IOException {
		
		// Cargamos el indice
		FileInputStream dict = new FileInputStream(indexFolder + "/" + Config.DICTIONARY_FILE);
		
		byte[] term;
		byte[] sizeTerm = new byte[Integer.BYTES];
		byte[] filePos = new byte[Long.BYTES];
		
		String termino;
		
		while (dict.read(sizeTerm) != -1) {
			
			term = new byte[ byte2int(sizeTerm) ];
			
			dict.read(term);
			
			termino = new String(term);
			
			dict.read(filePos);

			this.index.put(termino, byte2long(filePos));
		}
		
		dict.close();
	}
	
	private void loadPathFile(String indexFolder) throws NoIndexException, FileNotFoundException {
		
		// Cargamos el fichero de paths.
		File f = new File(indexFolder + "/" + Config.PATHS_FILE);
		
        if (!f.exists()) throw new NoIndexException(indexFolder);
        
        Scanner scn = new Scanner(f);
        
        int numDocs = new Integer(scn.nextLine());
        
        for (int docID = 0; docID < numDocs; docID++) {
        	
        	paths.add( scn.nextLine() );
		}
        
        scn.close();
	}
	
	
	private int byte2int(byte[] bytes) {
		
		return ByteBuffer.wrap(bytes).getInt();
	}
	
	private long byte2long(byte[] bytes) {
		
		return ByteBuffer.wrap(bytes).getLong();
	}

}
