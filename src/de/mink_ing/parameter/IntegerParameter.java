package de.mink_ing.parameter;

public class IntegerParameter extends AbstractParameter{
	private int value = 0;
	
	public String getValueAsString() {
		return("" + value); 
	}
	
	public void setValueAsString(String value){
		this.value = java.lang.Integer.parseInt(value);
	}
	
	public int getValue(){
		return value;
	}
	
	public void setValue(int value){
		this.value = value;
	}
}
