package de.mink_ing.automation.blocks;

import java.util.logging.Logger;

public abstract class DynamicBlockLog extends DynamicBlock {

	protected Logger logger;
	protected String name = "unnamed";
	
	public DynamicBlockLog(){
		super();
	}
	
	public DynamicBlockLog(String name){
		super(name);
	}
	
	public DynamicBlockLog(int Ts){
		super(Ts);
	}
	
	public DynamicBlockLog(String name, int Ts){
		super(name, Ts);
	}
	
	public void setLogger(Logger logger){
		this.logger = logger;
	}
	
	public void setName(String name){
		this.name = name;
	}
	
	public String getName(){
		return(name);
	}
	
}
