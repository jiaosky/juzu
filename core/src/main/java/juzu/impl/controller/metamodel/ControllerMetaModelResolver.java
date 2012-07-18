package juzu.impl.controller.metamodel;

import juzu.impl.controller.ControllerResolver;
import juzu.request.Phase;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/** @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a> */
class ControllerMetaModelResolver extends ControllerResolver<ControllerMethodMetaModel> {

  /** . */
  private final ControllersMetaModel controllers;

  /** . */
  private final ControllerMethodMetaModel[] methods;

  /** . */
  private final int size;

  ControllerMetaModelResolver(ControllersMetaModel controllers) throws NullPointerException {
    int size = 0;
    List<ControllerMethodMetaModel> methods = new ArrayList<ControllerMethodMetaModel>();
    for (ControllerMetaModel controller : controllers.getChildren(ControllerMetaModel.class)) {
      size++;
      for (ControllerMethodMetaModel method : controller.getMethods()) {
        methods.add(method);
      }
    }

    //
    this.controllers = controllers;
    this.methods = methods.toArray(new ControllerMethodMetaModel[methods.size()]);
    this.size = size;
  }

  @Override
  public ControllerMethodMetaModel[] getMethods() {
    return methods;
  }

  @Override
  public String getId(ControllerMethodMetaModel method) {
    return method.getId();
  }

  @Override
  public Phase getPhase(ControllerMethodMetaModel method) {
    return method.getPhase();
  }

  @Override
  public String getName(ControllerMethodMetaModel method) {
    return method.getName();
  }

  @Override
  public boolean isDefault(ControllerMethodMetaModel method) {
    return method.getController().getHandle().getFQN().equals(controllers.defaultController) || size < 2;
  }

  @Override
  public Collection<String> getParameterNames(ControllerMethodMetaModel method) {
    return method.getParameterNames();
  }
}