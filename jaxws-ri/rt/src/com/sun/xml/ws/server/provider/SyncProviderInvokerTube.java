/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * 
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
 * 
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License. You can obtain
 * a copy of the License at https://glassfish.dev.java.net/public/CDDL+GPL.html
 * or glassfish/bootstrap/legal/LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 * 
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at glassfish/bootstrap/legal/LICENSE.txt.
 * Sun designates this particular file as subject to the "Classpath" exception
 * as provided by Sun in the GPL Version 2 section of the License file that
 * accompanied this code.  If applicable, add the following below the License
 * Header, with the fields enclosed by brackets [] replaced by your own
 * identifying information: "Portions Copyrighted [year]
 * [name of copyright owner]"
 * 
 * Contributor(s):
 * 
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
package com.sun.xml.ws.server.provider;

import com.sun.istack.NotNull;
import com.sun.xml.ws.api.WSBinding;
import com.sun.xml.ws.api.message.Packet;
import com.sun.xml.ws.api.model.wsdl.WSDLPort;
import com.sun.xml.ws.api.pipe.NextAction;
import com.sun.xml.ws.api.server.Invoker;

import javax.xml.ws.Provider;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This tube is used to invoke the {@link Provider} endpoints.
 *
 * @author Jitendra Kotamraju
 */
class SyncProviderInvokerTube<T> extends ProviderInvokerTube<T> {

    private static final Logger LOGGER = Logger.getLogger(
        com.sun.xml.ws.util.Constants.LoggingDomain + ".server.SyncProviderInvokerTube");

    public SyncProviderInvokerTube(Invoker invoker, ProviderArgumentsBuilder<T> argsBuilder) {
        super(invoker, argsBuilder);
    }

    /*
    * This binds the parameter for Provider endpoints and invokes the
    * invoke() method of {@linke Provider} endpoint. The return value from
    * invoke() is used to create a new {@link Message} that traverses
    * through the Pipeline to transport.
    */
    public NextAction processRequest(Packet request) {
        WSDLPort port = getEndpoint().getPort();
        WSBinding binding = getEndpoint().getBinding();
        T param = argsBuilder.getParameter(request);

        LOGGER.fine("Invoking Provider Endpoint");

        T returnValue;
        try {
            returnValue = getInvoker(request).invokeProvider(request, param);
        } catch(Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            Packet response = argsBuilder.getResponse(request,e,port,binding);
            return doReturnWith(response);
        }
        if (returnValue == null) {
            // Oneway. Send response code immediately for transports(like HTTP)
            // Don't do this above, since close() may generate some exceptions
            if (request.transportBackChannel != null) {
                request.transportBackChannel.close();
            }
        }
        Packet response = argsBuilder.getResponse(request,returnValue,port,binding);
        return doReturnWith(response);
    }

    public NextAction processResponse(Packet response) {
        throw new IllegalStateException("InovkerPipe's processResponse shouldn't be called.");
    }

    public NextAction processException(@NotNull Throwable t) {
        throw new IllegalStateException("InovkerPipe's processException shouldn't be called.");
    }

}