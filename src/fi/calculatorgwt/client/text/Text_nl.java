package fi.calculatorgwt.client.text;

import java.util.Vector;

public class Text_nl {
	
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

            {	"gradenButton", "Graden"},
            {	"radialenButton", "Radialen"}
		};


}
