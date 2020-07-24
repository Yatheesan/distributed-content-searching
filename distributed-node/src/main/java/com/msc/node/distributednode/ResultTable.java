package com.msc.node.distributednode;

import java.util.ArrayList;
import java.lang.StringBuilder;

public class ResultTable{

    private final int PADDINGSIZE = 4;
    private final char SEPERATOR = '*';

    private ArrayList<String> headers;
    private ArrayList<ArrayList<String>> table;
    private ArrayList<Integer> maxLength;


    public ResultTable(ArrayList<String> headersIn, ArrayList<ArrayList<String>> content){
        this.headers = headersIn;
        this.maxLength =  new ArrayList<Integer>();
        for(int i = 0; i < headers.size(); i++){
            maxLength.add(headers.get(i).length());
        }
        this.table = content;
        calcMaxLengthAll();
    }

    public void updateField(int row, int col, String input){
        table.get(row).set(col,input);
        calcMaxLengthCol(col);
    }


    public void printTable(){
        StringBuilder sb = new StringBuilder();
        StringBuilder sbRowSep = new StringBuilder();
        String padder = "";
        String rowSeperator = "";

        for(int i = 0; i < PADDINGSIZE; i++){
            padder += " ";
        }

        for(int i = 0; i < maxLength.size(); i++){
            sbRowSep.append("*");
            for(int j = 0; j < maxLength.get(i)+(PADDINGSIZE*2); j++){
                sbRowSep.append(SEPERATOR);
            }
        }
        sbRowSep.append("|");
        rowSeperator = sbRowSep.toString();

        sb.append(rowSeperator);
        sb.append("\n");
        sb.append("|");
        for(int i = 0; i < headers.size(); i++){
            sb.append(padder);
            sb.append(headers.get(i));
            for(int k = 0; k < (maxLength.get(i)-headers.get(i).length()); k++){
                sb.append(" ");
            }
            sb.append(padder);
            sb.append("|");
        }
        sb.append("\n");
        sb.append(rowSeperator);
        sb.append("\n");

        for(int i = 0; i < table.size(); i++){
            ArrayList<String> tempRow = table.get(i);
            sb.append("|");
            for(int j = 0; j < tempRow.size(); j++){
                sb.append(padder);
                sb.append(tempRow.get(j));
                for(int k = 0; k < (maxLength.get(j)-tempRow.get(j).length()); k++){
                    sb.append(" ");
                }
                sb.append(padder);
                sb.append("|");
            }
            sb.append("\n");
            sb.append(rowSeperator);
            sb.append("\n");
        }
        System.out.println(sb.toString());
    }


    private void calcMaxLengthAll(){
        for(int i = 0; i < table.size(); i++){
            ArrayList<String> temp = table.get(i);
            for(int j = 0; j < temp.size(); j++){
                if(temp.get(j).length() > maxLength.get(j)){
                    maxLength.set(j, temp.get(j).length());
                }
            }
        }
    }


    private void calcMaxLengthCol(int col){
        for(int i = 0; i < table.size(); i++){
            if(table.get(i).get(col).length() > maxLength.get(col)){
                maxLength.set(col, table.get(i).get(col).length());
            }
        }
    }
}
