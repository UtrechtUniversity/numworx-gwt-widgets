package fi.statsimgwt.client;

import java.awt.Color;
import com.google.gwt.canvas.dom.client.CssColor;


public class DobbelstenenGrafiek {
	Dobbelstenen ssg;
	
	CssColor red = CssColor.make(255, 0, 0);
		
	public DobbelstenenGrafiek (Dobbelstenen dobbelstenen) {
		this.ssg=dobbelstenen;
	}
	
	public void paint() {
		ssg.gIm.setFillStyle(ssg.agKleur);
		ssg.gIm.fillRect(0,0,560,350);
		ssg.gIm.setLineWidth(1.0d);
		ssg.gIm.setStrokeStyle(ssg.lijnenKleur);
		ssg.gIm.setFillStyle(ssg.lijnenKleur);
		
		ssg.gIm.beginPath();

		int height=350;
		if (ssg.toonSom.getValue()==true) {
			height=175;
		}
		int width=560;

		ssg.gIm.moveTo(50,20);
		ssg.gIm.lineTo(50,height-30);
		ssg.gIm.moveTo(50,height-30);
		ssg.gIm.lineTo(width-10,height-30);

		
		int a=0;
		a=ssg.maxCount/2;
		
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
		//System.out.println(c);
		int numMarks=a/c;
		for (int i=0;i<=numMarks;i++) {
			ssg.gIm.moveTo(50,20);
			ssg.gIm.lineTo(50,height-30);
			ssg.gIm.moveTo(45,(height-40)-(height-50)*(i*c)/(a)+10);
			ssg.gIm.lineTo(50, (height-40)-(height-50)*(i*c)/(a)+10);
				
			String s=(i*c)+"";
			ssg.gIm.fillText(s, 25,(height-40)-(height-50)*(i*c)/(a)+15);
		}
		int numBars=0;
		int skipNum=0;
		String ogenString="";
		if (ssg.eenDobbelsteen.getValue()) {
			numBars=6;
			ogenString="Ogen";
		}
		if (ssg.tweeDobbelstenen.getValue()) {
			numBars=11;
			skipNum=1;
			ogenString="Som ogen";
		}
		if (ssg.drieDobbelstenen.getValue()) {
			numBars=16;
			skipNum=2;
			ogenString="Som ogen";
		}
		
		ssg.gIm.fillText(ogenString, 5, height-10+height);
		ssg.gIm.fillText("Frequentie", 5, 10+height);
		for (int i=0;i<numBars;i++) {
			ssg.gIm.setFillStyle(red);
			ssg.gIm.fillRect(50+i*(width-60)/numBars+5, 20+(a-ssg.ogen[i+1+skipNum])*(height-50)/a, (width-60)/numBars-10, ssg.ogen[i+1+skipNum]*(height-50)/a);
			ssg.gIm.setFillStyle(ssg.lijnenKleur);
			ssg.gIm.strokeRect(50+i*(width-60)/numBars+5, 20+(a-ssg.ogen[i+1+skipNum])*(height-50)/a, (width-60)/numBars-10, ssg.ogen[i+1+skipNum]*(height-50)/a);				
			
			ssg.gIm.moveTo(50+i*(width-60)/numBars+(width-60)/(numBars*2),height-30);
			ssg.gIm.lineTo(50+i*(width-60)/numBars+(width-60)/(numBars*2),height-25);
			
			ssg.gIm.fillText(i+1+skipNum+"",50+i*(width-60)/numBars+(width-60)/(numBars*2), height-10);
			ssg.gIm.fillText(ssg.ogen[i+1+skipNum]+"",50+i*(width-60)/numBars+(width-60)/(numBars*2), (a-ssg.ogen[i+1+skipNum])*(height-50)/a+10);
		}
		ssg.gIm.stroke();

		if (ssg.toonSom.getValue()==true) {
			ssg.gIm.beginPath();
	
			ssg.gIm.moveTo(50,20+height);
			ssg.gIm.lineTo(50,height-30+height);
			ssg.gIm.moveTo(50,height-30+height);
			ssg.gIm.lineTo(width-10,height-30+height);
	
			
			a=0;
			for (int i=0;i<19;i++) {
				if (ssg.ogenSom[i]>a)
					a=ssg.ogenSom[i];
			}
			if (a==0)
				a=1;
			
			b=0;
			c=0;
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
			//System.out.println(c);
			numMarks=a/c;
			for (int i=0;i<=numMarks;i++) {
				ssg.gIm.moveTo(50,20+height);
				ssg.gIm.lineTo(50,height-30+height);
				ssg.gIm.moveTo(45,(height-40)-(height-50)*(i*c)/(a)+10+height);
				ssg.gIm.lineTo(50, (height-40)-(height-50)*(i*c)/(a)+10+height);
					
				String s=(i*c)+"";
				ssg.gIm.fillText(s, 25,(height-40)-(height-50)*(i*c)/(a)+15+height);
			}
			numBars=0;
			skipNum=0;
			ogenString="";
			if (ssg.eenDobbelsteen.getValue()) {
				numBars=6;
				ogenString="Ogen";
			}
			if (ssg.tweeDobbelstenen.getValue()) {
				numBars=11;
				skipNum=1;
				ogenString="Som ogen";
			}
			if (ssg.drieDobbelstenen.getValue()) {
				numBars=16;
				skipNum=2;
				ogenString="Som ogen";
			}
			
			ssg.gIm.fillText(ogenString, 5, height-10);
			ssg.gIm.fillText("Frequentie", 5, 10);
			for (int i=0;i<numBars;i++) {
				ssg.gIm.setFillStyle(red);
				ssg.gIm.fillRect(50+i*(width-60)/numBars+5, 20+(a-ssg.ogenSom[i+1+skipNum])*(height-50)/a+height, (width-60)/numBars-10, ssg.ogenSom[i+1+skipNum]*(height-50)/a);
				ssg.gIm.setFillStyle(ssg.lijnenKleur);
				ssg.gIm.strokeRect(50+i*(width-60)/numBars+5, 20+(a-ssg.ogenSom[i+1+skipNum])*(height-50)/a+height, (width-60)/numBars-10, ssg.ogenSom[i+1+skipNum]*(height-50)/a);
				
				ssg.gIm.moveTo(50+i*(width-60)/numBars+(width-60)/(numBars*2),height-30+height);
				ssg.gIm.lineTo(50+i*(width-60)/numBars+(width-60)/(numBars*2),height-25+height);
				
				ssg.gIm.fillText(i+1+skipNum+"",50+i*(width-60)/numBars+(width-60)/(numBars*2), height-10+height);
				ssg.gIm.fillText(ssg.ogenSom[i+1+skipNum]+"",50+i*(width-60)/numBars+(width-60)/(numBars*2), (a-ssg.ogenSom[i+1+skipNum])*(height-50)/a+10+height);
			}
			ssg.gIm.stroke();
		}
		
	}
}
