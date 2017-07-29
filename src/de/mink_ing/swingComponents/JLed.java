package de.mink_ing.swingComponents;

import java.awt.*;

import javax.swing.*;

public class JLed extends JComponent
{

  public JLed() {
    setMinimumSize(new Dimension(10, 10));
    setPreferredSize(new Dimension(20, 20));
    setForeground(Color.red);
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
	  
	double ledDiameter = 0.65; 
	int w = this.getWidth();
	int h = this.getHeight();
	    
	Color onColor = this.getForeground();
	//todo: avoid creating new object -> Put to setForeground
	Color offColor = new Color((int)(onColor.getRed()*0.5),(int)(onColor.getGreen()*0.5),(int)(onColor.getBlue()*0.5));
	
    g.setColor(this.getBackground());
    g.fillRect(0, 0, w, h);  //fill opaque with bg color (see description of JComponent.paintComponent)

    g.setColor(Color.black);
    g.fillOval(0, 0, w-1, h-1);

    if(isEnabled()){
      g.setColor(onColor);//on
    }
    else{
    	g.setColor(offColor);//on
    }
    int ledOffsX = (int) ((w*(1-ledDiameter))/2);
    int ledOffsY = (int) ((h*(1-ledDiameter))/2);
    
    g.fillOval(ledOffsX, ledOffsY, w-2*ledOffsX-1, h-2*ledOffsY-1);
  }

}