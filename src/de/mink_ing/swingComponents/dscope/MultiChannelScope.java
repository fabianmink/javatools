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

package de.mink_ing.swingComponents.dscope;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JPanel;

public class MultiChannelScope extends JPanel {

	private static final long serialVersionUID = 5456025372588398417L;

	private DataBuffer dataTrace;
	private LineStyle[] lineStyles;

	//Grid
	protected int noofxintv = 10; 
	protected int noofyintv = 10;
	private Color gridColor = Color.gray;

	//Trace Properties
	protected double scale = 1.0;
	protected double offset = 0.0;

	
	private void initStyles() {
		Color colortbl[] = {Color.BLACK, Color.BLUE, Color.RED, Color.GREEN, Color.CYAN, Color.MAGENTA, Color.ORANGE, Color.PINK};
		
		for(int is = 0; is<lineStyles.length; is++) {
			lineStyles[is] = new LineStyle();
			//lineStyles[is].lineStyle = LineStyle.LINESTYLE_STAIRS;
			lineStyles[is].lineColor = colortbl[is % colortbl.length];
		}
	}

	public MultiChannelScope(int noOfChannels){
		this.setBackground(Color.white);
		this.dataTrace = new DataBuffer(noOfChannels);
		this.lineStyles = new LineStyle[noOfChannels];
		initStyles();
	}

	public void addData(double[] data) {
		dataTrace.addData(data);
	}

	public double getMaxValue() {
		return(dataTrace.getBufferMax());
	}

	public double getMinValue() {
		return(dataTrace.getBufferMin());
	}

	//public double getAvgValue() {
	//	return(dataTrace.getBufferAverage());
	//}

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

		double ymarker;
		int ymarker_px;
		int noOfChannels, datalen;
		int dataPointer;

		int xlast, ylast, x, y;

		double xvalue;
		double yvalue;

		Graphics2D g2d = (Graphics2D) g;
		drawAxesGrid(g, sizex, sizey);

		noOfChannels = dataTrace.getNoOfChannels();
		datalen = dataTrace.getDataLen();

		//"Freeze" data pointer for current trace
		dataPointer = dataTrace.getCurrentDataPointer();

		for (int ich = 0; ich < noOfChannels; ich++) {
			
			int markersize = 2;
			
			xlast = -1;
			ylast = 0;

			g2d.setColor(lineStyles[ich].lineColor);

			ymarker =  offset * 2/((double)noofyintv);
			ymarker_px = (int)(maxy * 0.5 * (1-ymarker));

			//Marker (Arrow)
			//g2d.drawLine(-5, ymarker_px, 5, ymarker_px);
			//g2d.drawLine(0, ymarker_px+5, 5, ymarker_px);
			//g2d.drawLine(0, ymarker_px-5, 5, ymarker_px);

			yvalue = java.lang.Double.NaN;

			//Draw Signal Trace
			for (int i = 0; i <= datalen; i++) {

				yvalue = dataTrace.getElementFromFirst(ich, i, dataPointer);

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
						if(lineStyles[ich].lineStyle == LineStyle.LINESTYLE_INTERP) {
							if(i < datalen)
								g2d.drawLine(xlast, ylast, x, y);
						}

						//-> Stairs
						if(lineStyles[ich].lineStyle == LineStyle.LINESTYLE_STAIRS) {
							if(i < datalen){
								g2d.drawLine(xlast, ylast, x, ylast);
								g2d.drawLine(x, ylast, x, y);
							}
						}

						//-> Stem
						if(lineStyles[ich].lineStyle == LineStyle.LINESTYLE_STEM) {
							g2d.drawLine(xlast, ylast, xlast, ymarker_px);
						}

						//-> x
						if(lineStyles[ich].markersVisible){
							g2d.drawLine(xlast-markersize, ylast-markersize, xlast+markersize, ylast+markersize); 
							g2d.drawLine(xlast-markersize, ylast+markersize, xlast+markersize, ylast-markersize);
						}

					}

					xlast = x;
					ylast = y;
				} //if( !java.lang.Double.isNaN(yvalue)){
			} //for i (datalen)
		} //for id (noOfChannels)



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

	//This class implements an N-Channel ring buffer
	private class DataBuffer {

		private static final int defaultNoOfChannels = 1;
		private static final int defaultDatalen = 1000;

		private int noOfChannels; 
		private int datalen; 
		private int datapointer;
		private double databuffer[][];

		public DataBuffer(int noOfChannels, int datalen){
			this.noOfChannels = noOfChannels; //redundant
			this.datalen = datalen; //redundant, because also the length of databuffer could be read; but may be faster access
			datapointer = 0;
			databuffer = new double[noOfChannels][datalen];

			this.clearBuffer();
		}

		public DataBuffer(int noOfChannels){
			this(noOfChannels, defaultDatalen);
		}

		public DataBuffer(){
			this(defaultNoOfChannels, defaultDatalen);
		}

		public void clearBuffer(){
			for(int ich = 0; ich < noOfChannels; ich++){
				for(int il = 0; il < datalen; il++){
					databuffer[ich][il] = java.lang.Double.NaN;
				}
			}
		}

		public int getDataLen(){
			return this.datalen;
		}

		public int getNoOfChannels(){
			return this.noOfChannels;
		}

		public void addData(double[] data){
			for(int ich = 0; ich < noOfChannels; ich++){
				if(ich < data.length) {
					databuffer[ich][datapointer] = data[ich];
				}
				else {
					databuffer[ich][datapointer] = java.lang.Double.NaN;
				}
			}
			datapointer++;
			if(datapointer>=this.datalen) datapointer = 0;
		} 

		//public double[] getDatabuffer(){
		//	return this.databuffer;
		//}
		public int getCurrentDataPointer(){
			return this.datapointer;
		}


		public double getElementFromLast(int channel, int position, int DataPointer){
			//datapointer points to next element to be written
			//thus, datapointer-1 points to latest element
			int index = datapointer - 1 - position;
			if(index < 0 ) index += this.datalen;

			return databuffer[channel][index];
		}

		public double getElementFromFirst(int channel, int position, int DataPointer){
			//datapointer points to next element to be written
			//thus, it also points to the oldest element.
			int index = datapointer + position;
			if(index >= this.datalen) index -= this.datalen;

			return databuffer[channel][index];
		}

		public double getLatestElement(int channel){
			return getElementFromLast(channel, 0, getCurrentDataPointer());
		}

		public double getBufferMin(int channel){
			double min;
			double val;
			min = databuffer[channel][0];
			for(int i = 0; i<datalen; i++){
				val = databuffer[channel][i];
				if( !java.lang.Double.isNaN(val) ){
					if(val<min) min = val;
				}
			}
			return(min);
		}

		public double getBufferMin(){
			double min;
			double val;
			min = getBufferMin(0);
			for(int i = 0; i<noOfChannels; i++){
				val = getBufferMin(i);
				if( !java.lang.Double.isNaN(val) ){
					if(val<min) min = val;
				}
			}
			return(min);
		}

		public double getBufferMax(int channel){
			double max;
			double val;
			max = databuffer[channel][0];
			for(int i = 0; i<datalen; i++){
				val = databuffer[channel][i];
				if( !java.lang.Double.isNaN(val) ){
					if(val>max) max = val;
				}
			}
			return(max);
		}

		public double getBufferMax(){
			double max;
			double val;
			max = getBufferMax(0);
			for(int i = 0; i<noOfChannels; i++){
				val = getBufferMin(i);
				if( !java.lang.Double.isNaN(val) ){
					if(val>max) max = val;
				}
			}
			return(max);
		}


		public double getBufferAverage(int channel){
			double avg=0.0;
			double val;
			int cnt = 0;

			for(int i = 0; i<datalen; i++){
				val = databuffer[channel][i];
				if( !java.lang.Double.isNaN(val) ){
					cnt++;
					avg += val;
				}
			}

			return(avg / (double)cnt);
		}

	}

}
