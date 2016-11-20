package edu.nyu.cs.cs2580;

import edu.nyu.cs.cs2580.SearchEngine.Options;
import java.io.*;
import java.nio.file.Files;
import java.util.*;

/**
 * Created by naman on 11/10/2016.
 */
public class PseudoRelevance {
    private Options _options = null;

    //for each term in al documents bein considered, contains the sum of frequency of the term in all those documents
    private HashMap<String, Integer> _term = new HashMap<>();

    //For each docId as key contains a list of terms and the frequency of those terms as value
    //private HashMap<Integer, HashMap<String,Integer>> _postings = new HashMap<>();



    public PseudoRelevance(Options options) {
        _options = options;
    }

    public void queryRepresentation(Vector<ScoredDocument> Results, int numTerms) throws IOException {
        for (ScoredDocument scoredDoc : Results) {
            Document doc = scoredDoc._doc;
            loadDataToPostingListForDoc(doc);
            printProbability(numTerms);
        }
    }

    public void printProbability(int numTems){
        //Contains a term object which has term and the terms probability, could be sorted using the terms probability
        Vector <TermObject> termProbability = new Vector<TermObject>();
        int  totalTermFrequency = totalTermFrequency();
        for(String term : _term.keySet()) {
            double probability = (double)_term.get(term) / (double)(totalTermFrequency - _term.get(term));
            TermObject tobj = new TermObject(term, probability);
            termProbability.add(tobj);
        }
        Collections.sort(termProbability, Collections.reverseOrder());
        for (int i = 0; i < termProbability.size() && i < numTems; ++i) {
            System.out.println(termProbability.get(i)._term + " " + termProbability.get(i)._termProbability);
        }
    }

    private int totalTermFrequency(){
        int total = 0;
        for(String term : _term.keySet()){
            total += _term.get(term);
        }
        return total;
    }

    public void loadDataToPostingListForDoc(Document doc) throws IOException {

        File docFolder = new File(_options._indexPrefix + "/Documents");
        File[] docFiles = docFolder.listFiles();
        Indexer indexer = null;
        int documentDataFileNumber = (doc._docid / Integer.parseInt(indexer._options._DOCS_PER_DOCFILE));

        if (documentDataFileNumber < docFiles.length) {
            String fileName = _options._indexPrefix + "/Documents/doc-" + documentDataFileNumber + ".tsv";
                BufferedReader reader = new BufferedReader(new FileReader(fileName));
                byte[] bytes = Files.readAllBytes(new File(fileName).toPath());


                Vector<Byte> vb = new Vector<>();
                for (byte b : bytes) {
                    vb.add(b);
                }

                Vector<Integer> numbers = IndexCompressor.vByteDecoder(vb);


                int i = 0;

                while (i < numbers.size()) {
                    int docId = numbers.get(i);
                    int postingSize = numbers.get(i + 1);
                    if (docId == doc._docid) {
                        int j;
                        for (j = i + 2; j < i + 2 + postingSize; j++) {
                            String mapKey = indexer._reverseDictionary.get(numbers.get(j));
                            if(_term.containsKey(mapKey)){
                                _term.put(mapKey, _term.get(mapKey) + numbers.get(j+1));
                            } else {
                                _term.put(mapKey, numbers.get(j + 1));
                            }
                        }
                        i=j;
                    } else {
                        i = i + 2 + postingSize;
                    }
                }
            }
        }

    private ArrayList<String> getAllQueries() throws IOException {
        BufferedReader br =  new BufferedReader(new FileReader(_options._indexPrefix+"/queries.tsv"));
        String line;
        ArrayList<String> fileQueries = new ArrayList<String>();
        while((line = br.readLine()) != null){
            fileQueries.add(line);
        }
        br.close();
        return fileQueries;
    }

    class TermObject implements Comparable<TermObject>{
        public String _term;
        public double _termProbability;

        public TermObject(String term, double termProbability){
            _term = term;
            _termProbability = termProbability;
        }

        @Override
        public int compareTo(TermObject o) {
            if (this._termProbability == o._termProbability) {
                return 0;
            }
            return (this._termProbability > o._termProbability) ? 1 : -1;
        }
    }
}
