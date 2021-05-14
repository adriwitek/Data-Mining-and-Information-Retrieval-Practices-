package es.uam.eps.bmi.search.index.lucene;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;

import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.index.MultiTerms;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import es.uam.eps.bmi.search.index.Index;
import es.uam.eps.bmi.search.index.freq.FreqVector;
import es.uam.eps.bmi.search.index.freq.lucene.LuceneFreqVector;

public class LuceneIndex extends LuceneBuilder implements Index {
	
	private IndexReader index;
	
	
	public LuceneIndex (String indexPath) throws IOException {
		
		Directory directory = FSDirectory.open(Paths.get(indexPath));

        this.index = DirectoryReader.open(directory);
	}

	@Override
	public ArrayList<String> getAllTerms() throws IOException {
		
		ArrayList<String> lista = new ArrayList<String>();
		
		TermsEnum terms = MultiTerms.getTerms(this.index, "content").iterator();
		
		while (terms.next() != null) {
		
		   lista.add(terms.term().utf8ToString());
		}

		return lista;
	}


	@Override
	public long getTotalFreq(String termString) throws IOException {
		
		Term term = new Term("content", termString);

		return this.index.totalTermFreq(term);
	}


	@Override
	public FreqVector getDocVector(int docID) throws IOException {
		
		Terms terms = this.index.getTermVector(docID, "content");
		
		return new LuceneFreqVector(terms);
	}

	@Override
	public String getDocPath(int docID) throws IOException {
		
		return this.index.document(docID).get("path");
	}

	@Override
	public long getTermFreq(String word, int docID) throws IOException {

		return this.getDocVector(docID).getFreq(word);

	}

	@Override
	public long getDocFreq(String word) throws IOException {
		
		Term term = new Term("content", word);
		
		return index.docFreq(term);
	}
	
	
	public IndexReader getLuceneIndexReader() {
		return this.index;
	}
	
	
}
