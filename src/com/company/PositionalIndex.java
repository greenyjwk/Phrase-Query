package com.company;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

/**
 * ISTE-612-2215 Lab #2
 * Ji Woong Kim
 * March 3 2022
 */

public class PositionalIndex {
    String[] myDocs;
    ArrayList<String> termDictionary;
//    ArrayList<ArrayList<Doc>> docLists;
    HashMap<String, ArrayList<Doc>> docLists;

    /**
     * Construct a positional index
     * @param docs List of input strings or file names
     *
     */
    public PositionalIndex(String[] docs) {

        myDocs = docs;
        termDictionary = new ArrayList<String>();             // Postings list
        docLists = new HashMap<>();     // This is the list of the postings list
        ArrayList<Doc> docList = new ArrayList<>();

        for(int i = 0; i < myDocs.length; i++){

            String[] tokens = myDocs[i].split(" ");
            for(int j = 0; j < tokens.length; j++){

                if(termDictionary.contains(tokens[j])){
                    docList = docLists.get(tokens[j]);

                    boolean check = false;
                    for(Doc doc :docList){
                        if(doc.docId == i){
                            doc.positionList.add(j);
                            check = true;
                        }
                    }
                    if(!check){
                        Doc doc = new Doc(i, j);
                        docList.add(doc);
                    }
                }else{
                    termDictionary.add(tokens[j]);
                    Doc doc = new Doc(i, j);
                    ArrayList<Doc> docIdList = new ArrayList<>();
                    docIdList.add(doc);
                    docLists.put( tokens[j] ,docIdList);
                }
            }
        }
    }

    /**
     * Return string representation of a positional index
     */
    public String toString()
    {
        String matrixString = new String();
        ArrayList<Doc> docList;
        for(int i=0;i<termDictionary.size();i++){
            matrixString += String.format("%-15s", termDictionary.get(i));
            docList = docLists.get(i);
            for(int j=0;j<docList.size();j++)
            {
                matrixString += docList.get(j)+ "\t";
            }
            matrixString += "\n";
        }
        return matrixString;
    }

    /**
     *
     * @param post1 first postings
     * @param post2 second postings
     * @return merged result of two postings
     */
    public ArrayList<Doc> intersect(ArrayList<Doc> post1, ArrayList<Doc> post2)
    {

        ArrayList<Doc> intersectList = new ArrayList<>();
        //TASK2: TO BE COMPLETED
//        for (Doc doc1 : post1){
        for (int q = 0 ;  q < post1.size() ; q++){
            for(int w = 0 ;  w < post2.size(); w++){
                Doc doc1 = post1.get(q);
                Doc doc2 = post2.get(w);

                if(doc1.docId == doc2.docId){
                    ArrayList<Integer> temp = doc1.positionList;
                    for (int i = 0; i < doc2.positionList.size(); i++) {
                        temp.add(doc2.positionList.get(i));
                    }
                    Collections.sort(temp);

                    for(int i = 0; i < temp.size()-1 ; i++){
                        int indexInterval = temp.get(i+1) - temp.get(i);
                        if(indexInterval == 1){
                            intersectList.add(doc1);
                        }
                    }
                }
            }
        }
        return intersectList; // To be modified
    }

    /**
     *
     * @param query a phrase query that consists of any number of terms in the sequential order
     * @return docIds of documents that contain the phrase
     */
    public ArrayList<Doc> phraseQuery(String[] query)
    {
        //TASK3: TO BE COMPLETED
        ArrayList<Doc> queryResult = new ArrayList<>();
        if(termDictionary.contains(query[0]) & termDictionary.contains(query[1]) ){

            ArrayList<Doc> posting1 = docLists.get(query[0]);
            ArrayList<Doc> posting2 = docLists.get(query[1]);
            queryResult = intersect(posting1, posting2);
            
        }else{
            System.out.println("The words are not searched in the list");
        }

        return queryResult; // To be modified
    }


    public static void main(String[] args) {
        String[] docs = {"text warehousing over big data",
                "dimension data warehouse over big data",
                "nlp after text mining",
                "nlp after text classification"};

        PositionalIndex pi = new PositionalIndex(docs);
        System.out.print(pi.docLists);
        System.out.println();

        System.out.println("------------------ Test 1 ------------------");
        String SearchTerm = "text mining";
        String[] search = SearchTerm.split(" ");
        ArrayList<Doc> queryResult = pi.phraseQuery(search);
        for(Doc doc:queryResult) System.out.println("Document ID is : " + doc.docId);


        System.out.println("------------------ Test 2 ------------------");
        SearchTerm = "big data";
        search = SearchTerm.split(" ");
        queryResult = pi.phraseQuery(search);
        for(Doc doc:queryResult) System.out.println("Document ID is : " + doc.docId);


        System.out.println("------------------ Test 3 ------------------");
        SearchTerm = "nlp after";
        search = SearchTerm.split(" ");
        queryResult = pi.phraseQuery(search);
        for(Doc doc:queryResult) System.out.println("Document ID is : " + doc.docId);


        System.out.println("------------------ Test 4 ------------------");
        SearchTerm = "warehouse over";
        search = SearchTerm.split(" ");
        queryResult = pi.phraseQuery(search);
        for(Doc doc:queryResult) System.out.println("Document ID is : " + doc.docId);


        System.out.println("------------------ Test 5 ------------------");   // Two lists are overllapped !!!!!!!!!!! needed to fix!!!!!!!!!!!! :)
        SearchTerm = "over big";
        search = SearchTerm.split(" ");
        queryResult = pi.phraseQuery(search);
        System.out.println(queryResult);
        for(Doc doc:queryResult) System.out.println("Document ID is : " + doc.docId);


        //TASK4: TO BE COMPLETED: design and test phrase queries with 2-5 terms
    }
}

/**
 *
 * Document class that contains the document id and the position list
 */
class Doc{
    int docId;
    ArrayList<Integer> positionList;
    public Doc(int did)
    {
        docId = did;
        positionList = new ArrayList<Integer>();
    }
    public Doc(int did, int position)
    {
        docId = did;
        positionList = new ArrayList<Integer>();
        positionList.add(new Integer(position));
    }

    public void insertPosition(int position)
    {
        positionList.add(new Integer(position));
    }

    public String toString()
    {
        String docIdString = ""+docId + ":<";
        for(Integer pos:positionList)
            docIdString += pos + ",";
        docIdString = docIdString.substring(0,docIdString.length()-1) + ">";
        return docIdString;
    }
}