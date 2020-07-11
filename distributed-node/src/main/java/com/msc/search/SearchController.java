package com.msc.search;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.msc.node.distributednode.QueryHandler;
import com.msc.node.distributednode.ResultTable;

public class SearchController {

	private MessageBroker msgBroker;

    private Map<Integer, SearchResponse> fileDownloadOptions;

    public SearchController(MessageBroker msgBroker) {
        this.msgBroker = msgBroker;
    }

    public int doSearch(String searchWord) {

        Map<String, SearchResponse> response
                = new HashMap<String, SearchResponse>();

        QueryHandler queryHitHandler = QueryHandler.getInstance();
        queryHitHandler.setSearchResutls(response);
        queryHitHandler.setSearchInitiatedTime(System.currentTimeMillis());

        this.msgBroker.doSearch(searchWord);

        System.out.println("Results are retireving..");

        try {
            Thread.sleep(3000);

        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        printSearchResponses(response);
        this.clearSearchResponses();
        return fileDownloadOptions.size();
    }

    List<String> doUISearch(String keyword) {

        Map<String, SearchResponse> SearchResponses
                = new HashMap<String, SearchResponse>();

        QueryHandler queryHitHandler = QueryHandler.getInstance();
        queryHitHandler.setSearchResutls(SearchResponses);
        queryHitHandler.setSearchInitiatedTime(System.currentTimeMillis());

        this.msgBroker.doSearch(keyword);

        System.out.println("Results are retireving..");

        try {
            Thread.sleep(3000);

        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        List<String> results = new ArrayList<String>();

        int fileIndex = 1;

        this.fileDownloadOptions = new HashMap<Integer, SearchResponse>();

        for (String s : SearchResponses.keySet()) {
            SearchResponse SearchResponse = SearchResponses.get(s);
            String temp = "" + SearchResponse.getName() + "\t" +
                    SearchResponse.getAddress() + ":" + SearchResponse.getPort() + "\t" +
                    SearchResponse.getNumberOfHops() + "\t" + SearchResponse.getTime() + "ms";
            this.fileDownloadOptions.put(fileIndex, SearchResponse);
            results.add(temp);
            fileIndex++;
        }

        this.clearSearchResponses();

        return results;
    }

    private void clearSearchResponses() {
        QueryHandler queryHitHandler = QueryHandler.getInstance();

        queryHitHandler.setSearchResutls(null);
    }

    private void printSearchResponses(Map<String, SearchResponse> SearchResponses) {

        System.out.println("\nFile search results : ");

        ArrayList<String> headers = new ArrayList<String>();
        headers.add("Option No");
        headers.add("FileName");
        headers.add("Source");
        headers.add("QueryHit time (ms)");
        headers.add("Hop count");

        ArrayList<ArrayList<String>> content = new ArrayList<ArrayList<String>>();

        int fileIndex = 1;

        this.fileDownloadOptions = new HashMap<Integer, SearchResponse>();

        for (String s : SearchResponses.keySet()) {
            SearchResponse SearchResponse = SearchResponses.get(s);
            this.fileDownloadOptions.put(fileIndex, SearchResponse);

            ArrayList<String> row1 = new ArrayList<String>();
            row1.add("" + fileIndex);
            row1.add(SearchResponse.getName());
            row1.add(SearchResponse.getAddress() + ":" + SearchResponse.getPort());
            row1.add("" + SearchResponse.getTime());
            row1.add("" + SearchResponse.getNumberOfHops());

            content.add(row1);

            fileIndex++;
        }

        if (fileDownloadOptions.size() == 0) {
            System.out.println("Not found.");

            return;
        }

        ResultTable result = new ResultTable(headers, content);
        result.printTable();

    }

    public SearchResponse getFileDetails(int fileIndex) {
        return this.fileDownloadOptions.get(fileIndex);
    }
}
