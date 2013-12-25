package com.hacktics.viewstate;

public enum MicrosoftTypes implements IType {
	  Type_Int16(1),
      Type_Int32(2),
      Type_Int64(3),
      Type_Char(4), 
      Type_String(5),
      Type_DateTime(6), 
      Type_Double(7), 
      Type_Single(8),
      Type_Color(9), 
      Type_KnownColor(10),
      Type_IntEnum(11),
      Type_EmptyColor(12),
      Type_Pair(15), 
      Type_Triplet(16),
      Type_Array(20), 
      Type_StringArray(21), 
      Type_ArrayList(22),
      Type_Hashtable(23), 
      Type_HybridDictionary(24),
      Type_Type(25),
	  Type_Unit(27),
      Type_EmptyUnit(28),
      Type_IndexedStringAdd(30),
      Type_IndexedString(31),
      Type_StringFormatted(40),
      TypeRefAdd(41),
      TypeRefAddLocal(42),
      TypeRef(43),
      Type_BinarySerialized(50),
      Type_SparseArray(60),
      Type_Null(100),
      Type_EmptyString(101),
      Type_ZeroInt32(102),
      Type_True(103),
      Type_False(104);
                 
      private int type;
      private MicrosoftTypes(int t) {
    	  type = t;
      }
      public int getType () {
    	  return type;
      }
      
      public static MicrosoftTypes fromInt(int i) {
    	  if (i > 0) {
    	      for (MicrosoftTypes t : MicrosoftTypes.values()) {
    	        if (i==t.type) {
    	          return t;
    	        }
    	      }
    	    }
    	    return null;
      }
 }

