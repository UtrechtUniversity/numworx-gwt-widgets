package fi.statsimgwt.client;

import com.google.gwt.canvas.dom.client.CssColor;

public class Grafiek {
	Munten ssg;
	
	CssColor yellow = CssColor.make(255, 255, 0);
	CssColor red = CssColor.make(255, 0, 0);
	CssColor green = CssColor.make(0, 255, 0);
	
	public Grafiek(Munten munten) {
		this.ssg=munten;
	}
	
	public void paint() {
		ssg.gIm.setFillStyle(ssg.agKleur);
		ssg.gIm.fillRect(0,0,560,340);
		ssg.gIm.setLineWidth(1.0d);
		ssg.gIm.setStrokeStyle(ssg.lijnenKleur);
		ssg.gIm.setFillStyle(ssg.lijnenKleur);
		
		ssg.gIm.beginPath();
	//	ssg.gIm.moveTo(0, 0);
	//	ssg.gIm.lineTo(100,100);

	//	ssg.gIm.moveTo(100, 0);
	//	ssg.gIm.lineTo(0,100);
		
		if (ssg.eenMunt.getValue()==true) {
			for (int i=1;i<ssg.muntCount;i++) {
				ssg.gIm.moveTo(i*(560-90)/ssg.maxCount+80, (int) (ssg.percentageMunt[i-1]*(340-50))+25);
				ssg.gIm.lineTo((i+1)*(560-90)/ssg.maxCount+80,(int) (ssg.percentageMunt[i]*(340-50))+25);
			}
			ssg.gIm.moveTo(80, 25);
			ssg.gIm.lineTo(80,340-25);
			ssg.gIm.moveTo(80, 340/2);
			ssg.gIm.lineTo(560-10,340/2);
			ssg.gIm.moveTo(80, 340-25);
			ssg.gIm.lineTo(560-10,340-25);
			for (int i=5;i>=0;i--) {
				ssg.gIm.fillText(i*20+"%",40,(5-i)*(340-50)/5+30);
				ssg.gIm.moveTo(75,(5-i)*(340-50)/5+25);
				ssg.gIm.lineTo(80,(5-i)*(340-50)/5+25);
			}
			int a=ssg.maxCount;
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
			int numMarks=a/c;
			for (int i=0;i<=numMarks;i++) {
				ssg.gIm.moveTo((560-90)*(i*c)/(a)+80, (340-25));
				ssg.gIm.lineTo((560-90)*(i*c)/(a)+80, (340-20));
				String s=(i*c)+"";
				ssg.gIm.fillText(s, (560-90)*(i*c)/(a)+70, (340-5));
			}
			/*Graphics2D g2 = (Graphics2D) g;
			AffineTransform orig = g2.getTransform();
			AffineTransform at = new AffineTransform();
			at.setToRotation(-Math.PI / 2.0, getWidth() / 2.0, getHeight() / 2.0);
			g2.setTransform(at);
			g2.setColor(Color.black);
			g2.drawString(StatSim.rb.getString("percentageHeads"),200,-90);
			g2.setTransform(orig);*/
		} else {
			ssg.gIm.moveTo(80,340-25);
			ssg.gIm.lineTo(560-60,340-25);
			ssg.gIm.moveTo(80, 25);
			ssg.gIm.lineTo(80, 340-25);
			ssg.gIm.moveTo(80+(560-90)/6,340-25);
			ssg.gIm.lineTo(80+(560-90)/6,340-20);
			ssg.gIm.fillText("0 kop",80+(560-90)/6-30,340-5);
			ssg.gIm.moveTo(80+ (560-90)*3/6, 340-25);
			ssg.gIm.lineTo(80+(560-90)*3/6,340-20);
			ssg.gIm.fillText("1 kop",80+(560-90)*3/6-25,340-5);
			ssg.gIm.moveTo(80+(560-90)*5/6, 340-25);
			ssg.gIm.lineTo(80+(560-90)*5/6, 340-20);
			ssg.gIm.fillText("2 kop",80+(560-90)*5/6-30,340-5);
			
			int a;			
			if (Double.parseDouble(ssg.kansOpKopText.getText())>0.75 || Double.parseDouble(ssg.kansOpKopText.getText())<0.25)
				a=ssg.maxCount;
			else
				a=ssg.maxCount*3/4;
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
			int numMarks=a/c;
			for (int i=0;i<=numMarks;i++) {
				ssg.gIm.moveTo(75,(340-30)-(340-50)*(i*c)/(a)+5);
				ssg.gIm.lineTo(80, (340-30)-(340-50)*(i*c)/(a)+5);
				String s=(i*c)+"";
				ssg.gIm.fillText(s, 50,(340-30)-(340-50)*(i*c)/(a)+10);
			}
			/*Graphics2D g2 = (Graphics2D) g;
			AffineTransform orig = g2.getTransform();
			AffineTransform at = new AffineTransform();
			at.setToRotation(-Math.PI / 2.0, getWidth() / 2.0, getHeight() / 2.0);
			g2.setTransform(at);
			g2.setColor(Color.black);
			g2.drawString(StatSim.rb.getString("frequency"),200,-70);
			g2.setTransform(orig);*/
			
			ssg.gIm.setFillStyle(red);
			ssg.gIm.fillRect(85, 5+(340-30)-ssg.geenKop*(340-50)/a, (560-90)/3-10, ssg.geenKop*(340-50)/a);
			ssg.gIm.setFillStyle(ssg.lijnenKleur);
			ssg.gIm.strokeRect(85, 5+(340-30)-ssg.geenKop*(340-50)/a, (560-90)/3-10, ssg.geenKop*(340-50)/a);
			ssg.gIm.setFillStyle(yellow);
			ssg.gIm.fillRect(85+(560-90)*1/3, 5+(340-30)-ssg.eenKop*(340-50)/a, (560-90)/3-10, ssg.eenKop*(340-50)/a);
			ssg.gIm.setFillStyle(ssg.lijnenKleur);
			ssg.gIm.strokeRect(85+(560-90)*1/3, 5+(340-30)-ssg.eenKop*(340-50)/a, (560-90)/3-10, ssg.eenKop*(340-50)/a);
			ssg.gIm.setFillStyle(green);
			ssg.gIm.fillRect(85+(560-90)*2/3, 5+(340-30)-ssg.tweeKop*(340-50)/a, (560-90)/3-10, ssg.tweeKop*(340-50)/a);
			ssg.gIm.setFillStyle(ssg.lijnenKleur);
			ssg.gIm.strokeRect(85+(560-90)*2/3, 5+(340-30)-ssg.tweeKop*(340-50)/a, (560-90)/3-10, ssg.tweeKop*(340-50)/a);
		}
		ssg.gIm.stroke();
	}
	
}
