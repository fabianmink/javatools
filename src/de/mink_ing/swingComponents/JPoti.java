package de.mink_ing.swingComponents;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.*;

public class JPoti extends JComponent
{
	double angle = 6;
	
	public JPoti() {
		setMinimumSize(new Dimension(20, 20));
		setPreferredSize(new Dimension(50, 50));
		setForeground(Color.white);
		this.addMouseListener(new MouseAdapter() { //does not work!!
			@Override
			public void mousePressed(MouseEvent e) {
				int px = e.getX();
				int py = e.getY();
				int w = getWidth();
				int h = getHeight();
				angle = Math.atan2(py-h/2, px-w/2);
				//angle = angle + 0.5;
				repaint();
			}
		});
	}


	protected void paintComponent(Graphics g) {
		//if (ui != null) {
		Graphics scratchGraphics = (g == null) ? null : g.create();
		try {
			reallyPaintComponent(scratchGraphics);
		}
		finally {
			scratchGraphics.dispose();
		}
		//}
}

	public void reallyPaintComponent(Graphics g) {

		//Attention!!! fillRect draws Rectangle of width height pixels
		//fillOvel draws oval of width+1 height+1 pixels!!
		//The component drawing area goes from 0... getWidth-1 / 0... getHeight-1, this ist DOUBLE CHECKED!
		int w = this.getWidth();
		int h = this.getHeight();

		Color fullColor = this.getForeground();
		//todo: avoid creating new object -> Put to setForeground
		Color shadowColor = new Color((int)(fullColor.getRed()*0.75),(int)(fullColor.getGreen()*0.75),(int)(fullColor.getBlue()*0.75));
			
		
		g.setColor(this.getBackground());
		//g.setColor(Color.RED); //for testing
		g.fillRect(0, 0, w, h);  //fill opaque with bg color (see description of JComponent.paintComponent)

		double knobDiameter = 0.6;
		double knobHeight =  0.12;
		double indDiameter = 0.18;
		double indRadius = 0.22;
		
		g.setColor(Color.gray);
		g.fillOval(0, 0, w-1, h-1);
		g.setColor(Color.black);
		g.drawOval(0, 0, w-1, h-1);

		int knobOffsX = (int) ((w*(1-knobDiameter))/2);
		int knobOffsY = (int) ((h*(1-knobDiameter))/2);

		g.setColor(shadowColor);
		g.fillOval(knobOffsX, knobOffsY, w-2*knobOffsX-1, h-2*knobOffsY-1);

		g.setColor(Color.black);
		g.drawOval(knobOffsX, knobOffsY, w-2*knobOffsX-1, h-2*knobOffsY-1);
		
		g.setColor(fullColor);
	    
	    int heightOffsX = (int)(knobHeight*w);
	    int heightOffsY = (int)(knobHeight*h);
	    g.fillOval(knobOffsX-heightOffsX, knobOffsY-heightOffsY, w-2*knobOffsX-1, h-2*knobOffsY-1);
	    g.setColor(Color.black);
	    g.drawOval(knobOffsX-heightOffsX, knobOffsY-heightOffsY, w-2*knobOffsX-1, h-2*knobOffsY-1);
	    
	    int indOffsX = (int)(w*indRadius*Math.cos(angle));
	    int indOffsY = (int)(h*indRadius*Math.sin(angle));
	    
	    int indRadInt = (int) ((w*indDiameter)/2);
	    g.setColor(Color.black);
	    //g.fillOval(10, 10, indRadInt*2, indRadInt*2);
	    g.fillOval(w/2+indOffsX-indRadInt-heightOffsX, h/2+indOffsY-indRadInt-heightOffsY, indRadInt*2, indRadInt*2);
		    
	    
	    //Graphics2D g2 = (Graphics2D) g;
        //g2.setStroke(new BasicStroke(2));
        //g2.draw(new Line2D.Float(0, 0, 80, 90));
	    
	}

}