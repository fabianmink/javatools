package de.mink_ing.swingComponents.dscope;

import java.awt.Color;

public class LineStyle {
	
	
	//Plot Style
	public boolean markersVisible = true;
	public int markersize = 2;
	
	//todo: Change to enum
	public static final int LINESTYLE_INTERP = 1;
	public static final int LINESTYLE_STAIRS = 2;
	public static final int LINESTYLE_STEM = 3;
	
	public int lineStyle = LINESTYLE_INTERP;
	public Color lineColor = Color.blue;
}
