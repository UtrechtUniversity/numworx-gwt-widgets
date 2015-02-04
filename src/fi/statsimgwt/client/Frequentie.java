package fi.statsimgwt.client;

import com.google.gwt.canvas.dom.client.CssColor;

public class Frequentie {
	Munten ssg;

	CssColor yellow = CssColor.make(255, 255, 0);
	CssColor red = CssColor.make(255, 0, 0);
	
	public Frequentie(Munten munten) {
		this.ssg=munten;
	}
	
	public void paint() {
		ssg.gIm2.setFillStyle(ssg.agKleur);
		ssg.gIm2.fillRect(0,0,200,100);
		ssg.gIm2.setLineWidth(1.0d);
		ssg.gIm2.setStrokeStyle(ssg.lijnenKleur);
		ssg.gIm2.setFillStyle(ssg.lijnenKleur);
		
		ssg.gIm2.fillText ("Kop",90,90);
		ssg.gIm2.fillText ("Munt",150,90);
		
		ssg.gIm2.setFillStyle(yellow);
		ssg.gIm2.fillRect(65, 10+60-(int)60*(ssg.muntCount-ssg.totaalmunt)/(int)(ssg.maxCount*0.75), 60, (int)60*(ssg.muntCount-ssg.totaalmunt)/(int)(ssg.maxCount*0.75));
		ssg.gIm2.setFillStyle(ssg.lijnenKleur);
		ssg.gIm2.strokeRect(65, 10+60-(int)60*(ssg.muntCount-ssg.totaalmunt)/(int)(ssg.maxCount*0.75), 60, (int)60*(ssg.muntCount-ssg.totaalmunt)/(int)(ssg.maxCount*0.75));
		
		ssg.gIm2.setFillStyle(red);
		ssg.gIm2.fillRect(135, 10+60-60*(ssg.totaalmunt)/(int)(ssg.maxCount*0.75), 60, 60*ssg.totaalmunt/(int)(ssg.maxCount*0.75));
		ssg.gIm2.setFillStyle(ssg.lijnenKleur);
		ssg.gIm2.strokeRect(135, 10+60-60*(ssg.totaalmunt)/(int)(ssg.maxCount*0.75), 60, 60*ssg.totaalmunt/(int)(ssg.maxCount*0.75));
		
		ssg.gIm2.beginPath();
		ssg.gIm2.moveTo(55,10);
		ssg.gIm2.lineTo(55,70);
		ssg.gIm2.moveTo(50,10);
		ssg.gIm2.lineTo(55,10);
		ssg.gIm2.moveTo(50,70);
		ssg.gIm2.lineTo(55,70);
		ssg.gIm2.stroke();
		
		ssg.gIm2.fillText("0", 40, 75);
		ssg.gIm2.fillText((int)(ssg.maxCount*0.75)+ "",25,15);

	}
}
