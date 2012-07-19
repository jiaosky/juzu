/*
 * Copyright (C) 2011 eXo Platform SAS.
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

package juzu.impl.bridge.spi.standalone;

import juzu.PropertyMap;
import juzu.PropertyType;
import juzu.impl.application.ApplicationContext;
import juzu.impl.common.MethodHandle;
import juzu.impl.controller.descriptor.MethodDescriptor;
import juzu.impl.inject.Scoped;
import juzu.impl.inject.ScopedContext;
import juzu.impl.request.Request;
import juzu.impl.bridge.spi.RequestBridge;
import juzu.impl.common.Tools;
import juzu.impl.router.RenderContext;
import juzu.request.HttpContext;
import juzu.request.Phase;
import juzu.request.SecurityContext;
import juzu.request.WindowContext;
import juzu.standalone.JuzuServlet;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Map;

/** @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a> */
public abstract class ServletRequestBridge implements RequestBridge, HttpContext, WindowContext {

  /** . */
  final ApplicationContext application;

  /** . */
  final ServletBridge servlet;

  /** . */
  final HttpServletRequest req;

  /** . */
  final HttpServletResponse resp;

  /** . */
  final Map<String, String[]> parameters;

  /** . */
  final MethodHandle target;

  /** . */
  protected Request request;

  ServletRequestBridge(
      ApplicationContext application,
      ServletBridge servlet,
      HttpServletRequest req,
      HttpServletResponse resp,
      MethodHandle target,
      Map<String, String[]> parameters) {

    //
    this.application = application;
    this.target = target;
    this.servlet = servlet;
    this.req = req;
    this.resp = resp;
    this.parameters = parameters;
    this.request = null;
  }

  //

  public MethodHandle getTarget() {
    return target;
  }

  public Cookie[] getCookies() {
    return req.getCookies();
  }

  public String getScheme() {
    return req.getScheme();
  }

  public int getServerPort() {
    return req.getServerPort();
  }

  public String getServerName() {
    return req.getServerName();
  }

  public String getContextPath() {
    return req.getContextPath();
  }

  public <T> T getProperty(PropertyType<T> propertyType) {
    if (JuzuServlet.PATH.equals(propertyType)) {
      return propertyType.getType().cast(req.getRequestURI());
    }
    return null;
  }

  //

  public final String getNamespace() {
    return "window_ns";
  }

  public final String getId() {
    return "window_id";
  }
  //

  public final Map<String, String[]> getParameters() {
    return parameters;
  }

  public final HttpContext getHttpContext() {
    return this;
  }

  public final WindowContext getWindowContext() {
    return this;
  }

  public final SecurityContext getSecurityContext() {
    return null;
  }

  public final Scoped getRequestValue(Object key) {
    ScopedContext context = getRequestContext(false);
    return context != null ? context.get(key) : null;
  }

  public final void setRequestValue(Object key, Scoped value) {
    if (value != null) {
      ScopedContext context = getRequestContext(false);
      if (context != null) {
        context.set(key, null);
      }
    }
    else {
      getRequestContext(true).set(key, value);
    }
  }

  public final Scoped getFlashValue(Object key) {
    ScopedContext context = getFlashContext(false);
    return context != null ? context.get(key) : null;
  }

  public final void setFlashValue(Object key, Scoped value) {
    if (value == null) {
      ScopedContext context = getFlashContext(false);
      if (context != null) {
        context.set(key, null);
      }
    }
    else {
      getFlashContext(true).set(key, value);
    }
  }

  public final Scoped getSessionValue(Object key) {
    ScopedContext context = getSessionContext(false);
    return context != null ? context.get(key) : null;
  }

  public final void setSessionValue(Object key, Scoped value) {
    if (value == null) {
      ScopedContext context = getSessionContext(false);
      if (context != null) {
        context.set(key, null);
      }
    }
    else {
      getSessionContext(true).set(key, value);
    }
  }

  public final Scoped getIdentityValue(Object key) {
    return null;
  }

  public final void setIdentityValue(Object key, Scoped value) {
  }

  public void purgeSession() {
    HttpSession session = req.getSession(false);
    if (session != null) {
      for (String key : Tools.list((Enumeration<String>)session.getAttributeNames())) {
        session.removeAttribute(key);
      }
    }
  }

  public <T> String checkPropertyValidity(Phase phase, PropertyType<T> propertyType, T propertyValue) {
    // For now we don't validate anything
    return null;
  }

  public final String renderURL(MethodHandle target, Map<String, String[]> parameters, PropertyMap properties) {
    StringBuilder buffer = new StringBuilder();
    buffer.append(req.getScheme());
    buffer.append("://");
    buffer.append(req.getServerName());
    int port = req.getServerPort();
    if (port != 80) {
      buffer.append(':').append(port);
    }

    // Add context path
    buffer.append(req.getContextPath());

    // Add servlet path
    if (!req.getServletPath().equals("/")) {
      buffer.append(req.getServletPath());
    }

    //
    MethodDescriptor method = application.getDescriptor().getControllers().getMethodByHandle(target);

    //
    RenderContext ctx = new RenderContext(Collections.singletonMap(ServletBridge.TARGET, method.getHandle().toString()));
    String s = servlet.router.render(ctx);
    if (s != null) {
      buffer.append(s);
    } else {
      throw new UnsupportedOperationException("handle me gracefully");
    }

    //
/*
    boolean first = true;
    for (Map.Entry<String, String[]> parameter : parameters.entrySet()) {
      String name = parameter.getKey();
      try {
        String encName = URLEncoder.encode(name, "UTF-8");
        for (String value : parameter.getValue()) {
          String encValue = URLEncoder.encode(value, "UTF-8");
          buffer.append(first ? '?' : '&').append(encName).append('=').append(encValue);
          first = false;
        }
      }
      catch (UnsupportedEncodingException e) {
        // Should not happen
        throw new AssertionError(e);
      }
    }
*/
    return buffer.toString();
  }

  protected final ScopedContext getRequestContext(boolean create) {
    ScopedContext context = (ScopedContext)req.getAttribute("juzu.request_scope");
    if (context == null && create) {
      req.setAttribute("juzu.request_scope", context = new ScopedContext());
    }
    return context;
  }

  protected final ScopedContext getFlashContext(boolean create) {
    ScopedContext context = null;
    HttpSession session = req.getSession(create);
    if (session != null) {
      context = (ScopedContext)session.getAttribute("juzu.flash_scope");
      if (context == null && create) {
        session.setAttribute("juzu.flash_scope", context = new ScopedContext());
      }
    }
    return context;
  }

  protected final ScopedContext getSessionContext(boolean create) {
    ScopedContext context = null;
    HttpSession session = req.getSession(create);
    if (session != null) {
      context = (ScopedContext)session.getAttribute("juzu.session_scope");
      if (context == null && create) {
        session.setAttribute("juzu.session_scope", context = new ScopedContext());
      }
    }
    return context;
  }

  public final void begin(Request request) {
    this.request = request;
  }

  public void end() {
    this.request = null;

    //
    ScopedContext context = getRequestContext(false);
    if (context != null) {
      context.close();
    }
  }
}
