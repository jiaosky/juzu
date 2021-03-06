/*
 * Copyright 2013 eXo Platform SAS
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package juzu.impl.common;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;

/**
 * @author Julien Viet
 */
public abstract class AbstractAnnotatedElement implements AnnotatedElement {

  /** . */
  public static final AbstractAnnotatedElement EMPTY = wrap(new Annotation[0]);

  public static AbstractAnnotatedElement wrap(final Annotation[] annotations) {
    return new AbstractAnnotatedElement() {
      @Override
      public Annotation[] getDeclaredAnnotations() {
        return annotations;
      }
    };
  }

  @Override
  public boolean isAnnotationPresent(Class<? extends Annotation> annotationClass) {
    return getAnnotation(annotationClass) != null;
  }

  @Override
  public <T extends Annotation> T getAnnotation(Class<T> annotationClass) {
    for (Annotation annotation : getDeclaredAnnotations()) {
      if (annotationClass.isInstance(annotation)) {
        return annotationClass.cast(annotation);
      }
    }
    return null;
  }

  @Override
  public Annotation[] getAnnotations() {
    return getDeclaredAnnotations();
  }
}
