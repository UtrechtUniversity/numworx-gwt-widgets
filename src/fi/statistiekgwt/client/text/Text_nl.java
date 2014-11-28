package fi.statistiekgwt.client.text;

import java.util.Vector;

public class Text_nl
{
	private Vector<String> keys = new Vector<String>();
	private Vector<String> values = new Vector<String>();

	public Text_nl()
	{
		Object[][] items = this.getContents();
		for (int i = 0; i < items.length; i++)
		{
			keys.add(items[i][0].toString());
			values.add(items[i][1].toString());
		}
	}

	public Object[][] getContents()
	{
		return contents;
	}

	public String getString(String key)
	{
		int keyint = keys.indexOf(key);
		return values.get(keyint);
	}

	static final Object[][] contents =
	{
		{ "OKButtonText", "OK" },
		{ "cancelButtonText", "Annuleren" },
		{ "addaviewKnopTekst", "Voeg een view toe: " },
		{ "chooseaviewOption", "Kies een view" },
		{ "changeviewnameDialog", "Verander view naam" },
		{ "enternameLabel", "Geef een nieuwe naam" },
		{ "namealreadyinuseLabel",
			"Deze naam is al in gebruik. Geef een andere naam" },
		{ "dataeditableCheckBox", "Data is bewerkbaar" },
		{ "viewseditableCheckBox", "Views zijn bewerkbaar" },
		{ "viewsaddableCheckBox", "Views zijn toevoegbaar" },
		{ "closetabToolTip", "sluit deze tab" },
		{ "splitoptionsDialog", "Kies splits opties" },
		{ "splitoptionsButton", "Maak splitsing" },
		{ "removeSplitoptionsButton", "Verwijder splitsing" },
		{ "splitvariableLabel", "Splitsvariabele" },
		{ "splitsLabel", "Splitsen" },
		{ "noneItem", "Geen" },
		{ "chooseItem", "Kies" },
		{ "doneButton", "Klaar" },
		{ "classDivisionLabel", "Indeling" },
		{ "noClassesLabel", "Aantal klassen" },
		{ "labelBetweenBinsRadio", "labels tussen klassen" },
		{ "labelUnderBinRadio", "labels midden onder klasse" },
		{ "classesLabel", "Klassen" },
		{ "autoButton", "Automatisch bepalen" },
		{ "rowsLabel", "rijen" },
		{ "columnsLabel", "kolommen" },
		{ "swapLabel", "Verwissel" },
		{ "swapTooltip", "Verwissel rij- en kolomvariabele" },
		{ "startvalueLabel", "Beginwaarde" },
		{ "classwidthLabel", "Klassenbreedte" },
		{ "boundariesintervals", "Grenzen intervallen: " },
		{ "boundariesintervalsLabel", "Grenzen intervallen: " },
		{ "observationsLabel", "Waarnemingen" },
		{ "numberLabel", "Aantal meetwaarden: " },
		{ "minLabel", "Minimum: " },
		{ "minimum", "Minimum" },
		{ "maxLabel", "Maximum: " },
		{ "maximum", "Maximum" },
		{ "mean", "Gemiddelde" },
		{ "standardDeviation", "Standaarddeviatie" },
		{ "median", "Mediaan" },
		{ "mode", "Modus" },
		{ "sortItem", "Sorteer" },
		{ "editcolumnItem", "Bewerk kolom" },
		{ "deletecolumnItem", "Wis kolom" },
		{ "addrowButton", "Voeg een rij toe" },
		{ "addcolumnButton", "Voeg een kolom toe" },
		{ "deleteselectedrowsButton", "Wis geselecteerde rijen" },
		{ "pasteclipboardButton", "Plak vanaf klembord" },
		{ "resetButton", "Reset" },
		{ "importButton", "Open bestand" },
		{ "importWarning",
			"De tabel bevat data. Een bestand openen overschrijft deze data en verwijdert de views." },
		{ "binboundariesDialog", "Definieer klassengrenzen" },
		{ "setupDialog", "Set up" },
		{ "viewnameLabel", "View naam: " },
		{ "variableLabel", "Variabele" },
		{ "variableXLabel", "Variabele x-as" },
		{ "variableYLabel", "Variabele y-as" },
		{ "axisLabel", "Variabele-as" },
		{ "amountLabel", "Aantal" },
		{ "splitsingleviewCheckBox", "split in één scherm" },
		{ "addacolumn", "Voeg een kolom toe" },
		{ "columnname", "Kolom naam:" },
		{ "choosetype", "Kies type:" },
		{ "addenumeration", "Voeg opsommingselement toe: " },
		{ "removeselectedelement", "Verwijder geselecteerde" },
		{ "removeAllElements", "Verwijder alle" },
		{ "sortElements", "A-Z" },
		{ "sortElementsTooltip", "Sorteer A-Z" },
		{ "moveElementUpTooltip", "Verplaats omhoog" },
		{ "moveElementDownTooltip", "Verplaats omlaag" },
		{ "uitlegbijkolom", "Uitleg bij kolom:" },
		{ "splitdataButton", "Splits data" },
		{ "verticalboxplotsRadio", "verticale boxplots" },
		{ "horizontalboxplotsRadio", "horizontale boxplots" },
		{ "variablexaxisLabel", "Variabele X-As" },
		{ "variableyaxisLabel", "Variabele Y-As" },
		{ "usecolorscaleCheckbox", "Gebruik kleurschaal" },
		{ "variablecolorscaleLabel", "Variabele kleurschaal" },
		{ "showcorrelationCheckbox", "Laat correlatie zien" },
		{ "showpercentageCheckbox", "laat percentage zien" },
		{ "showfrequencyCheckbox", "laat frequentie zien" },
		{ "numberofbinsLabel", "Aantal bins" },
		{ "BoundariesButton", "Grenzen" },
		{ "binsButton", "Klassen" },
		{ "hideButtonLabel", "Verberg" },
		{ "showcumulativefrequencyCheckbox",
			"laat cumulatieve frequentie zien" },
		{ "totalLabel", "Totaal" },
		{ "percentageRadio", "Percentage" },
		{ "stackfrequencypolygonsCheckbox",
			"Stapel frequentiepolygonen" },
		{ "cumulativeCheckbox", "cumulatief" },
		{ "tableOption", "Tabel" },
		{ "histogramOption", "Staafdiagram" },
		{ "dotplotOption", "Dotplot" },
		{ "frequencytableOption", "Frequentietabel" },
		{ "frequencypolygonOption", "Frequentiepolygoon" },
		{ "boxplotOption", "Boxplot" },
		{ "crosstabOption", "Kruistabel" },
		{ "scatterplotOption", "Spreidingsdiagram" },
		{ "descriptivesOption", "Kengetallen" },
		{ "settingsButton", "Instellingen" },
		{ "nexttoeachotherCheckbox", "Naast elkaar" },
		{ "chooseStartVarLabel", "Kies variabele: " },
		{ "chooseStartVarRowLabel", "Kies variabele rijen: " },
		{ "chooseStartVarColumnLabel", "Kies variabele kolommen: " },
		{ "chooseStartVarXLabel", "Kies variabele x-as: " },
		{ "chooseStartVarYLabel", "Kies variabele y-as: " },
		{ "chooseAVariableOption", "Kies een variabele" },
		{ "absRelLabel", "Weergave" },
		{ "nextToEachOtherRadioItem", "staafjes naast elkaar" },
		{ "aboveEachOtherRadioItem", "staafjes gestapeld" },
		{ "separateFromEachOtherRadioItem", "losse diagrammen" },
		{ "percentageLabel", "Percentage" },
		{ "frequentieLabel", "Frequentie" },
		{ "percentage_endTotal", "Eindtotaal op 100%" },
		{ "percentage_rowTotal", "Rijtotaal op 100%" },
		{ "percentage_columnTotal", "Kolomtotaal op 100%" },
		{ "integer", "Geheel getal" },
		{ "double", "Decimaal getal" },
		{ "string", "Tekst" },
		{ "enum", "Opsomming" },
		{ "messageNrRowsMoreThan", "Aantal rijen is meer dan " },
		{ "messageChooseOtherVar", "Kies een andere variabele." },
		{ "correlationNoShow", "Correlatie kan niet berekend worden" },
		{ "significanceNoShow",
			"significantie kan niet berekend worden" },
		{ "notAvailable", "Niet beschikbaar" },
		{ "selection", "selectie" },
		{ "selectCSVFile", "Selecteer een CSV bestand: " },
		{ "importFile", "Importeer bestand" },
		{ "noFileMessage", "Geen bestand gekozen" },
		{ "noCSVMessage", "Het gekozen bestand is geen CSV-bestand. Kies een CSV-bestand (.csv)." }
	};
}
