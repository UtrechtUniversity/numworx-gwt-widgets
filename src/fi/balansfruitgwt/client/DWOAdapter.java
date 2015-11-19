package fi.balansfruitgwt.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fi.balansfruitgwt.client.DraggableObjectFactory.Objects;

/**
 * This class called the DWOAdapter read data and write data to the DWO player
 * 
 * @author casperkolkman, Sylvia van Borkulo
 * @version 1.0
 * @since 13-12-2012
 */
@SuppressWarnings(
{ "unchecked", "rawtypes" })
public class DWOAdapter
{

	/**
	 * All static final variables are decided by Freudenthal institute
	 */
	private static final int idLeftContainer = 1;
	private static final int idRightContainer = 2;
	private static final int idStockContainer = 0;

	static final int idOne = 10;
	private static final int idOneGram = 11;
	private static final int idFiveGrams = 12;
	private static final int idTenGrams = 13;
	private static final int idFiftyGrams = 14;
	private static final int idHunderdGrams = 15;
	private static final int idFiveHunderdGrams = 16;
	private static final int idPineApple = 0;
	private static final int idApple = 1;
	private static final int idBanana = 2;
	private static final int idLemon = 3;
	private static final int idPear = 4;
	private static final int idPeach = 5;
	private static final int idOrange = 6;
	private static final int idTomato = 7;
	static final int idX = 8;
	private static final int idY = 9;
	static final int idOneEmpty = 17;
	private static final int idOneBlockAbstract = 18;
	private static final int idTwoBlockAbstract = 19;
	private static final int idFiveBlockAbstract = 20;
	private static final int idTenBlockAbstract = 21;
	private static final int idTwentyBlockAbstract = 22;
	private static final int idFiftyBlockAbstract = 23;
	private static final int idHundredBlockAbstract = 24;

	private static final String keyFixed = "fixedOptie";
	private static final String keyReset = "resetOptie";
	private static final String keySave = "bewaarOptie";
	static final String keyLaunchData = "interactiePanelLaunchState";
	private static final String keyAmountOfObject = "aantal";
	private static final String keyAmountOfAllObjects = "aantalFruitObjects";
	private static final String keyObjectsX = "stukFruitX";
	private static final String keyObjectsInContainer = "containerNr";
	private static final String keyWeight = "gewicht";

	private BalansFruitGWT balansFruitGWT;
	private DraggableObjectFactory factory;
	private DragDropPanel control;
	private Map<String,Object> launchData;

	Map<String, Object> getLaunchData() {
		return launchData;
	}


	void setLaunchData(Map<String, Object> launchData) {
		this.launchData = launchData;
	}

	private int weightOne = 0;
	private int weightOneGram = 0;
	private int weightFiveGrams = 0;
	private int weightTenGrams = 0;
	private int weightFiftyGrams = 0;
	private int weightHunderdGrams = 0;
	private int weightFiveHunderdGrams = 0;
	private int weightPineApple = 0;
	private int weightApple = 0;
	private int weightBanana = 0;
	private int weightLemon = 0;
	private int weightPear = 0;
	private int weightPeach = 0;
	private int weightOrange = 0;
	private int weightTomato = 0;
	private int weightX = 0;
	private int weightY = 0;
	private int weightOneEmpty = 0;
	private int weightOneBlockAbstract = 0;
	private int weightTwoBlockAbstract = 0;
	private int weightFiveBlockAbstract = 0;
	private int weightTenBlockAbstract = 0;
	private int weightTwentyBlockAbstract = 0;
	private int weightFiftyBlockAbstract = 0;
	private int weightHundredBlockAbstract = 0;

	public DWOAdapter(final BalansFruitGWT panel, final DraggableObjectFactory factory, final DragDropPanel control)
	{
		balansFruitGWT = panel;
		this.factory = factory;
		this.control = control;
	}


	public void readLaunchData(Map map) {
		launchData = map;
		readSaveMode();
		readResetMode();
		readFixedMode();
		control.reset();
		setObjects();
	}

	/**
	 * look for the fixed mode if fixed mode is true, objects aren't draggable
	 */
	private void readFixedMode()
	{
		if (launchData.containsKey(keyFixed))
		{
			factory.setFixed((Boolean) launchData.get(keyFixed));
		}
	}

	/**
	 * look for the reset mode if reset mode is true, there will be a reset
	 * button
	 */
	private void readResetMode()
	{
		if (launchData.containsKey(keyReset))
		{
			balansFruitGWT.setResetMode((Boolean) launchData.get(keyReset));
		}
	}

	/**
	 * look for the save mode if save mode is true, the player will save your
	 * changes
	 */
	private void readSaveMode()
	{
		if (launchData.containsKey(keySave))
		{
			balansFruitGWT.setSavedMode((Boolean) launchData.get(keySave));
		}
	}

	/**
	 * The player calls getState method by switching to an other exercise
	 * 
	 * @return data with the current state
	 * 
	 * @note The structure of this methode is the same in the old application
	 */
	public HashMap<String, Object> getState()
	{
		if (balansFruitGWT.getSaveMode())
		{
			int amountObjects = control.getLeftContainer().getWidgetCount() + control.getRightContainer().getWidgetCount() + control.getStockContainer().getWidgetCount();
			ArrayList<Integer> objectXPositions = new ArrayList();
			ArrayList<Integer> objectsInContainer = new ArrayList();
			ArrayList<Integer> objects = findObjects();
			int foundObjects = 0;
			for (int i = 0; i < objects.size(); i++)
			{
				//for (int j = 0; j < Integer.parseInt((String) launchData.get(keyAmountOfObject + objects.get(i))); j++)
				{
					findContainerAndX(control.getLeftContainer(), objectsInContainer, objectXPositions, objects.get(i), foundObjects);
					findContainerAndX(control.getRightContainer(), objectsInContainer, objectXPositions, objects.get(i), foundObjects);
					findContainerAndX(control.getStockContainer(), objectsInContainer, objectXPositions, objects.get(i), foundObjects);
					foundObjects++;
				}
			}
			return createSaveHashMap(amountObjects, objectXPositions, objectsInContainer);
		}
		else
		{
			return new HashMap();
		}
	}

	/**
	 * Create a HashMap to current state
	 * 
	 * @param amountObjects
	 * @param objectXPositions
	 * @param objectsInContainer
	 * @return hash with current state
	 * 
	 * @note Filling the hashmap is fixed by the old application
	 */
	private HashMap<String, Object> createSaveHashMap(int amountObjects, ArrayList<Integer> objectXPositions, ArrayList<Integer> objectsInContainer)
	{
		HashMap<String, Object> state = new HashMap();
		state.put(keyAmountOfAllObjects, amountObjects);
		state.put(keyObjectsX, objectXPositions.toArray());
		state.put(keyObjectsInContainer, objectsInContainer.toArray());
		return state;
	}

	/**
	 * find object in a container and get x position and the container
	 * 
	 * @param c
	 *            location to look for an object
	 * @param objectsInContainer
	 *            Array to fill with the container where object is placed
	 * @param objectXPositions
	 *            Array to fill with the x position of an object
	 * @param id
	 *            of the object that needs to be found
	 * @param index
	 */
	private void findContainerAndX(final Container c, final ArrayList<Integer> objectsInContainer, final ArrayList<Integer> objectXPositions, final Integer id, final int index_niet_gebruikt)
	{
		for (int i = 0; i < c.getWidgetCount(); i++)
		{
			if (c.getWidget(i) instanceof DraggableObject)
			{
				if (((DraggableObject) c.getWidget(i)).getId() == id)
				{
					if (c == control.getLeftContainer())
					{
						objectsInContainer.add(idLeftContainer);
					}
					else if (c == control.getRightContainer())
					{
						objectsInContainer.add(idRightContainer);
					}
					else
					{
						objectsInContainer.add(idStockContainer);
					}
					int x = ((DraggableObject) c.getWidget(i)).getAbsoluteLeft() - c.getAbsoluteLeft();
					objectXPositions.add(x);
				}
			}
		}
	}

	/**
	 * They player calls setState function after construct the balancePanel
	 * 
	 * @param state
	 *            current state
	 * 
	 * @note The structure of this methode is the same in the old application
	 */
	public void setState(final HashMap<String, Object> state)
	{
		if (!balansFruitGWT.getSaveMode())
		{
			return;
		}
		else
		{
			if (state.containsKey(keyAmountOfAllObjects) && state.containsKey(keyObjectsX) && state.containsKey(keyObjectsInContainer))
			{
				control.reset();
				ArrayList<Integer> objects = findObjects();
				int LatestObjectPlacedInContainer = 0;
				for (int i = 0; i < objects.size(); i++)
				{
					int amount = Integer.parseInt((String) launchData.get(keyAmountOfObject + objects.get(i)));
					for (int j = 0; j < amount; j++)
					{
						Container container = getContainer(state, LatestObjectPlacedInContainer);
						int x = getObjectX(state, LatestObjectPlacedInContainer);
						LatestObjectPlacedInContainer++;
						createObject(objects.get(i), container, x);
					}
				}
			}
			control.redrawBalance();
			control.setEquation(balansFruitGWT, false);
		}
	}

	/**
	 * Get a x position of an object out of a hashmap based on an index
	 * 
	 * @param launchData2
	 *            hashmap
	 * @param latestObjectPlacedInContainer
	 * @return x position
	 */
	private int getObjectX(final Map launchData2, final int latestObjectPlacedInContainer)
	{
		Object[] x = null;
		if (launchData2.containsKey(keyObjectsX))
		{
			x = toObjectArray(launchData2.get(keyObjectsX));
		}
		if (detectIncorrectXData(launchData2))
		{
			return -1;
		}
		else
		{
			return ((Number) x[latestObjectPlacedInContainer]).intValue();
		}
	}

	/**
	 * Find to objects that needs to be placed
	 * 
	 * @return Array with Id's of object that needs to be placed
	 */
	private ArrayList<Integer> findObjects()
	{
		ArrayList<Integer> objects = new ArrayList<Integer>();
		for (int i = 0; i <= idHundredBlockAbstract; i++)
		{
			if (launchData.get(keyAmountOfObject + i) != null && !(launchData.get(keyAmountOfObject + i).equals("0")))
			{
				objects.add(i);
			}
		}
		return objects;
	}

	/**
	 * Detects the bugs with too short x positon array from DWO
	 */
	private boolean detectIncorrectXData(final Map launchData2)
	{
		int totalAmount = ((Number) launchData2.get(keyAmountOfAllObjects)).intValue();
		int positionXLength = toObjectArray(launchData2.get(keyObjectsX)).length;
		if (positionXLength < totalAmount)
		{
			return true;
		}
		else
		{
			return false;
		}
	}

	/**
	 * Detects the bugs with too short container location array from DWO
	 * 
	 * @param launchData2
	 * @return true if data is incorrect
	 */
	private boolean detectIncorrectContainerData(final Map launchData2)
	{
		int totalAmount = ((Number) launchData2.get(keyAmountOfAllObjects)).intValue();
		int containerNrLength = toObjectArray(launchData2.get(keyObjectsInContainer)).length;
		if (containerNrLength < totalAmount)
		{
			return true;
		}
		else
		{
			return false;
		}
	}

	/**
	 * Get the location container out of data hashmap based on an index
	 * 
	 * @param launchData2
	 *            hashmap
	 * @param LastObjectPlacedInContainer
	 *            index
	 * @return the location container
	 */
	private Container getContainer(Map launchData2, final int LastObjectPlacedInContainer)
	{
		Object[] containerNr = toObjectArray(launchData2.get(keyObjectsInContainer));
		if (detectIncorrectContainerData(launchData2))
		{
			return control.getStockContainer();
		}
		else
		{
			if (((Number) containerNr[LastObjectPlacedInContainer]).intValue() == idRightContainer)
			{
				return control.getRightContainer();
			}
			else if (((Number) containerNr[LastObjectPlacedInContainer]).intValue() == idLeftContainer)
			{
				return control.getLeftContainer();
			}
			else
			{
				return control.getStockContainer();
			}
		}
	}

	/**
	 * Convert to Object[].
	 * 
	 * @param object
	 * @return
	 */
	private Object[] toObjectArray(Object object)
	{
		if (object instanceof List)
			return ((List<?>) object).toArray();

		return (Object[]) object;
	}

	/**
	 * reads data from the player and placed the objects
	 */
	private void setObjects()
	{
		int LatestObjectPlacedInContainer = 0;
		ArrayList<Integer> objects = findObjects();
		for (int indexId = 0; indexId < objects.size(); indexId++)
		{
			int weight = Integer.parseInt((String) launchData.get(keyWeight + objects.get(indexId)));
			int amount = Integer.parseInt((String) launchData.get(keyAmountOfObject + objects.get(indexId)));
			for (int j = 0; j < amount; j++)
			{
				Container container = getContainer(launchData, LatestObjectPlacedInContainer);
				int x = getObjectX(launchData, LatestObjectPlacedInContainer);
				LatestObjectPlacedInContainer++;
				setWeight(objects.get(indexId), weight);
				createObject(objects.get(indexId), container, x);
			}
		}
	}

	/**
	 * set the weight based on the id
	 * 
	 * @param id
	 *            of the object where the weight needs to be set
	 * @param weight
	 */
	private void setWeight(final int id, final int weight)
	{
		switch (id)
		{
		case idPineApple:
			weightPineApple = weight;
			break;
		case idApple:
			weightApple = weight;
			break;
		case idBanana:
			weightBanana = weight;
			break;
		case idLemon:
			weightLemon = weight;
			break;
		case idPear:
			weightPear = weight;
			break;
		case idPeach:
			weightPeach = weight;
			break;
		case idOrange:
			weightOrange = weight;
			break;
		case idTomato:
			weightTomato = weight;
			break;
		case idX:
			weightX = weight;
			break;
		case idY:
			weightY = weight;
			break;
		case idOne:
			weightOne = weight;
			break;
		case idOneGram:
			weightOneGram = weight;
			break;
		case idFiveGrams:
			weightFiveGrams = weight;
			break;
		case idTenGrams:
			weightTenGrams = weight;
			break;
		case idFiftyGrams:
			weightFiftyGrams = weight;
			break;
		case idHunderdGrams:
			weightHunderdGrams = weight;
			break;
		case idFiveHunderdGrams:
			weightFiveHunderdGrams = weight;
			break;
		case idOneEmpty:
			weightOneEmpty = weight;
			break;
		case idOneBlockAbstract:
			weightOneBlockAbstract = weight;
			break;
		case idTwoBlockAbstract:
			weightTwoBlockAbstract = weight;
			break;
		case idFiveBlockAbstract:
			weightFiveBlockAbstract = weight;
			break;
		case idTenBlockAbstract:
			weightTenBlockAbstract = weight;
			break;
		case idTwentyBlockAbstract:
			weightTwentyBlockAbstract = weight;
			break;
		case idFiftyBlockAbstract:
			weightFiftyBlockAbstract = weight;
			break;
		case idHundredBlockAbstract:
			weightHundredBlockAbstract = weight;
			break;
		default:
			break;
		}
	}

	/**
	 * This method let the factory create an object in a container on a position
	 * 
	 * @param id
	 *            the id of the object that needs to be create
	 * @param container
	 *            container where the object needs to be placed
	 * @param x
	 *            x-position where the object needs to be placed in a container
	 */
	private void createObject(final int id, final Container container, final int x)
	{
		switch (id)
		{
		case idPineApple:
			factory.createObjects(container, x, Objects.PINEAPPLE, weightPineApple, idPineApple);
			break;
		case idApple:
			factory.createObjects(container, x, Objects.APPLE, weightApple, idApple);
			break;
		case idBanana:
			factory.createObjects(container, x, Objects.BANANA, weightBanana, idBanana);
			break;
		case idLemon:
			factory.createObjects(container, x, Objects.LEMON, weightLemon, idLemon);
			break;
		case idPear:
			factory.createObjects(container, x, Objects.PEAR, weightPear, idPear);
			break;
		case idPeach:
			factory.createObjects(container, x, Objects.PEACH, weightPeach, idPeach);
			break;
		case idOrange:
			factory.createObjects(container, x, Objects.ORANGE, weightOrange, idOrange);
			break;
		case idTomato:
			factory.createObjects(container, x, Objects.TOMATO, weightTomato, idTomato);
			break;
		case idX:
			factory.createObjects(container, x, Objects.X, weightX, idX);
			break;
		case idY:
			factory.createObjects(container, x, Objects.Y, weightY, idY);
			break;
		case idOne:
			factory.createObjects(container, x, Objects.ONE, weightOne, idOne);
			break;
		case idOneGram:
			factory.createObjects(container, x, Objects.ONEGRAM, weightOneGram, idOneGram);
			break;
		case idFiveGrams:
			factory.createObjects(container, x, Objects.FIVEGRAMS, weightFiveGrams, idFiveGrams);
			break;
		case idTenGrams:
			factory.createObjects(container, x, Objects.TENGRAMS, weightTenGrams, idTenGrams);
			break;
		case idFiftyGrams:
			factory.createObjects(container, x, Objects.FIFTYGRAMS, weightFiftyGrams, idFiftyGrams);
			break;
		case idHunderdGrams:
			factory.createObjects(container, x, Objects.HUNDERDGRAMS, weightHunderdGrams, idHunderdGrams);
			break;
		case idFiveHunderdGrams:
			factory.createObjects(container, x, Objects.FIVEHUNDERDGRAMS, weightFiveHunderdGrams, idFiveHunderdGrams);
			break;
		case idOneEmpty:
			factory.createObjects(container, x, Objects.ONEEMPTY, weightOneEmpty, idOneEmpty);
			break;
		case idOneBlockAbstract:
			factory.createObjects(container, x, Objects.ONEBLOCKABSTRACT, weightOneBlockAbstract, idOneBlockAbstract);
			break;
		case idTwoBlockAbstract:
			factory.createObjects(container, x, Objects.TWOBLOCKABSTRACT, weightTwoBlockAbstract, idTwoBlockAbstract);
			break;
		case idFiveBlockAbstract:
			factory.createObjects(container, x, Objects.FIVEBLOCKABSTRACT, weightFiveBlockAbstract, idFiveBlockAbstract);
			break;
		case idTenBlockAbstract:
			factory.createObjects(container, x, Objects.TENBLOCKABSTRACT, weightTenBlockAbstract, idTenBlockAbstract);
			break;
		case idTwentyBlockAbstract:
			factory.createObjects(container, x, Objects.TWENTYBLOCKABSTRACT, weightTwentyBlockAbstract, idTwentyBlockAbstract);
			break;
		case idFiftyBlockAbstract:
			factory.createObjects(container, x, Objects.FIFTYBLOCKABSTRACT, weightFiftyBlockAbstract, idFiftyBlockAbstract);
			break;
		case idHundredBlockAbstract:
			factory.createObjects(container, x, Objects.HUNDREDBLOCKABSTRACT, weightHundredBlockAbstract, idHundredBlockAbstract);
			break;
		default:
			break;
		}
	}
}
