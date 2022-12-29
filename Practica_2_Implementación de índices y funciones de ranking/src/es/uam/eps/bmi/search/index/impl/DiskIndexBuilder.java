package es.uam.eps.bmi.search.index.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import es.uam.eps.bmi.search.index.AbstractIndexBuilder;
import es.uam.eps.bmi.search.index.Config;
import es.uam.eps.bmi.search.index.Index;
import es.uam.eps.bmi.search.index.structure.Posting;
import es.uam.eps.bmi.search.index.structure.impl.RAMPostingsList;

public class DiskIndexBuilder extends AbstractIndexBuilder {
	
	HashMap<String, RAMPostingsList> index;
	ArrayList<String> paths;
	
	String indexPath;

	@Override
	public void build(String collectionPath, String indexPath) throws IOException {
		
		this.indexPath = indexPath;
		
		clear(indexPath);
		
		this.index = new HashMap<>();
		this.paths = new ArrayList<>();
		
		// Buscar coleccion y acceder a ella.
        File f = new File(collectionPath);
        if (f.isDirectory()) indexFolder(f);                // A directory containing text files.
        else if (f.getName().endsWith(".zip")) indexZip(f); // A zip file containing compressed text files.
        else indexURLs(f);                                  // A file containing a list of URLs.
		
        savePaths(indexPath);
        serializarIndex(indexPath);
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
			
			termList = this.index.get(term);
			
			if (termList == null) {
				termList = new RAMPostingsList();
				this.index.put(term, termList);
			}
				
			termList.add( docID );
		}
	}

	@Override
	protected Index getCoreIndex() throws IOException {
		return new DiskIndex(this.indexPath);
	}
	
	
	private void serializarIndex(String indexPath) throws IOException {

		FileOutputStream dict = new FileOutputStream( indexPath + "/" + Config.DICTIONARY_FILE );
		FileOutputStream post = new FileOutputStream( indexPath + "/" + Config.POSTINGS_FILE );
        
        long sizeWrite = 0;
        
        for (String term : this.index.keySet()) {
        	
        	Iterator<Posting> termList = this.index.get(term).iterator();
        	
        	int size = this.index.get(term).size();
        	
        	post.write( int2byte(size));
        	
        	while (termList.hasNext()) {
        		
        		Posting p = termList.next();
        		
        		post.write( int2byte( p.getDocID()));
        		post.write( long2byte( p.getFreq()));
        	}
        	
        	byte[] termino = term.getBytes();

        	dict.write( int2byte(termino.length) );
        	dict.write( termino );
        	dict.write( long2byte(sizeWrite) );
        	
        	sizeWrite += size * (Integer.BYTES + Long.BYTES) + Integer.BYTES;
        }
        
        dict.close();
        post.close();
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
	
	
	
	private byte[] int2byte (int number) {
		
		return ByteBuffer.allocate(Integer.BYTES).putInt(number).array();
	}
	
	private byte[] long2byte (long number) {
		
		return ByteBuffer.allocate(Long.BYTES).putLong(number).array();
	}

}
