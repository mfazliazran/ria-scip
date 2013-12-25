package com.hacktics.vehicle;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class TokenLocations {

	// gets the original response and the token
	// returns the number of the token instances in the original response
	// In case the token does not exist in the original response - returns zero
	public static int numberOfTokenInstances(String originalResponseStr, String token) {
		int numberOfInstances = 0;
		
		int startSearchLocation = 0;
        while (originalResponseStr.indexOf(token, startSearchLocation) != -1){
        	numberOfInstances++;
        	int nextTokenStartIndex = originalResponseStr.indexOf(token, startSearchLocation);
            startSearchLocation = nextTokenStartIndex + token.length();
        }

		return numberOfInstances;
	}
	
	// gets the original response and the token
	// returns an array of TokenWrappers of each instance of the token
	// each TokenWrapper instance contains: 
	// 1) startStr - the string prior to the token (starting from the previous token instance)
	// 2) tokenStr - the token's string
	// 3) endStr - the string coming after the token (ending at the next token instance)
	public static ArrayList<TokenWrapper> tokenWrapperInstances(String originalResponseStr, String token) {
		
		ArrayList<TokenWrapper> wrapperArray = new ArrayList<TokenWrapper>();
		
		int startIndexOfStartStr = 0;
        int endIndexOfStartStr;
        int startIndexOfEndtStr;
        int endtIndexOfEndtStr;
        
        int startSearchLocation = 0;
        while (originalResponseStr.indexOf(token, startSearchLocation) != -1){
            int nextTokenStartIndex = originalResponseStr.indexOf(token, startSearchLocation);
            
            // Start String
        	endIndexOfStartStr = nextTokenStartIndex;
            String startStr = originalResponseStr.substring(startIndexOfStartStr, endIndexOfStartStr);
        	
            // Token String
            String tokenStr = originalResponseStr.substring(nextTokenStartIndex, nextTokenStartIndex + token.length());
            
        	// End String
            startIndexOfEndtStr = (nextTokenStartIndex + token.length());
        	// If there is a next token instance
            if ((originalResponseStr.indexOf(token, nextTokenStartIndex + token.length())) != -1){
            	startSearchLocation = nextTokenStartIndex + token.length();
            	int nextTokenStartIndexTemp = originalResponseStr.indexOf(token, startSearchLocation);
            	endtIndexOfEndtStr = nextTokenStartIndexTemp;
                // update the start index of start string
                startIndexOfStartStr = nextTokenStartIndex + token.length();
        	}
        	else
        	{
        		endtIndexOfEndtStr = originalResponseStr.length();
        		startSearchLocation = nextTokenStartIndex + token.length();
        	}
        	String endStr = originalResponseStr.substring(startIndexOfEndtStr, endtIndexOfEndtStr);

        	TokenWrapper currentTokenWrapper = new TokenWrapper(startStr, tokenStr, endStr);
        	wrapperArray.add(currentTokenWrapper);       
        }
         
		return wrapperArray;
	}

	// gets the array of token instances from "tokenLocations" function and a number of instances to remove
	// and removes the first number of token instances from the original response
	// when numberToRemove<0 --> remove ALL instances of the token
	// when numberToRemove>number of actual token instances --> remove ALL instances of the token
	public static String removeTokenInstances(ArrayList<TokenWrapper> tokenInstances, int numberToRemove) {
		String newResponse = "";
		
		if ((numberToRemove < 0) || (numberToRemove > tokenInstances.size()))
		{
			numberToRemove = tokenInstances.size();
		}

		for (int i=0; i < numberToRemove; i++)
		{
			TokenWrapper tw = tokenInstances.get(i);
			// removes the token in index i
			newResponse = newResponse + tw.startStr;
		}
		
		for (int i=numberToRemove; i < tokenInstances.size(); i++)
		{
			TokenWrapper tw = tokenInstances.get(i);
			// removes the token in index i
			newResponse = newResponse + tw.startStr + tw.tokenStr;
		}		
		
		newResponse = newResponse + tokenInstances.get(tokenInstances.size()-1).endStr;
		
		return newResponse;
	}

}