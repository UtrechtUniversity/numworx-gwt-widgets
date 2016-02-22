package fi.statsimgwt.client;

import java.awt.Color;

import com.google.gwt.canvas.dom.client.CssColor;

public class BinomGrafiek {
	BinomTrekking ssg;
	int[] binomVerdeling;
	
	CssColor red = CssColor.make(255, 0, 0);
	CssColor black = CssColor.make(0, 0, 0);
	
	int breedte=360;
	int hoogte=340;
	
	public BinomGrafiek (BinomTrekking binomTrekking) {
		this.ssg = binomTrekking;
	}
	
	public void setGrootte (int breedte, int hoogte) {
		this.breedte=breedte;
		this.hoogte=hoogte;
		paint();
	}
	
	public void paint() {
		int width=breedte;
		int height=hoogte;
		
		ssg.gIm2.setFillStyle(ssg.agKleur);
		ssg.gIm2.fillRect(0,0,width,height);
		
		ssg.gIm2.beginPath();
		
		binomVerdeling = new int[ssg.maxCount+1];
		for (int i=0;i<ssg.experiment;i++) {
			binomVerdeling[ssg.trekkingen[i]]++;
		}
		int maxHeight=0;
		for (int i=0;i<ssg.maxCount+1;i++) {
			if (binomVerdeling[i]>maxHeight) {
				maxHeight=binomVerdeling[i];
			}
		}
		if (maxHeight<10) maxHeight=10;
	
		int d=maxHeight;
		int e=0;
		int f=0;
		while (true) {
			if (d/(1*Math.pow(10, e))<7) {
				f =1*(int)Math.pow(10, e);
				break;
			}
			if (d/(2*Math.pow(10, e))<7) {
				f =2*(int)Math.pow(10, e);
				break;
			}
			if (d/(3*Math.pow(10, e))<7) {
				f =3*(int)Math.pow(10, e);
				break;
			}
			if (d/(5*Math.pow(10, e))<7) {
				f =5*(int)Math.pow(10, e);
				break;
			}
			//if (d/(10*Math.pow(10, e))<7) {
			//	f =10*(int)Math.pow(10, e);
			//	break;
			//}
			e++;
		}
		//System.out.println(f);
		int numMarks1=d/f;
		
		for (int i=0;i<=numMarks1;i++) {
			if (maxHeight>0) {
				ssg.gIm2.moveTo(45,height-20-(i*f*(height-50)/d));
				ssg.gIm2.lineTo(50,height-20-(i*f*(height-50)/d));
				ssg.gIm2.fillText(i*f+"", 25,height-20-(i*f*(height-50)/d));
			}
		}
		
		for (int i=0;i<ssg.maxCount+1;i++) {
			if (maxHeight>0) {
				ssg.gIm2.setFillStyle(red);
				ssg.gIm2.fillRect(50+(i*(width-60)/(ssg.maxCount+1)), (maxHeight-binomVerdeling[i])*(height-50)/maxHeight+30, ((width-60)/(ssg.maxCount+1)), (binomVerdeling[i])*(height-50)/maxHeight);
				ssg.gIm2.setFillStyle(black);
				ssg.gIm2.strokeRect(50+(i*(width-60)/(ssg.maxCount+1)), (maxHeight-binomVerdeling[i])*(height-50)/maxHeight+30, ((width-60)/(ssg.maxCount+1)), (binomVerdeling[i])*(height-50)/maxHeight);
			}
		}
		
		ssg.gIm2.moveTo(50,30);
		ssg.gIm2.lineTo(50,height-20);
		
		ssg.gIm2.moveTo(50,height-20);
		ssg.gIm2.lineTo(width-10,height-20);
		
		int a=ssg.maxCount+1;
		int b=0;
		int c=0;
		while (true) {
			if (a/(2*Math.pow(10, b))<7) {
				c =2*(int)Math.pow(10, b);
				break;
			}
			if (a/(3*Math.pow(10, b))<7) {
				c =3*(int)Math.pow(10, b);
				break;
			}
			if (a/(5*Math.pow(10, b))<7) {
				c =5*(int)Math.pow(10, b);
				break;
			}
			if (a/(10*Math.pow(10, b))<7) {
				c =10*(int)Math.pow(10, b);
				break;
			}
			b++;
		}
		System.out.println(c);
		int numMarks=a/c;
		for (int i=0;i<=numMarks;i++) {
			ssg.gIm2.moveTo((width-60)*(i*c)/(a)+50, height-20);
			ssg.gIm2.lineTo((width-60)*(i*c)/(a)+50, height-15);
			
			String s=(i*c)+"";
			ssg.gIm2.fillText(s, (width-60)*(i*c)/(a)+45, height);
		}
		ssg.gIm2.stroke();
	}
}
