/*
 * The contents of this file are subject to the terms
 * of the Common Development and Distribution License
 * (the "License").  You may not use this file except
 * in compliance with the License.
 *
 * You can obtain a copy of the license at
 * https://jwsdp.dev.java.net/CDDLv1.0.html
 * See the License for the specific language governing
 * permissions and limitations under the License.
 *
 * When distributing Covered Code, include this CDDL
 * HEADER in each file and include the License file at
 * https://jwsdp.dev.java.net/CDDLv1.0.html  If applicable,
 * add the following below this CDDL HEADER, with the
 * fields enclosed by brackets "[]" replaced with your
 * own identifying information: Portions Copyright [yyyy]
 * [name of copyright owner]
 */
package com.sun.xml.ws.client;

import javax.xml.ws.handler.LogicalHandler;
import javax.xml.ws.handler.Handler;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.namespace.QName;
import java.util.*;

/**
 * @author Rama Pulavarthi
 */

/**
 * This class holds the handler information on the BindingProvider.
 * HandlerConfiguration is immutable, and a new object is created
 * when the BindingImpl is created or User calls Binding.setHandlerChain() or
 * SOAPBinding.setRoles()
 * During inovcation in Stub.process(), snapshot of the handler configuration is set in
 * Packet.handlerConfig
 * The information in the HandlerConfiguration is used by MUPipe and HandlerPipe
 * implementations.
 */
public class HandlerConfiguration {
    private final Set<String> roles;
    /**
     * This chain may contain both soap and logical handlers.
     */
    private final List<Handler> handlerChain;
    private final List<LogicalHandler> logicalHandlers;
    private final List<SOAPHandler> soapHandlers;
    private Set<QName> knownHeaders;
    private Set<QName> handlerKnownHeaders;
    /**
     * @param roles                    This contains the roles assumed by the Binding implementation.
     * @param portKnownHeaders    This contains the headers that are bound to the current WSDL Port
     * @param handlerChain             This contains the handler chain set on the Binding
     * @param logicalHandlers
     * @param soapHandlers
     * @param handlerKnownHeaders The set is comprised of headers returned from SOAPHandler.getHeaders()
     *                                 method calls.
     */
    public HandlerConfiguration(Set<String> roles, Set<QName> portKnownHeaders,
                                List<Handler> handlerChain,
                                List<LogicalHandler> logicalHandlers, List<SOAPHandler> soapHandlers,
                                Set<QName> handlerKnownHeaders) {
        this.roles = roles;
        this.handlerChain = handlerChain;
        this.logicalHandlers = logicalHandlers;
        this.soapHandlers = soapHandlers;
        this.handlerKnownHeaders = handlerKnownHeaders;
        this.knownHeaders = new HashSet<QName>();
        knownHeaders.addAll(portKnownHeaders);
        knownHeaders.addAll(handlerKnownHeaders);
    }

    public Set<String> getRoles() {
        return roles;
    }

    /**
     *
     * @return return a copy of handler chain
     */
    public List<Handler> getHandlerChain() {
        if(handlerChain == null)
            return Collections.emptyList();
        return new ArrayList<Handler>(handlerChain);

    }

    public List<LogicalHandler> getLogicalHandlers() {
        return logicalHandlers;
    }

    public List<SOAPHandler> getSoapHandlers() {
        return soapHandlers;
    }

    public Set<QName> getKnownHeaders() {
        return knownHeaders;
    }

    public Set<QName> getHandlerKnownHeaders() {
        return handlerKnownHeaders;
    }

}
