/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 1997-2013 Oracle and/or its affiliates. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License.  You can
 * obtain a copy of the License at
 * http://glassfish.java.net/public/CDDL+GPL_1_1.html
 * or packager/legal/LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at packager/legal/LICENSE.txt.
 *
 * GPL Classpath Exception:
 * Oracle designates this particular file as subject to the "Classpath"
 * exception as provided by Oracle in the GPL Version 2 section of the License
 * file that accompanied this code.
 *
 * Modifications:
 * If applicable, add the following below the License Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyright [year] [name of copyright owner]"
 *
 * Contributor(s):
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
 */

package server.endpoint1.client;

import junit.framework.TestCase;

import javax.xml.ws.Endpoint;
import javax.xml.transform.stream.StreamSource;
import java.net.URL;
import java.io.*;
import javax.xml.ws.Provider;
import javax.xml.transform.Source;
import javax.xml.ws.WebServiceProvider;
import java.io.StringReader;
import javax.annotation.Resource;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.http.HTTPBinding;
import javax.xml.ws.WebServiceException;

import com.sun.xml.ws.developer.JAXWSProperties;
import com.sun.net.httpserver.HttpExchange;
import testutil.PortAllocator;


/**
 * @author Jitendra Kotamraju
 */
public class MessageContextTest extends TestCase {

    @WebServiceProvider
    static class MessageContextProvider implements Provider<Source> {
        @Resource
        WebServiceContext ctxt;

        public Source invoke(Source source) {
            MessageContext msgCtxt = ctxt.getMessageContext();
            
            HttpExchange exchange = (HttpExchange)msgCtxt.get(JAXWSProperties.HTTP_EXCHANGE);
            if (exchange == null ) {
                throw new WebServiceException("HttpExchange object is not populated");
            }

            String replyElement = new String("<p>hello world</p>");
            StreamSource reply = new StreamSource(new StringReader (replyElement));
            return reply;
        }
    }

    public void testHttpProperties() throws Exception {
        int port = PortAllocator.getFreePort();
        String address = "http://localhost:"+port+"/hello";
        Endpoint e = Endpoint.create(HTTPBinding.HTTP_BINDING, new MessageContextProvider());
        e.publish(address);

        URL url = new URL(address+"/a/b?a=b");
        InputStream in = url.openStream();
        BufferedReader rdr = new BufferedReader(new InputStreamReader(in));
        String str;
        while ((str=rdr.readLine()) != null);

        e.stop();
    }

}

