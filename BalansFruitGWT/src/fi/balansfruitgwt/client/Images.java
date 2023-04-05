package fi.balansfruitgwt.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

/**
 * Specified all images and provide efficient access to image data at runtime.
 *
 * To add a new image, add to following lines:
 * :  @Source("PATH");
 *    ImageResource FUNCTIONNAME();
 */
public interface Images extends ClientBundle {

    public static final Images INSTANCE =  GWT.create(Images.class);

    @Source("images/1.gif")
    ImageResource one();

    @Source("images/1g.gif")
    ImageResource oneGram();

    @Source("images/5g.gif")
    ImageResource fiveGrams();

    @Source("images/10g.gif")
    ImageResource tenGrams();

    @Source("images/50g.gif")
    ImageResource fiftyGrams();

    @Source("images/100g.gif")
    ImageResource hundredGrams();

    @Source("images/500g.gif")
    ImageResource fiveHundredGrams();

    @Source("images/ananas.gif")
    ImageResource pineapple();

    @Source("images/appel.gif")
    ImageResource apple();

    @Source("images/balansgoed.gif")
    ImageResource balanceCenter();

    @Source("images/balanslinks.gif")
    ImageResource balanceLeft();

    @Source("images/balansrechts.gif")
    ImageResource balanceRight();

    @Source("images/banaan.gif")
    ImageResource banana();

    @Source("images/citroen.gif")
    ImageResource lemon();

    @Source("images/peer.gif")
    ImageResource pear();

    @Source("images/perzik.gif")
    ImageResource peach();

    @Source("images/sinaasappel.gif")
    ImageResource orange();

    @Source("images/tomaat.gif")
    ImageResource tomato();

    @Source("images/x.gif")
    ImageResource x();

    @Source("images/y.gif")
    ImageResource y();

    @Source("images/1leeg.gif")
    ImageResource oneEmpty();

    @Source("images/1blok_abstract.gif")
    ImageResource oneBlockAbstract();

    @Source("images/2blok_abstract.gif")
    ImageResource twoBlockAbstract();

    @Source("images/5blok_abstract.gif")
    ImageResource fiveBlockAbstract();

    @Source("images/10blok_abstract.gif")
    ImageResource tenBlockAbstract();

    @Source("images/20blok_abstract.gif")
    ImageResource twentyBlockAbstract();

    @Source("images/50blok_abstract.gif")
    ImageResource fiftyBlockAbstract();

    @Source("images/100blok_abstract.gif")
    ImageResource hundredBlockAbstract();
}
