package nl.numworx.geodefinergwt.client;

import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import fi.euclides.event.Tracker;
import nl.numworx.geodefiner.common.CELL;
import nl.numworx.geodefiner.common.Definitions;
import nl.numworx.geodefiner.common.UIModel;
import nl.numworx.geodefiner.common.UIModelFactory;
import nl.numworx.geodefinergwt.client.ui.UIModelFactoryGWT;
import nl.uu.fi.dwo.interaction.client.JSONUtilities;
import nl.uu.fi.dwo.interaction.client.json.ObjectMap;

@Singleton
public class DefinitionsGWT extends Definitions {

  final private UIModelFactory factory;
  
  @Inject DefinitionsGWT(Tracker viewer, UIModelFactoryGWT gwtFactory) {
    super(viewer);
    factory = gwtFactory;
  }

  @Override
  protected void installConfig(CELL cell, Map<String, ?> config, String name) {
    if(config != null && !config.isEmpty()) {
      UIModel<?, ?> build = factory.build(cell.item);
      ObjectMap cellConfig = JSONUtilities.wrapMap(config);
      cell.config = build;
      cell.config.fromMap(cellConfig);
      cell.config.install();
    }

    super.installConfig(cell, config, name);
  }

}
