package es.uam.eps.bmi.search.index.structure.impl;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.TreeMap;

import es.uam.eps.bmi.search.index.structure.Posting;

public class PartPostingsFile {
	
	String indexPath;
	int idFile;
	
	String term;
	int pos;
	int postings;
	
	FileInputStream file;

	public PartPostingsFile(String indexPath, int idFile, HashMap<String, RAMPostingsList> idx) throws IOException {
		
		this.indexPath = indexPath;
		this.idFile = idFile;
		
		TreeMap<String, RAMPostingsList> index = new TreeMap<>(idx);
		
		FileOutputStream post = new FileOutputStream( indexPath + "/" + idFile );
        
        for (String term : index.keySet()) {
        	
        	Iterator<Posting> termList = index.get(term).iterator();
        	
        	int size = index.get(term).size();
        	byte[] termino = term.getBytes();

        	post.write( int2byte(termino.length) );
        	post.write( termino );
        	
        	post.write( int2byte(size));
        	
        	while (termList.hasNext()) {
        		
        		Posting p = termList.next();
        		post.write( int2byte( p.getDocID()));
        		post.write( long2byte( p.getFreq()));
        	}
        }
        
        post.close();
        
        
        this.term = "";
        this.pos = 0;
        this.postings = 0;
        
        this.file = new FileInputStream(this.indexPath + "/" + this.idFile);
	}
	
	
	public Posting getNextPosting() throws IOException {
		
		byte[] int_b = new byte[Integer.BYTES];
		byte[] long_b = new byte[Long.BYTES];
		
		// Creo que no hace falta.
		// this.file.skip( this.pos );
		
		// En caso de que el termino se haya recorrido por completo.
		if (this.postings == 0) {

			// Leemos el tamanio del termino.
			if (this.file.read(int_b) == -1) return null;
			
			byte[] term = new byte[ byte2int(int_b) ];
			
			// Leemos el termino.
			this.file.read(term);
			this.term = new String(term);
			
			//this.pos += byte2int(int_b) + Integer.BYTES + Integer.BYTES;
			
			//Leemos el numero de postings en la lista.
			this.file.read(int_b);
			this.postings = byte2int(int_b);
		}
		
		// Leemos el posting.
		this.file.read(int_b);//docID
		this.file.read(long_b);//freq
		
		//this.pos += Long.BYTES + Integer.BYTES;
		this.postings--;
		
		return new Posting(byte2int(int_b), byte2long(long_b));
	}
	
	
	public String getTerm() {
		return this.term;
	}
	
	public int getID() {
		return this.idFile;
	}
	
	
	
	private byte[] int2byte (int number) {
		
		return ByteBuffer.allocate(Integer.BYTES).putInt(number).array();
	}
	
	private byte[] long2byte (long number) {
		
		return ByteBuffer.allocate(Long.BYTES).putLong(number).array();
	}
	
	private int byte2int(byte[] bytes) {
		
		return ByteBuffer.wrap(bytes).getInt();
	}
	
	private long byte2long(byte[] bytes) {
		
		return ByteBuffer.wrap(bytes).getLong();
	}
	
}
