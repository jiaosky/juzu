/*
 * Copyright (C) 2012 eXo Platform SAS.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package juzu.impl.inject.spi.spring;

import org.springframework.beans.factory.annotation.AnnotatedGenericBeanDefinition;
import org.springframework.beans.factory.support.AutowireCandidateQualifier;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;

import java.lang.annotation.Annotation;

/** @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a> */
class SingletonBean extends AbstractBean {

  /** . */
  final Object instance;

  SingletonBean(Object instance, Iterable<Annotation> qualifiers) {
    super(instance.getClass(), qualifiers);

    //
    this.instance = instance;
  }

  @Override
  void configure(String name, SpringInjector builder, DefaultListableBeanFactory factory) {
    AnnotatedGenericBeanDefinition definition = new AnnotatedGenericBeanDefinition(type);
    definition.setScope("singleton");
    if (qualifiers != null) {
      for (AutowireCandidateQualifier qualifier : qualifiers) {
        definition.addQualifier(qualifier);
      }
    }
    factory.registerBeanDefinition(name, definition);

    // Register instance
    builder.instances.put(name, instance);
  }
}
