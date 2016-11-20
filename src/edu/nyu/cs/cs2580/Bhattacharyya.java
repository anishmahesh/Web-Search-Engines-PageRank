package edu.nyu.cs.cs2580;

import edu.nyu.cs.cs2580.SearchEngine.Options;

import java.io.*;
import java.util.ArrayList;
import java.util.DoubleSummaryStatistics;
import java.util.HashMap;
import java.util.Scanner;

/**
 * Created by naman on 11/11/2016.
 */
public class Bhattacharyya {
    private static Options _options = null;

    public Bhattacharyya(Options options) {
        _options = options;
    }

    public static void main(String[] args) throws IOException {
        Bhattacharyya bc = new Bhattacharyya(new Options("conf/engine.conf"));
        String pathPrfOutput, pathOutputFile;
        if(args[0].contains(".tsv")){
            //Split content of TSV file and return the path where split content is found
            pathPrfOutput = splitInMultipleFiles(args[0]);
        } else {
            pathPrfOutput = args[0];
        }
        if(args[1].contains(".tsv")){
            pathOutputFile = _options._indexPrefix + "/Bhattacharya/" + args[1];
        } else {
            pathOutputFile = args[1];
        }
        bc.querySimilarity(pathPrfOutput, pathOutputFile);
    }

    private static String splitInMultipleFiles(String tsvFileName) {

        return new String();
    }

    public void querySimilarity(String pathPrfOutput, String pathOutputFile) throws IOException {
        File queryFolder = new File(pathPrfOutput);
        File[] fileList = queryFolder.listFiles();
        ArrayList<String> queriesList = getAllQueries();
        FileWriter outputFile = new FileWriter(pathOutputFile);
        BufferedWriter output = new BufferedWriter(outputFile);

        for (int i = 1; i <= fileList.length; i++) {
            double beta = 0.0;

            File file1 = new File(pathPrfOutput + "/prf-" + i + ".tsv");
            if(!file1.exists()){
                break;
            }
            BufferedReader reader1 = new BufferedReader(new FileReader(file1));
            for (int j = i; j <= fileList.length; j++) {
                    File file2 = new File(pathPrfOutput + "/prf-" + j + ".tsv");
                    if (!file2.exists()) {
                        break;
                    }
                    BufferedReader reader2 = new BufferedReader(new FileReader(file2));

                    String line = null;
                    HashMap<String, Double> queryProbabilityMap = new HashMap<String, Double>();
                    while ((line = reader1.readLine()) != null) {
                        Scanner s = new Scanner(line);
                        queryProbabilityMap.put(s.next().toString(), Double.parseDouble(s.next()));
                    }
                    reader1.close();
                    String queryTerm = null;
                    double probability = 0;
                    while ((line = reader2.readLine()) != null) {
                        Scanner s = new Scanner(line);
                        queryTerm = s.next().toString();
                        probability = Double.parseDouble(s.next());
                        if (queryProbabilityMap.containsKey(queryTerm)) {
                            beta += Math.sqrt(queryProbabilityMap.get(queryTerm) * probability);
                        }
                    }
                    reader2.close();
                    output.write(queriesList.get(i) + "\t" + queriesList.get(j) + "\t" + Double.toString(beta) + "\n");
            }
        }
        output.close();
    }

    private ArrayList<String> getAllQueries() throws IOException {
        BufferedReader br =  new BufferedReader(new FileReader(_options._indexPrefix+"/queries.tsv"));
        String line;
        ArrayList<String> queryList = new ArrayList<String>();
        while((line = br.readLine()) != null){
            queryList.add(line);
        }
        br.close();
        return queryList;
    }
}
