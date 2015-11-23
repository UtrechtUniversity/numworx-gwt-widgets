package fi.statsimgwt.client;

import com.google.gwt.canvas.dom.client.CssColor;

public class BinomRooster {
	CssColor lightgray = CssColor.make(130, 130, 130);
	CssColor black = CssColor.make(0, 0, 0);
	
	BinomTrekking ssg;
	
	public BinomRooster (BinomTrekking binomTrekking) {
		this.ssg = binomTrekking;
	}

	public void paint() {
		
		ssg.gIm.beginPath();
		
		int height=340;
		int width=200;

		ssg.gIm.setLineWidth(1);
		ssg.gIm.setFillStyle(ssg.agKleur);
		ssg.gIm.fillRect(0,0,width,height);
		ssg.gIm.setFillStyle(black);
		
		int a=height-20;
		int d=0;
		a=width-20;
		d=height-a-20;
		
		
		int b=ssg.maxCount;
		double c=a/ssg.maxCount;
		ssg.gIm.setStrokeStyle(lightgray);
		for (int i=0;i<ssg.maxCount;i++) {

			ssg.gIm.moveTo((int)(10+i*c), 10+a+d);
			ssg.gIm.lineTo((int)(10+i*c), (int)(10+a-b*c)+d);
			
			ssg.gIm.moveTo(10,10+a-(int)(i*c)+d);
			ssg.gIm.lineTo(10+(int)(b*c),10+a-(int)(i*c)+d);
			b--;
		}
		b=ssg.maxCount;
		for (int i=0;i<ssg.maxCount;i++) {
			for (int j=0;j<b;j++) {
				ssg.gIm.fillRect(10+(int)(c*j)-1, 10+a-(int)(c*i)-1+d, 3, 3);
			}
			b--;
		}
		double x=0;
		double y=a;
		double x1=0;
		double y1=0;
		
		ssg.gIm.stroke();
		ssg.gIm.beginPath();
		ssg.gIm.setStrokeStyle(black);
		ssg.gIm.setLineWidth(3);
		
		String s="";
		
		
		//return;
		
		for (int i=0;i<ssg.trekkingCount;i++) {
			if (ssg.trekkingenGeschiedenis[i]==true) {
				y1=y-c;
				x1=x;
				s=s+ "W";
			} else {
				y1=y;
				x1=x+c;
				s=s+"N";
			}
			ssg.gIm.moveTo((int)x+10, (int)y+10+d);
			ssg.gIm.lineTo((int)x1+10, (int)y1+10+d);
			
			x=x1;
			y=y1;
		}
		ssg.gIm.fillText(s, a/3+10, a/3-10+d);
		ssg.gIm.stroke();
	}
}
