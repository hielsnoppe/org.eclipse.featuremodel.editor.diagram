/*******************************************************************************
 * Copyright (c) 2013 FZI Forschungszentrum Informatik.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Benjamin Klatt - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.featuremodel.diagrameditor.diagram;

import org.eclipse.graphiti.dt.AbstractDiagramTypeProvider;
import org.eclipse.graphiti.tb.IToolBehaviorProvider;

/**
 * Defines the diagram type provider.
 * 
 */
public class FMEDiagramTypeProvider extends AbstractDiagramTypeProvider {

  /**
   * Array of all registered tool behavior providers.
   */
  private IToolBehaviorProvider[] toolBehaviorProviders = null;

  /**
   * Creates an instance of {@link FMEDiagramTypeProvider}.
   */
  public FMEDiagramTypeProvider() {
    super();

    // The diagram type provider needs to know its feature provider, so the
    // Graphiti framework can ask which operations are supported.
    setFeatureProvider(new FMEFeatureProvider(this));
  }

  /**
   * Returns all available tool behavior providers.
   * 
   * @return An array of all registered tool behavior providers
   */
  @Override
  public IToolBehaviorProvider[] getAvailableToolBehaviorProviders() {
    if (this.toolBehaviorProviders == null) {
      this.toolBehaviorProviders = new IToolBehaviorProvider[] { new FMEToolBehaviourProvider(this) };
    }
    return this.toolBehaviorProviders;
  }
}
