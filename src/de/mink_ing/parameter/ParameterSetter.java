package de.mink_ing.parameter;

import java.io.PrintStream;

import de.mink_ing.IExecutable;

public class ParameterSetter implements IExecutable{
	private ParameterList paraList;
	
	public ParameterSetter(ParameterList paraList){
		this.paraList = paraList;
	}
	
	public void main(String[] args, PrintStream out){
		int paraNum = 0;
		if(args.length < 2){
			out.println("ERR - wrong number of arguments");
			return;
		}
				
		try{ 
			paraNum = java.lang.Integer.parseInt(args[0]);
		}
		catch(NumberFormatException e){
			out.println("ERR - wrong parameter number format. Must be integer number");
			return;
		}
		
		out.println("Parameter no.: " + paraNum);
		
		IParameter parameter = null; 
		try{
			parameter = paraList.get(paraNum);
		}
		catch(IndexOutOfBoundsException e){
			
		}
		if(parameter == null){
			out.println("ERR - Parameter not existing");
			return;
		}
		
		try{
			synchronized (parameter) {
				parameter.setValueAsString(args[1]);	
			}
			
		}
		catch(Exception e){
			out.println("ERR - wrong parameter format");
			return;
		}
	}

}
