package fi.geomalggwt.client;

//import java.io.Serializable; 

public class State //implements Serializable
{
	private Figuur[] figurenrij;
	private int aantalFg;
	private int varx,vary,varz;
	
	public State(int aantal, Figuur[] fg, int[] var)
	{	aantalFg = aantal;
		figurenrij = new Figuur[aantalFg];
		for(int i=0 ; i<aantalFg ; i++)
		{	figurenrij[i] = fg[i].dupliceer();
		}
		varx = var[1];
		vary = var[2];
		varz = var[3];
	}
	
	public Figuur[] geefFigurenRij()
	{	Figuur[] fn = new Figuur[200];
		for(int i=0 ; i<aantalFg ; i++)
		{	fn[i] = figurenrij[i].dupliceer();
		}
		return fn;
	}
	
	public int  geefAantalFiguren()
	{	
		return aantalFg;
	}
	
	public int[]  geefVars()
	{	int[] var = new int[4];
		var[1] = varx;
		var[2] = vary;
		var[3] = varz;
		return var;
	}
}

class Buffer
{
	private State[] states;
	private int aantalStates;
	private int maxAantalStates;
	
	public Buffer(int max)
	{	maxAantalStates = max;
		states = new State[max+1];
		aantalStates = 0;
	}
	
	public void voegToe(State st)
	{	states[aantalStates] = st;
		aantalStates++;
		if(aantalStates > maxAantalStates)
		{	for(int i=0 ; i<aantalStates-1 ; i++)
			{	states[i] = states[i+1];
			}
			aantalStates--;
		}
	}
	
	public State geefVorigeState()
	{	if(aantalStates == 1)return null;
		aantalStates--;
		return states[aantalStates-1];
	}
	
	public State geefHuidigeState()
	{	if(aantalStates == 0)return null;
		return states[aantalStates-1];
	}
	
	public int geefAantalStates()
	{	return aantalStates;
	}
}
		
