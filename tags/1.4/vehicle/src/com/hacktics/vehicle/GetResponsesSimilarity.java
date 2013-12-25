package com.hacktics.vehicle;

/**
*
* @author michalg
*/
public class GetResponsesSimilarity {
    
   // Returns the similarity between the responses in percentage (0-100%)
   // If the responses are similar - returns 100 %
   // If the responses are completely different (no similar character) - returns 0%
   public static int getResponsesSimilarity(String response1, String response2) {
       Double d = 0.00000;
       if (response1.equalsIgnoreCase(response2)) {
           return 100;
       }
       String[] response1_base = response1.split(" ");
       String[] response2_base = response2.split(" ");
       int score = 0;
       for (int r1 = 0; r1 < response1_base.length; r1++) {
           for (int r2 = 0; r2 < response2_base.length; r2++) {
               if (response1_base[r1].toLowerCase().compareTo(response2_base[r2].toLowerCase()) == 0) {
                   score++;
                   break;
               }
           }
       }
       d = ((double) score * (double) 2) / (double) ((double) response1_base.length + (double) response2_base.length);
       d = d * 100;
       int result = d.intValue();
       return result;
   }
}