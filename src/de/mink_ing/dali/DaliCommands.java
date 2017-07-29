package de.mink_ing.dali;

public class DaliCommands {

	public static final int BRC_ADDRESS_ARC = 0xFE;
	public static final int BRC_ADDRESS_CMD = 0xFF;

	public static int idv_address_arc(int address) {
		if(address < 0 || address > 63) throw new RuntimeException("Address out of range");
		
		return (0x00 + address*2);
	}
	
	public static int idv_address_cmd(int address) {
		if(address < 0 || address > 63) throw new RuntimeException("Address out of range");
		
		return (0x01 + address*2);
	}
	
	public static int grp_address_arc(int address) {
		if(address < 0 || address > 15) throw new RuntimeException("Address out of range");
		
		return (0x80 + address*2);
	}
	
	public static int grp_address_cmd(int address) {
		if(address < 0 || address > 15) throw new RuntimeException("Address out of range");
		
		return (0x81 + address*2);
	}
	
	
	public static final int QUERY_ACTUAL_LEVEL = 0xA0;

}
