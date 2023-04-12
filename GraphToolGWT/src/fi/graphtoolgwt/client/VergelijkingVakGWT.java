/*
package fi.graphtoolgwt.client;

import fi.graphtoolgwt.client.ui.formuleholder.FormuleEditor;
import fi.graphtoolgwt.client.ui.formuleobjects.FormuleElement;
import fi.graphtoolgwt.client.ui.formuleobjects.FormuleRegel;
import fi.graphtoolgwt.client.ui.views.interactionviews.FormuleEditorWithAnswer;
import fi.wiskopdr.formuleobjects.FormuleVak;

public class VergelijkingVakGWT extends FormuleRegel{

	FormuleElement formuleVak;
	FormuleElement functieBeginVak; 
	//FormuleVak formuleVak;
	//FormuleVak functieBeginVak;
	private boolean functieBeginAanpasbaar;
	private FormuleEditorWithAnswer owner;


	public VergelijkingVakGWT(boolean functieBeginAanpasbaar)
	{
		FormuleRegel vak = new FormuleRegel(owner);
		
		this.functieBeginAanpasbaar = functieBeginAanpasbaar;
		
		functieBeginVak = new FormuleElement();
		//functieBeginVak.setLocation(0, 0);
		//functieBeginVak.setEditable(false);
		if(!functieBeginAanpasbaar)
			vak.insert(functieBeginVak);
		
		formuleVak = new FormuleVak();
		if(functieBeginAanpasbaar)
			formuleVak.setLocation(0, 0);
		else
			formuleVak.setLocation(functieBeginVak.getWidth(), 0);
		add(formuleVak);
	}
	
	public void zetMaat()
	{	int b = formuleVak.getSize().width;
		
		if(!functieBeginAanpasbaar)
		{	b = formuleVak.getSize().width + functieBeginVak.getSize().width;
		}
		
		int h1 = formuleVak.ashoogte;
		if(functieBeginVak.ashoogte > h1) h1 = functieBeginVak.ashoogte;
		int h2 = formuleVak.getSize().height - formuleVak.ashoogte;
		if(functieBeginVak.getSize().height - functieBeginVak.ashoogte > h2) h2 = functieBeginVak.getSize().height - functieBeginVak.ashoogte;
		
		setSize(b, h1+h2);
		ashoogte = h1;
		
		if(functieBeginAanpasbaar)
			formuleVak.setLocation(0,ashoogte-formuleVak.ashoogte);
		else
		{	functieBeginVak.setLocation(0, ashoogte - functieBeginVak.ashoogte);
			formuleVak.setLocation(functieBeginVak.getSize().width, ashoogte - formuleVak.ashoogte);
			
		}
		
	}
	
}
*/
