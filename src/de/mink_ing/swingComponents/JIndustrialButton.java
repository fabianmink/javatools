package de.mink_ing.swingComponents;
import java.awt.*;
import javax.swing.*;
import javax.swing.plaf.ButtonUI;

public class JIndustrialButton extends AbstractButton
{

  private static final String uiClassID = "ButtonUI";

  private static final long serialVersionUID = 1899953482984737410L;
  
  public static final int ONBUTTON = 1;
  public static final int OFFBUTTON = 2;

  private boolean drawO = false;
  private boolean drawI = false;

  public JIndustrialButton() {
    setMinimumSize(new Dimension(10, 10));
    setPreferredSize(new Dimension(20, 20));
    setForeground(Color.white);
    setBorder(BorderFactory.createEmptyBorder());
    setModel(new DefaultButtonModel());
    updateUI();
  }
  
  public JIndustrialButton(int type) {
    
    this();
    
    switch(type){
      case 0:
        break;
      case ONBUTTON:
        drawI = true;
        setForeground(new Color(0,180,0));
        break;
      case OFFBUTTON:
        drawO = true;
        setForeground(new Color(255,0,0));
        break;
      
    }
    
    
    
  }
  

  public void updateUI() {
    setUI((ButtonUI)UIManager.getUI(this));
  }

  public String getUIClassID() {
    return uiClassID;
  }
  
  //public void setColor(Color color){
  //  this.fullColor = color;
  //  this.shadowColor = new Color((int)(color.getRed()*0.75),(int)(color.getGreen()*0.75),(int)(color.getBlue()*0.75));
  //}
  
  protected void paintComponent(Graphics g) {
    if (ui != null) {
      Graphics scratchGraphics = (g == null) ? null : g.create();
      try {
        reallyPaintComponent(scratchGraphics);
      }
      finally {
        scratchGraphics.dispose();
      }
    }
  }
  
  private void reallyPaintComponent(Graphics g) {
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

    double knobDiameter = 0.75;

    double oDiameter = 0.55;
    double oInnerDiameter = 0.4;
    double iheight = 0.50;
    double iwidth = 0.05;

    double knobHeightReleased = 0.07;
    
    //double knobHeightPushed = 0.02;
    double knobHeightPushed = 0.00;

    double knobHeight = knobHeightReleased;
    
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

    
    if(getModel().isPressed()){
      knobHeight = knobHeightPushed;
    }
    g.setColor(fullColor);
    
    int heightOffsX = (int)(knobHeight*w);
    int heightOffsY = (int)(knobHeight*h);
    g.fillOval(knobOffsX-heightOffsX, knobOffsY-heightOffsY, w-2*knobOffsX-1, h-2*knobOffsY-1);
    g.setColor(Color.black);
    g.drawOval(knobOffsX-heightOffsX, knobOffsY-heightOffsY, w-2*knobOffsX-1, h-2*knobOffsY-1);
    

    if(drawO){
        //O
        int oOffsX = (int) ((w*(1-oDiameter))/2);
        int oOffsY = (int) ((h*(1-oDiameter))/2);
    	g.setColor(Color.white);
    	g.fillOval(oOffsX-heightOffsX, oOffsY-heightOffsY, w-2*oOffsX-1, h-2*oOffsY-1);
    	
    	int oInOffsX = (int) ((w*(1-oInnerDiameter))/2);
        int oInOffsY = (int) ((h*(1-oInnerDiameter))/2);
    	
        g.setColor(fullColor);
        g.fillOval(oInOffsX-heightOffsX, oInOffsY-heightOffsY, w-2*oInOffsX-1, h-2*oInOffsY-1);
    }

    if(drawI){
      //I
      int iOffsX = (int) ((w*(1-iwidth))/2);
      int iOffsY = (int) ((h*(1-iheight))/2);
    	
      g.setColor(Color.white);
      g.fillRect(iOffsX-heightOffsX, iOffsY-heightOffsY, w-2*iOffsX, h-2*iOffsY);
    }
    


  }


}