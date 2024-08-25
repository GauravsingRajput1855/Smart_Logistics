package com.couriermanagement.utility;

public class Helper {
	
	public static String getAlphaNumericUniqueId(int uniqueIdLength)
    {
  
        String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
                                    + "0123456789"
                                    + "abcdefghijklmnopqrstuvxyz";
  
        StringBuilder sb = new StringBuilder(10);
  
        for (int i = 0; i < uniqueIdLength; i++) {
  
            int index
                = (int)(AlphaNumericString.length()
                        * Math.random());
  
            sb.append(AlphaNumericString
                          .charAt(index));
        }
  
        return sb.toString().toUpperCase();
    }

}
