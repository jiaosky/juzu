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

package juzu;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotate a method for declaring a resource serving controller method.
 *
 * <code><pre>
 *    public void MyController {
 *
 *       &#064;{@link Resource}
 *       public void myResource() {
 *          ...
 *       }
 *    }
 * </pre></code>
 *
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Resource {

  /**
   * The controller method id.
   *
   * @return the controller method id
   */
  String id() default "";

  /**
   * The methods this resource is matching, by default all are matched.
   *
   * @return the http verbs
   */
  HttpMethod[] method() default {
      HttpMethod.GET,
      HttpMethod.HEAD,
      HttpMethod.POST,
      HttpMethod.PUT,
      HttpMethod.DELETE,
      HttpMethod.TRACE,
      HttpMethod.OPTIONS,
      HttpMethod.CONNECT
  };
}
