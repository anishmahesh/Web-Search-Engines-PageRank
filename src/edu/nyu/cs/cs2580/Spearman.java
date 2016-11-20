package edu.nyu.cs.cs2580;

import java.io.*;
import java.util.Collections;
import java.util.HashMap;
import java.util.Vector;
import edu.nyu.cs.cs2580.SearchEngine.Options;

/**
 * Created by naman on 11/9/2016.
 */
public class Spearman {
    private static Options _options = null;
    private HashMap <Integer, Integer> _pageRank = new HashMap<>();
    private HashMap <Integer, Integer> _numViews = new HashMap<>();
    private Vector <DocData> _pageRankData = new Vector<DocData>();
    private Vector <DocData> _numViewsData = new Vector<DocData>();


    public Spearman(Options options) {
        _options = options;
    }

    public static void main (String[] args) throws IOException, ClassNotFoundException {
        Spearman sp = new Spearman(new Options("conf/engine.conf"));
        sp.loadData(args[0], args[1]);
        System.out.println(sp.computeSpearman());
    }

    public double computeSpearman(){
        double x_bar = calculateXBar();
        double y_bar = calculateXBar();
        double numerator = 0.0;
        double denominatorSummationPageRank = 0.0;
        double denominatorSummationNumViews = 0.0;
        for (int i=1; i<=_pageRank.size(); i++){
            numerator += (_pageRank.get(i) - x_bar)*(_numViews.get(i) - y_bar);
            denominatorSummationPageRank += Math.pow(_pageRank.get(i) - x_bar, 2);
            denominatorSummationNumViews += Math.pow(_numViews.get(i) - y_bar, 2);
        }
        double denominator = Math.sqrt(denominatorSummationNumViews + denominatorSummationPageRank);
        if (denominator == 0.0) {
            return 0.0;
        }
        return numerator/denominator;
    }

    public double calculateXBar(){
        int total = 0;
        for (Integer key : _pageRank.keySet()){
            total += _pageRank.get(key);
        }
        return ((double) total)/((double)_pageRank.size());
    }

    public double calculateYBar(){
        int total = 0;
        for (Integer key : _numViews.keySet()){
            total += _numViews.get(key);
        }
        return ((double) total)/((double)-_numViews.size());
    }

    public void loadData(String pageRank, String numviews) throws IOException, ClassNotFoundException {

        String pageRankPath = _options._indexPrefix + pageRank;
        ObjectInputStream pageRankReader = new ObjectInputStream(new FileInputStream(pageRankPath));
        Spearman loadedPageRank = (Spearman)pageRankReader.readObject();
        this._pageRank = loadedPageRank._pageRank;
        pageRankReader.close();

        String numViewsPath = _options._indexPrefix + numviews;
        ObjectInputStream numViewsReader = new ObjectInputStream(new FileInputStream(pageRankPath));
        Spearman loadedNumViews = (Spearman)numViewsReader.readObject();
        this._numViews = loadedNumViews._numViews;
        numViewsReader.close();
    }

    public HashMap <Integer, Integer> rank(Vector<DocData> docForRanking){
        HashMap <Integer, Integer> rankedDoc = new HashMap<>();
        Collections.sort(docForRanking, Collections.reverseOrder());
        int i=0;
        double prevDoc_data = -1.0;
        for(DocData doc: docForRanking){
            if(doc._data != prevDoc_data){
                i++;
                prevDoc_data = doc._data;
            }
            rankedDoc.put(doc._docId, i);
        }
        return rankedDoc;
    }

    class DocData implements Comparable <DocData> {
        public int _docId;
        public double _data;

        public DocData (int docId, double data){
            _docId = docId;
            _data = data;
        }

        public int compareTo(DocData o) {
            if (this._data == o._data) {
                return 0;
            }
            return (this._data > o._data) ? 1 : -1;
        }
    }
}
