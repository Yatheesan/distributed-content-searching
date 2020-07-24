package com.msc.node.distributednode.search;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.msc.node.distributednode.QueryHandling;
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

        QueryHandling queryHandler = QueryHandling.getInstance();
        queryHandler.setSearchResutls(response);
        queryHandler.setSearchInitiatedTime(System.currentTimeMillis());

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


    private void clearSearchResponses() {
        QueryHandling queryHitHandler = QueryHandling.getInstance();

        queryHitHandler.setSearchResutls(null);
    }

    private void printSearchResponses(Map<String, SearchResponse> SearchResponses) {

        System.out.println("\nFile search results : ");

        ArrayList<String> headers = new ArrayList<String>();
        headers.add("Number");
        headers.add("Name");
        headers.add("Source address");
        headers.add("Searching time (ms)");
        headers.add("Hop counts");

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
