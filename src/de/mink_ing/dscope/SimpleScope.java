/*
Copyright (c) 2017, Fabian Mink <fabian.mink@mink-ing.de>
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:

* Redistributions of source code must retain the above copyright notice, this
  list of conditions and the following disclaimer.

* Redistributions in binary form must reproduce the above copyright notice,
  this list of conditions and the following disclaimer in the documentation
  and/or other materials provided with the distribution.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

package de.mink_ing.dscope;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JPanel;

public class SimpleScope extends JPanel {

	private static final long serialVersionUID = -8134167429246974685L;

	private DataBuffer dataTrace;
	
	//Grid
	protected int noofxintv = 10; 
	protected int noofyintv = 10;
	private Color gridColor = Color.gray;

	//Plot Style
	protected boolean markersVisible = true;
	protected int markersize = 2;
	protected int lineStyle = 1;

	//Trace Properties
	protected double scale = 1.0;
	protected double offset = 0.0;


	public SimpleScope(){
		this.setBackground(Color.white);
		this.dataTrace = new DataBuffer();
	}

	public void addData(double data) {
		dataTrace.addData(data);
	}
	
	public double getMaxValue() {
		return(dataTrace.getBufferMax());
	}
	
	public double getMinValue() {
		return(dataTrace.getBufferMin());
	}
	
	public double getAvgValue() {
		return(dataTrace.getBufferAverage());
	}
	
	public void setScale(double scale) {
		this.scale = scale;
	}
	
	public void setOffset(double offset) {
		this.offset = offset;
	}
	
	public void setMinMax(double min, double max) {
		double delta = max-min;
		double avg = (max+min)/2.0;
		if(delta < 1e-20) delta = 1e-20;
		this.setScale(2.0/delta); //Scope goes from -1.0...1.0
		this.setOffset(-2.0*avg/delta);
	}

	protected void paintComponent(Graphics g) {

		super.paintComponent(g);
		this.drawScopePlot(g, this.getWidth(), this.getHeight());
	}

	private void drawScopePlot(Graphics g, int sizex, int sizey) {
		this.drawAxes(g, sizex, sizey);
		//g.translate(5, 5);
		//this.drawAxes(g, sizex-10, sizey-50);
		//g.translate(-5, -5);
	}

	private void drawAxes(Graphics g, int sizex, int sizey) {
		int maxx = sizex - 1;
		int maxy = sizey - 1;

		int i;

		double ymarker;
		int ymarker_px;
		int datalen;
		int dataPointer;

		int xlast, ylast, x, y;

		double xvalue;
		double yvalue;

		Graphics2D g2d = (Graphics2D) g;
		drawAxesGrid(g, sizex, sizey);

		datalen = dataTrace.getDataLen();

		xlast = -1;
		ylast = 0;

		g2d.setColor(Color.blue);

		ymarker =  offset * 2/((double)noofyintv);
		ymarker_px = (int)(maxy * 0.5 * (1-ymarker));
		
		//Marker (Arrow)
		if(false) {
			g2d.drawLine(-5, ymarker_px, 5, ymarker_px);
			g2d.drawLine(0, ymarker_px+5, 5, ymarker_px);
			g2d.drawLine(0, ymarker_px-5, 5, ymarker_px);
		}


		yvalue = java.lang.Double.NaN;

		//"Freeze" data pointer for current trace
		dataPointer = dataTrace.getCurrentDataPointer();

		//Draw Signal Trace
		for (i = 0; i <= datalen; i++) {

			yvalue = dataTrace.getElementFromFirst(i, dataPointer);

			if( !java.lang.Double.isNaN(yvalue)){
				xvalue = ((double)i) / ((double)datalen-1);  //Bildet das Array auf den Bereich 0...1 ab

				//yvalue = (yvalue * scale + offset ) * 2/((double)noofyintv); //Skalierung auf DIV
				yvalue = (yvalue * scale + offset ); //Skalierung und Offset

				yvalue = java.lang.Math.max(yvalue, -1.0);
				yvalue = java.lang.Math.min(yvalue, 1.0);

				x = (int)(xvalue*maxx);
				y = (int)(maxy * 0.5 * (1-yvalue));

				if(xlast != -1){

					//-> Interpolated
					if(lineStyle == 1)
						if(i < datalen)
							g2d.drawLine(xlast, ylast, x, y);

					//-> Stairs
					if(lineStyle == 2)
						if(i < datalen){
							g2d.drawLine(xlast, ylast, x, ylast);
							g2d.drawLine(x, ylast, x, y);
						}

					//-> Stem
					if(lineStyle == 3)
						g2d.drawLine(xlast, ylast, xlast, ymarker_px);


					//-> x
					if(markersVisible){
						g2d.drawLine(xlast-markersize, ylast-markersize, xlast+markersize, ylast+markersize); 
						g2d.drawLine(xlast-markersize, ylast+markersize, xlast+markersize, ylast-markersize);
					}

				}

				xlast = x;
				ylast = y;
			} //if( !java.lang.Double.isNaN(yvalue)){
		} //for i (datalen)



	} //private void drawAxes(Graphics g, int sizex, int sizey)

	private void drawAxesGrid(Graphics g, int sizex, int sizey){

		int maxx = sizex - 1;
		int maxy = sizey - 1;

		Graphics2D g2d = (Graphics2D) g;

		//Koordinatengitter zeichnen, wenn gewuenscht
		if(gridColor != null){
			g2d.setColor(gridColor);
			double tickdist = ((double)maxx) / ((double) noofxintv);
			for (int i = 0; i <= noofxintv; i++){
				g2d.drawLine((int)(i*tickdist), 0, (int)(i*tickdist), (int)(maxy));
			}
			tickdist = ((double)maxy) / ((double) noofyintv);
			for (int i = 0; i <= noofyintv; i++){
				g2d.drawLine(0, (int)(i*tickdist), (int)(maxx), (int)(i*tickdist));
			}
		}
	}


	//This class implements a ring buffer
	private class DataBuffer {

		private static final int defaultDatalen = 1000;


		private int datalen; 
		private int datapointer;
		private double databuffer[];

		public DataBuffer(int datalen){
			this.datalen = datalen; //redundant, because also the length of databuffer could be read; but may be faster access
			datapointer = 0;
			databuffer = new double[datalen];

			this.clearBuffer();
		}

		public DataBuffer(){
			this(defaultDatalen);
		}

		public void clearBuffer(){
			for(int i = 0; i < datalen; i++){
				databuffer[i] = java.lang.Double.NaN;
			}
		}

		public int getDataLen(){
			return this.datalen;
		}

		public void addData(double data){
			databuffer[datapointer] = data;
			datapointer++;
			if(datapointer>=this.datalen) datapointer = 0;
		} 

		//public double[] getDatabuffer(){
		//	return this.databuffer;
		//}
		public int getCurrentDataPointer(){
			return this.datapointer;
		}


		public double getElementFromLast(int position, int DataPointer){
			//datapointer points to next element to be written
			//thus, datapointer-1 points to latest element
			int index = datapointer - 1 - position;
			if(index < 0 ) index += this.datalen;

			return databuffer[index];
		}

		public double getElementFromFirst(int position, int DataPointer){
			//datapointer points to next element to be written
			//thus, it also points to the oldest element.
			int index = datapointer + position;
			if(index >= this.datalen) index -= this.datalen;

			return databuffer[index];
		}

		public double getLatestElement(){
			return getElementFromLast(0, getCurrentDataPointer());
		}

		public double getBufferMin(){
			double min;
			double val;
			min = databuffer[0];
			for(int i = 0; i<datalen; i++){
				val = databuffer[i];
				if( !java.lang.Double.isNaN(val) ){
					if(val<min) min = val;
				}
			}
			return(min);
		}

		public double getBufferMax(){
			double max;
			double val;
			max = databuffer[0];
			for(int i = 0; i<datalen; i++){
				val = databuffer[i];
				if( !java.lang.Double.isNaN(val) ){
					if(val>max) max = val;
				}
			}
			return(max);
		}

		
		public double getBufferAverage(){
			double avg=0.0;
			double val;
			int cnt = 0;

			for(int i = 0; i<datalen; i++){
				val = databuffer[i];
				if( !java.lang.Double.isNaN(val) ){
					cnt++;
					avg += val;
				}
			}

			return(avg / (double)cnt);
		}

	}

}
