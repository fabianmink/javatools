package de.mink_ing.parameter;

public class BooleanParameter extends AbstractParameter{
	private boolean value = false;
	
	public String getValueAsString() {
		return("" + value); 
	}
	
	public void setValueAsString(String value){
		this.value = java.lang.Boolean.parseBoolean(value);
	}
	
	public boolean getValue(){
		return value;
	}
	
	public void setValue(boolean value){
		this.value = value;
	}
}
