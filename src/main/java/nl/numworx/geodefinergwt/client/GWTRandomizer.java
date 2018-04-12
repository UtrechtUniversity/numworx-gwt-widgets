package nl.numworx.geodefinergwt.client;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.inject.Singleton;

import fi.wiskopdr.FormuleParser;
import nl.numworx.geodefiner.common.DefaultRandomizer;


@Singleton
public class GWTRandomizer extends DefaultRandomizer {

	private static Logger LOG = Logger.getLogger("GWTRandomizer");
	@Inject public GWTRandomizer() {
	}

	@Override
	public String randomize(Map<String, Number> random, String text) {
		try {
			HashMap<String, Number> m = new HashMap<String, Number>(random);
			String[] keys = random.keySet().toArray(new String[random.size()]);
			return FormuleParser.randomizeString(text, keys, m);
		} catch (Exception e) {
			LOG.log(Level.SEVERE, "randomize " + text, e);
		}
		return super.randomize(random, text);
	}

}
