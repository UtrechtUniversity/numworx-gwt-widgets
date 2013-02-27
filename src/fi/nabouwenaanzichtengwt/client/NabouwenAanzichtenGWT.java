package fi.nabouwenaanzichtengwt.client;

import java.util.ArrayList;
import java.util.HashMap;

import nl.uu.fi.dwo.interaction.client.InteractionView;
import nl.uu.fi.dwo.interaction.client.OpdrNavIF;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.BorderStyle;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;
import com.googlecode.mgwt.dom.client.event.touch.TouchCancelEvent;
import com.googlecode.mgwt.dom.client.event.touch.TouchEndEvent;
import com.googlecode.mgwt.dom.client.event.touch.TouchEndHandler;
import com.googlecode.mgwt.dom.client.event.touch.TouchHandler;
import com.googlecode.mgwt.dom.client.event.touch.TouchMoveEvent;
import com.googlecode.mgwt.dom.client.event.touch.TouchMoveHandler;
import com.googlecode.mgwt.dom.client.event.touch.TouchStartEvent;
import com.googlecode.mgwt.dom.client.event.touch.TouchStartHandler;
import com.googlecode.mgwt.ui.client.widget.touch.TouchPanel;

public class NabouwenAanzichtenGWT implements EntryPoint, InteractionView
{
	static final String holderId = "canvasholder";
	static final String upgradeMessage = "Your browser does not support the HTML5 Canvas. Please upgrade your browser to view this demo.";

	Canvas canvas;
	int mouseX, mouseY;
	OpdrNavIF comRoot;

	static final int refreshRate = 25;

	final CssColor redrawColor = CssColor.make("rgba(255,255,255,0.6)");

	private int breedte = 600;
	private int hoogte = 250;
	private HashMap<String, Object> launchState;
	String[] randomVarNamen = null;
	HashMap<String, Object> randomVarWaarden = null;

	FlowPanel panel = new FlowPanel();
	TouchPanel touchPanel = new TouchPanel();
	TouchButton nakijkKnop = new TouchButton();
	private Viewer3d vWerk = null;
	private VaktekPanel vaktekPanel = null;
	private NabouwenAanzichtenChecker naChecker;

	private int goedHalfFout;
	private int score = 0;
	private boolean correct = false;
	private String feedback = "";

	private boolean nagekeken;
	private boolean ingevuld;

	boolean kijkNaActief = false;

	private KubusRooster startKr;

	private Image vinkjeGroenImage, vinkjeGeelImage, vinkjeRoodImage,
			vinkjeGrijsImage, buttonBgImage;
	private NabouwenAanzichtenGWTCssResource nabouwenAanzichtenCss;

	public NabouwenAanzichtenGWT()
	{

	}

	public NabouwenAanzichtenGWT(HashMap<String, Object> h, String[] randomVarNamen, HashMap randomVarWaarden)
	{
		makeResources();

		this.randomVarNamen = randomVarNamen;
		this.randomVarWaarden = randomVarWaarden;
		if (h != null && h.get("breedte") != null)
			breedte = (Integer) h.get("breedte");
		if (h != null && h.get("hoogte") != null)
			hoogte = (Integer) h.get("hoogte");
		if (h != null && h.get("interactiePanelLaunchState") != null)
			launchState = (HashMap<String, Object>) h.get("interactiePanelLaunchState");

		panel.getElement().getStyle().setWidth(breedte, Unit.PX);
		panel.getElement().getStyle().setHeight(hoogte, Unit.PX);
		panel.getElement().getStyle().setProperty("textAlign", "right");

		int maxAantal = 4;
		ArrayList<ArrayList<ArrayList<Boolean>>> stateNew = null;

		if (launchState.containsKey("stateNew"))
			stateNew = (ArrayList<ArrayList<ArrayList<Boolean>>>) launchState.get("stateNew");
		if (launchState.containsKey("maxAantal"))
			maxAantal = (Integer) launchState.get("maxAantal");

		boolean[][][] b = new boolean[maxAantal][maxAantal][maxAantal];
		for (int i = 0; i < maxAantal; i++)
		{
			for (int j = 0; j < maxAantal; j++)
			{
				for (int k = 0; k < maxAantal; k++)
				{
					b[i][j][k] = false;
				}
			}
		}
		if (stateNew != null)
		{
			b = new boolean[maxAantal][maxAantal][maxAantal];
			for (int i = 0; i < stateNew.size(); i++)
			{
				for (int j = 0; j < stateNew.get(i).size(); j++)
				{
					for (int k = 0; k < stateNew.get(i).get(j).size(); k++)
					{
						b[i][j][k] = (Boolean) stateNew.get(i).get(j).get(k);
					}
				}
			}

		}

		kijkNaActief = false;
		if (launchState.containsKey("kijkNaActief"))
			kijkNaActief = ((Boolean) launchState.get("kijkNaActief")).booleanValue();

		boolean drieAanzichten = false;
		if (launchState.containsKey("drieAanzichten"))
			drieAanzichten = ((Boolean) launchState.get("drieAanzichten")).booleanValue();
		boolean voorZijAanzicht = false;
		if (launchState.containsKey("voorZijAanzicht"))
			voorZijAanzicht = ((Boolean) launchState.get("voorZijAanzicht")).booleanValue();

		if (drieAanzichten)
		{
			startKr = new KubusRooster(b, 1.5);
			vaktekPanel = new VaktekPanel(startKr, breedte, hoogte, 3, this);
			panel.add(vaktekPanel.getPanel());
		}
		else if (voorZijAanzicht)
		{
			startKr = new KubusRooster(b, 1.5);
			vaktekPanel = new VaktekPanel(startKr, breedte, hoogte, 2, this);
			panel.add(vaktekPanel.getPanel());
		}
		else
		{
			startKr = new KubusRooster(b, 1);

			vWerk = new Viewer3d(new KubusRooster(b, 1), 351, -30, breedte, hoogte - (kijkNaActief ? 30 : 0), this);
			vWerk.zetAfstand(1000);
			vWerk.zetSchaduw(true);
			vWerk.zetBeginHoeken(30, -30);
			vWerk.zetMuisAan(true);

			boolean rotatieVast = false;
			if (launchState.containsKey("rotatieVast"))
				rotatieVast = ((Boolean) launchState.get("rotatieVast")).booleanValue();
			vWerk.zetMuisAan(!rotatieVast);

			double beginHoekX = 30;
			double beginHoekY = -30;
			if (launchState.containsKey("beginHoekX"))
				beginHoekX = ((Double) launchState.get("beginHoekX")).doubleValue();
			if (launchState.containsKey("beginHoekY"))
				beginHoekY = ((Double) launchState.get("beginHoekY")).doubleValue();
			vWerk.zetBeginHoeken(beginHoekX, beginHoekY);

			boolean nietBouwenSlopen = false;
			if (launchState.containsKey("nietBouwenSlopen"))
				nietBouwenSlopen = ((Boolean) launchState.get("nietBouwenSlopen")).booleanValue();
			vWerk.zetKlikAan(!nietBouwenSlopen);

			vWerk.initContext2d();

			vWerk.draw();

			canvas = vWerk.getCanvas();

			touchPanel.getElement().getStyle().setWidth(breedte, Unit.PX);
			touchPanel.getElement().getStyle().setHeight(hoogte - (kijkNaActief ? 30 : 0), Unit.PX);
			touchPanel.add(canvas);
			panel.add(touchPanel);

			if (kijkNaActief)
			{
				naChecker = new NabouwenAanzichtenChecker(launchState, randomVarNamen, randomVarWaarden);

				Image image = new Image(clientBundle.vinkjegrijs());
				nakijkKnop.add(image);
				addCheckButtonHandler(nakijkKnop);
				//nakijkKnop.getElement().getStyle().setProperty("textAlign", "right");
				//nakijkKnop.getElement().getStyle().setProperty("textAlign", "right");
				nakijkKnop.getElement().getStyle().setBackgroundImage("url(images/resources/footerbgimage.png)");
				nakijkKnop.getElement().getStyle().setBorderColor("gray");
				nakijkKnop.getElement().getStyle().setBorderColor("gray");
				nakijkKnop.getElement().getStyle().setBorderStyle(BorderStyle.SOLID);
				nakijkKnop.getElement().getStyle().setBorderWidth(1, Unit.PX);
				nakijkKnop.getElement().getStyle().setProperty("display", "inline-block");
				nakijkKnop.getElement().getStyle().setPaddingTop(3, Unit.PX);
				nakijkKnop.getElement().getStyle().setPaddingBottom(3, Unit.PX);
				nakijkKnop.getElement().getStyle().setPaddingLeft(10, Unit.PX);
				nakijkKnop.getElement().getStyle().setPaddingRight(10, Unit.PX);
				nakijkKnop.getElement().getStyle().setProperty("borderRadius", "5px");
				panel.add(nakijkKnop);
			}

			MuisBeheerder mb = new MuisBeheerder(vWerk);

			touchPanel.addTouchStartHandler((TouchStartHandler) mb);
			touchPanel.addTouchEndHandler((TouchEndHandler) mb);
			touchPanel.addTouchMoveHandler((TouchMoveHandler) mb);
		}
	}

	NabouwenAanzichtenGWTClientBundle clientBundle = GWT.create(NabouwenAanzichtenGWTClientBundle.class);

	public void makeResources()
	{
		nabouwenAanzichtenCss = clientBundle.getNabouwenAanzichtenGWTCSS();
		nabouwenAanzichtenCss.ensureInjected();

		vinkjeGroenImage = new Image(clientBundle.vinkje());
		vinkjeGeelImage = new Image(clientBundle.vinkjegeel());
		vinkjeRoodImage = new Image(clientBundle.vinkjerood());
		vinkjeGrijsImage = new Image(clientBundle.vinkjegrijs());
		buttonBgImage = new Image(clientBundle.footerbgimage());
	}

	public void onModuleLoad()
	{
		makeResources();
		int maxAantal = 6;
		boolean[][][] b = new boolean[maxAantal][maxAantal][maxAantal];
		for (int i = 0; i < maxAantal; i++)
		{
			for (int j = 0; j < maxAantal; j++)
			{
				for (int k = 0; k < maxAantal; k++)
				{
					if (k == 0 && j == 0 && i == 0)
						;
					b[i][j][k] = false;
				}
			}
		}
		Viewer3d vWerk = new Viewer3d(new KubusRooster(b, 1), 351, -30, 450, 450, this);
		//vWerk.zetAchtergrond(bgcolor);
		vWerk.zetAfstand(1000);
		vWerk.zetSchaduw(true);
		vWerk.zetBeginHoeken(30, -30);
		vWerk.zetMuisAan(true);
		//vWerk.zetGetalRooster(true);

		canvas = vWerk.getCanvas();
		RootPanel rootPanel = RootPanel.get(holderId);
		if (canvas == null)
		{
			if (rootPanel != null)
				rootPanel.add(new Label(upgradeMessage));
			return;
		}

		vWerk.initContext2d();

		vWerk.draw();

		touchPanel.getElement().getStyle().setWidth(breedte, Unit.PX);
		touchPanel.getElement().getStyle().setHeight(hoogte - (kijkNaActief ? 30 : 0), Unit.PX);
		touchPanel.add(canvas);
		panel.add(touchPanel);

		if (rootPanel != null)
			rootPanel.add(touchPanel);

		MuisBeheerder mb = new MuisBeheerder(vWerk);

		touchPanel.addTouchStartHandler((TouchStartHandler) mb);
		touchPanel.addTouchEndHandler((TouchEndHandler) mb);
		touchPanel.addTouchMoveHandler((TouchMoveHandler) mb);
	}

	private void addCheckButtonHandler(final TouchButton tb)
	{
		tb.addTouchHandler(new TouchHandler()
		{
			@Override
			public void onTouchStart(TouchStartEvent event)
			{
				check();
			}

			@Override
			public void onTouchMove(TouchMoveEvent event)
			{
			}

			@Override
			public void onTouchEnd(TouchEndEvent event)
			{
			}

			@Override
			public void onTouchCanceled(TouchCancelEvent event)
			{
			}
		});
	}

	private void check()
	{
		if (vWerk == null || !ingevuld || !kijkNaActief)
			return;
		KubusRooster useranswer = vWerk.kr;
		HashMap<String, Object> checkResults = naChecker.checkAnswer(useranswer);

		this.correct = (Boolean) checkResults.get("correct");
		this.score = (Integer) checkResults.get("score");
		this.feedback = (String) checkResults.get("feedback");
		this.goedHalfFout = (Integer) checkResults.get("goedHalfFout");

		//System.out.println("userAnswer: "+useranswer);
		//System.out.println("correct: "+correct);
		//System.out.println("score: "+score);
		//System.out.println("goedHalfFout: "+goedHalfFout);
		//System.out.println(" feedback: "+ feedback);

		if (goedHalfFout == NabouwenAanzichtenChecker.DOOR || goedHalfFout == NabouwenAanzichtenChecker.HALF)
		{
			nakijkKnop.clear();
			nakijkKnop.add(new Image("images/resources/vinkjegeel.png"));
		}

		else if (goedHalfFout == NabouwenAanzichtenChecker.GOED)
		{
			nakijkKnop.clear();
			nakijkKnop.add(new Image("images/resources/vinkje.png"));
		}
		else if (goedHalfFout == NabouwenAanzichtenChecker.FOUT)
		{
			nakijkKnop.clear();
			nakijkKnop.add(new Image("images/resources/vinkjerood.png"));
		}
		nagekeken = true;
		comRoot.setChanged();

	}

	public Panel getAsPanel()
	{
		return panel;
	}

	void zetVeranderd()
	{
		if (vWerk == null || !kijkNaActief)
			return;
		nakijkKnop.clear();
		nakijkKnop.add(new Image(clientBundle.vinkjegrijs()));
		correct = false;
		score = 0;
		if (!startKr.isGelijk(vWerk.kr))
			ingevuld = true;
		else
			ingevuld = false;
		comRoot.setChanged();
	}

	boolean isBouwen()
	{
		return true;
	}

	@Override
	public HashMap<String, Object> getState()
	{
		HashMap<String, Object> h = new HashMap<String, Object>();
		if (vWerk == null)
			return h;

		check();
		boolean[][][] stateNew = null;
		stateNew = vWerk.kr.geefBooleanRooster();

		h.put("stateNew", stateNew);
		h.put("nagekeken", nagekeken);
		h.put("ingevuld", ingevuld);

		return h;
	}

	@Override
	public void setState(HashMap<String, Object> h)
	{
		if (vWerk == null || h == null)
			return;

		Object stateNew = null;

		if (h.containsKey("nagekeken"))
			nagekeken = (Boolean) h.get("nagekeken");
		if (h.containsKey("ingevuld"))
			ingevuld = (Boolean) h.get("ingevuld");
		if (h.containsKey("stateNew"))
			stateNew = h.get("stateNew");
		if (stateNew != null)
		{
			if (stateNew instanceof boolean[][][])
				vWerk.zetKubusRooster(new KubusRooster((boolean[][][]) stateNew, 1));
			else if (stateNew instanceof Object[])
				vWerk.zetKubusRooster(new KubusRooster(KubusRooster.toBooleanArray((Object[]) stateNew), 1));
			vWerk.draw();
		}
		if (nagekeken)
			check();
	}

	@Override
	public int getScore()
	{
		// TODO Auto-generated method stub
		return score;
	}

	@Override
	public boolean isCorrect()
	{
		if (!kijkNaActief)
			return true;
		return correct;
	}

	@Override
	public void setCommunicationRoot(OpdrNavIF comRoot)
	{
		this.comRoot = comRoot;

	}

	@Override
	public Widget asWidget()
	{
		return getAsPanel();
	}
}
