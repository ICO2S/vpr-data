package org.virtualparts.sbol;

public enum Direction {
	Input {
		 @Override
		    public String toString() {
		      return "input";
		    }		
	},
	Output
	{
		 @Override
		    public String toString() {
		      return "output";
		    }
	}
}
