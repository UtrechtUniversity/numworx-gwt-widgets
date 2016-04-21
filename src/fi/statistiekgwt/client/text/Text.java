package fi.statistiekgwt.client.text;

import com.google.gwt.i18n.client.Constants;

public interface Text extends Constants
{
	@DefaultStringValue("OK")
	String oKButtonText(); // was: "OKButtonText"
	
	@DefaultStringValue("Annuleren")
	String cancelButtonText();
	
	@DefaultStringValue("Sluiten")
	String closeButtonText();
	
	@DefaultStringValue("Voeg een view toe: ")
	String addaviewKnopTekst();
	
	@DefaultStringValue("Kies een view")
	String chooseaviewOption();
	
	@DefaultStringValue("Wijzig viewnaam")
	String changeviewnameDialog();
	
	@DefaultStringValue("Wijzig naam")
	String changeViewName();
	
	@DefaultStringValue("Toon in los venster")
	String showInDialog();
	
	@DefaultStringValue("Geef een nieuwe naam")
	String enternameLabel();
	
	@DefaultStringValue("Deze naam is al in gebruik. Geef een andere naam")
	String namealreadyinuseLabel();
	
	@DefaultStringValue("Data is bewerkbaar")
	String dataeditableCheckBox();
	
	@DefaultStringValue("Views zijn bewerkbaar")
	String viewseditableCheckBox();
	
	@DefaultStringValue("Views zijn toevoegbaar")
	String viewsaddableCheckBox();
	
	@DefaultStringValue("sluit deze tab")
	String closetabToolTip();
	
	@DefaultStringValue("Kies splitsopties")
	String splitoptionsDialog();
	
	@DefaultStringValue("Maak splitsing")
	String splitoptionsButton();
	
	@DefaultStringValue("Verwijder splitsing")
	String removeSplitoptionsButton();
	
	@DefaultStringValue("Splitsvariabele")
	String splitvariableLabel();
	
	@DefaultStringValue("Splitsen")
	String splitsLabel();
	
	@DefaultStringValue("Geen")
	String noneItem();
	
	@DefaultStringValue("Kies")
	String chooseItem();
	
	@DefaultStringValue("Klaar")
	String doneButton();
	
	@DefaultStringValue("Indeling")
	String classDivisionLabel();
	
	@DefaultStringValue("Aantal klassen")
	String noClassesLabel();
	
	@DefaultStringValue("labels tussen klassen")
	String labelBetweenBinsRadio();
	
	@DefaultStringValue("labels midden onder klasse")
	String labelUnderBinRadio();
	
	@DefaultStringValue("Klassen")
	String classesLabel();
	
	@DefaultStringValue("rijen")
	String rowsLabel();
	
	@DefaultStringValue("kolommen")
	String columnsLabel();
	
	@DefaultStringValue("Verwissel")
	String swapLabel();
	
	@DefaultStringValue("Verwissel rij- en kolomvariabele")
	String swapTooltip();
	
	@DefaultStringValue("Beginwaarde")
	String startvalueLabel();
	
	@DefaultStringValue("Minimumwaarde")
	String minValueLabel();
	
	@DefaultStringValue("Maximumwaarde")
	String maxValueLabel();
	
	@DefaultStringValue("Klassenbreedte")
	String classwidthLabel();
	
	@DefaultStringValue("Waarnemingen")
	String observationsLabel();
	
	@DefaultStringValue("Aantal meetwaarden: ")
	String numberLabel();
	
	@DefaultStringValue("Minimum: ")
	String minLabel();
	
	@DefaultStringValue("Minimum")
	String minimum();
	
	@DefaultStringValue("Maximum: ")
	String maxLabel();
	
	@DefaultStringValue("Maximum")
	String maximum();
	
	@DefaultStringValue("Gemiddelde")
	String mean();
	
	@DefaultStringValue("Standaarddeviatie")
	String standardDeviation();
	
	@DefaultStringValue("Mediaan")
	String median();
	
	@DefaultStringValue("Modus")
	String mode();
	
	@DefaultStringValue("minimum = ")
	String minimumIs();
	
	@DefaultStringValue("kleinste niet-uitschieter = ")
	String tukeyMinimumIs();
	
	@DefaultStringValue("1e kwartiel = ")
	String firstQuartileIs();
	
	@DefaultStringValue("mediaan = ")
	String medianIs();
	
	@DefaultStringValue("3e kwartiel = ")
	String thirdQuartileIs();
	
	@DefaultStringValue("maximum = ")
	String maximumIs();
	
	@DefaultStringValue("grootste niet-uitschieter = ")
	String tukeyMaximumIs();
	
	@DefaultStringValue("waarde = ")
	String valueIs();
	
	@DefaultStringValue("Sorteer")
	String sortItem();
	
	@DefaultStringValue("Bewerk kolom")
	String editcolumnItem();
	
	@DefaultStringValue("Wis kolom")
	String deletecolumnItem();
	
	@DefaultStringValue("Kolominfo")
	String infocolumnItem();
	
	@DefaultStringValue("Markeer waarde als uitschieter")
	String markOutlierCell();
	
	@DefaultStringValue("Markeer rij als uitschieter")
	String markOutlierRow();
	
	@DefaultStringValue("Demarkeer waarde als uitschieter")
	String demarkOutlierCell();
	
	@DefaultStringValue("Demarkeer rij als uitschieter")
	String demarkOutlierRow();
	
	@DefaultStringValue("Voeg rij toe")
	String addrowButton();
	
	@DefaultStringValue("Voeg kolom toe")
	String addcolumnButton();
	
	@DefaultStringValue("Wis geselecteerde rijen")
	String deleteselectedrowsButton();
	
	@DefaultStringValue("Plak vanaf klembord")
	String pasteclipboardButton();
	
	@DefaultStringValue("Plak vanaf klembord")
	String pasteclipboardDialog();
	
	@DefaultStringValue("Plak de data in het tekstvak en klik op ")
	String pasteclipboardMessage();
	
	@DefaultStringValue("Scheid de waarden met ';' of tab. Zet iedere rij op een nieuwe regel met 'Enter'.")
	String pasteclipboardMessage2();
	
	@DefaultStringValue("Importeer")
	String importPastedDataButton();
	
	@DefaultStringValue("De data heeft niet het goede formaat.")
	String importPastedDataFailMessage();
	
	@DefaultStringValue("Reset")
	String resetButton();
	
	@DefaultStringValue("Open bestand")
	String importButton();
	
	@DefaultStringValue("De tabel bevat data. Een bestand openen overschrijft deze data en verwijdert de views.")
	String importWarning();
	
	@DefaultStringValue("Open bestand")
	String importDialogLabel();
	
	@DefaultStringValue("Variabele")
	String variableLabel();
	
	@DefaultStringValue("Variabele x-as")
	String variableXLabel();
	
	@DefaultStringValue("Variabele y-as")
	String variableYLabel();
	
	@DefaultStringValue("Variabele-as")
	String axisLabel();
	
	@DefaultStringValue("Aantal")
	String amountLabel();
	
	@DefaultStringValue("aantal = ")
	String amountIs();
	
	@DefaultStringValue("split in één scherm")
	String splitsingleviewCheckBox();
	
	@DefaultStringValue("Voeg een kolom toe")
	String addacolumn();
	
	@DefaultStringValue("Bewerk kolom")
	String editacolumn();
	
	@DefaultStringValue("Kolominfo")
	String columninfo();
	
	@DefaultStringValue("Kolomnaam:")
	String columnname();
	
	@DefaultStringValue("Kies type:")
	String choosetype();
	
	@DefaultStringValue("Voeg opsommingselement toe: ")
	String addenumeration();
	
	@DefaultStringValue("Verwijder geselecteerde")
	String removeselectedelement();
	
	@DefaultStringValue("Verwijder alle")
	String removeAllElements();
	
	@DefaultStringValue("A-Z")
	String sortElements();
	
	@DefaultStringValue("Sorteer A-Z")
	String sortElementsTooltip();
	
	@DefaultStringValue("Verplaats omhoog")
	String moveElementUpTooltip();
	
	@DefaultStringValue("Verplaats omlaag")
	String moveElementDownTooltip();
	
	@DefaultStringValue("Uitleg bij kolom:")
	String uitlegbijkolom();
	
	@DefaultStringValue("Splits data")
	String splitdataButton();
	
	@DefaultStringValue("Tukey boxplot")
	String tukeyCheckbox();
	
	@DefaultStringValue("verticale boxplots")
	String verticalboxplotsRadio();
	
	@DefaultStringValue("horizontale boxplots")
	String horizontalboxplotsRadio();
	
	@DefaultStringValue("Gebruik kleurschaal")
	String usecolorscaleCheckbox();
	
	@DefaultStringValue("Variabele kleurschaal")
	String variablecolorscaleLabel();
	
	@DefaultStringValue("Laat correlatie zien")
	String showcorrelationCheckbox();
	
	@DefaultStringValue("laat percentage zien")
	String showpercentageCheckbox();
	
	@DefaultStringValue("laat frequentie zien")
	String showfrequencyCheckbox();
	
	@DefaultStringValue("Klassen")
	String binsButton();
	
	@DefaultStringValue("Verberg")
	String hideButtonLabel();
	
	@DefaultStringValue("laat cumulatieve frequentie zien")
	String showcumulativefrequencyCheckbox();
	
	@DefaultStringValue("Totaal")
	String totalLabel();
	
	@DefaultStringValue("Percentage")
	String percentageRadio();
	
	@DefaultStringValue("Stapel frequentiepolygonen")
	String stackfrequencypolygonsCheckbox();
	
	@DefaultStringValue("Optimaliseer schaal")
	String optimizeScaleBox();
	
	@DefaultStringValue("Optimaliseer schaal x-as")
	String optimizeScaleXBox();
	
	@DefaultStringValue("Optimaliseer schaal y-as")
	String optimizeScaleYBox();
	
	@DefaultStringValue("cumulatief")
	String cumulativeCheckbox();
	
	@DefaultStringValue("Tabel")
	String tableOption();
	
	@DefaultStringValue("Staafdiagram")
	String histogramOption();
	
	@DefaultStringValue("Dotplot")
	String dotplotOption();
	
	@DefaultStringValue("Frequentietabel")
	String frequencytableOption();
	
	@DefaultStringValue("Frequentiepolygoon")
	String frequencypolygonOption();
	
	@DefaultStringValue("Boxplot")
	String boxplotOption();
	
	@DefaultStringValue("Kruistabel")
	String crosstabOption();
	
	@DefaultStringValue("Spreidingsdiagram")
	String scatterplotOption();
	
	@DefaultStringValue("Kengetallen")
	String descriptivesOption();
	
	@DefaultStringValue("Instellingen")
	String settingsButton();
	
	@DefaultStringValue("Naast elkaar")
	String nexttoeachotherCheckbox();
	
	@DefaultStringValue("Kies variabele: ")
	String chooseStartVarLabel();
	
	@DefaultStringValue("Kies variabele rijen: ")
	String chooseStartVarRowLabel();
	
	@DefaultStringValue("Kies variabele kolommen: ")
	String chooseStartVarColumnLabel();
	
	@DefaultStringValue("Kies variabele x-as: ")
	String chooseStartVarXLabel();
	
	@DefaultStringValue("Kies variabele y-as: ")
	String chooseStartVarYLabel();
	
	@DefaultStringValue("Kies een variabele")
	String chooseAVariableOption();
	
	@DefaultStringValue("Weergave")
	String absRelLabel();
	
	@DefaultStringValue("staafjes naast elkaar")
	String nextToEachOtherRadioItem();
	
	@DefaultStringValue("staafjes gestapeld")
	String aboveEachOtherRadioItem();
	
	@DefaultStringValue("losse diagrammen")
	String separateFromEachOtherRadioItem();
	
	@DefaultStringValue("Percentage")
	String percentageLabel();
	
	@DefaultStringValue("Frequentie")
	String frequentieLabel();
	
	@DefaultStringValue("Eindtotaal op 100%")
	String percentage_endTotal();
	
	@DefaultStringValue("Rijtotaal op 100%")
	String percentage_rowTotal();
	
	@DefaultStringValue("Splittotaal op 100%")
	String percentage_splitTotal();
	
	@DefaultStringValue("Kolomtotaal op 100%")
	String percentage_columnTotal();
	
	@DefaultStringValue("Geheel getal")
	String integer();
	
	@DefaultStringValue("Decimaal getal")
	String decimalNumber(); // was: "double"
	
	@DefaultStringValue("Tekst")
	String string();
	
	@DefaultStringValue("Opsomming")
	String enumValue(); // was: "enum"
	
	@DefaultStringValue("Aantal rijen is meer dan ")
	String messageNrRowsMoreThan();
	
	@DefaultStringValue("Kies een andere variabele.")
	String messageChooseOtherVar();
	
	@DefaultStringValue("Correlatie kan niet berekend worden")
	String correlationNoShow();
	
	@DefaultStringValue("significantie kan niet berekend worden")
	String significanceNoShow();
	
	@DefaultStringValue("Niet beschikbaar")
	String notAvailable();
	
	@DefaultStringValue("selectie")
	String selection();
	
	@DefaultStringValue("Selecteer een CSV bestand: ")
	String selectCSVFile();
	
	@DefaultStringValue("Importeer bestand")
	String importFile();
	
	@DefaultStringValue("Er is geen bestand gekozen.")
	String noFileMessage();
	
	@DefaultStringValue("Het gekozen bestand is geen CSV-bestand. Kies een CSV-bestand (.csv). "
		+ "Een excelbestand kan worden omgezet naar een CSV-bestand door 'Opslaan als...' in formaat 'CSV'.")
	String noCSVMessage();
	
	@DefaultStringValue("De tabel is leeg")
	String emptyTableMessage();
	
	@DefaultStringValue("Sla de statistiekcomponent opnieuw op in de auteursomgeving "
			+ "om de component te kunnen bekijken in HTML5.")
	String notHTML5ReadyMessage();
	
	@DefaultStringValue("Selecteer kleuren")
	String selectColors();
	
	@DefaultStringValue("De tabel wordt geladen...")
	String loadingTable();
	
	@DefaultStringValue("Er is iets misgegaan. "
			+ "Het geselecteerde databestand heeft niet het goede formaat. Kies een CSV-bestand.")
	String errorLoadingTable();
	
	@DefaultStringValue("Weet je zeker dat je deze view wilt verwijderen?")
	String removeViewWarning();
	
	@DefaultStringValue("Ja, verwijder")
	String yesRemove();
	
	@DefaultStringValue("Nee, annuleren")
	String noCancel();
	
	@DefaultStringValue("nl")
	String language();
	
}
