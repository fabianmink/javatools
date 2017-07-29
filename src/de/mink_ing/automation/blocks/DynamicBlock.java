package de.mink_ing.automation.blocks;

public abstract class DynamicBlock implements IDynamicBlock {

	protected String name = "unnamed";
	
	//Parameters
	protected int Ts=1; //Sample time in user defined unit
	
	public DynamicBlock(){
	}
	
	public DynamicBlock(String name){
		this.name = name;
	}
	
	public DynamicBlock(int Ts){
		this.Ts = Ts;
	}
	
	public DynamicBlock(String name, int Ts){
		this.name = name;
		this.Ts = Ts;
	}
	
	public void setName(String name){
		this.name = name;
	}
	
	public String getName(){
		return(name);
	}
	
}
