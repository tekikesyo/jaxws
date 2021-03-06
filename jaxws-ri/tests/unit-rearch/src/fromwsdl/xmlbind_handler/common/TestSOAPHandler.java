/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2005-2013 Oracle and/or its affiliates. All rights reserved.
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

package fromwsdl.xmlbind_handler.common;

import java.util.Map;
import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;
import javax.xml.soap.*;

/*
 * Trying to use this handler should result in an exception on the
 * server end. Client code uses soap binding.
 */
public class TestSOAPHandler implements SOAPHandler<SOAPMessageContext> {

    private boolean expectEmptyResponse = false;
    private Exception exception = null;
    
    public void setExpectEmptyResponse(boolean expectEmptyResponse) {
        this.expectEmptyResponse = expectEmptyResponse;
    }
    
    public Exception getException() {
        return exception;
    }
    
    public Set<QName> getHeaders() {
        return null;
    }
    
    public boolean handleMessage(SOAPMessageContext context) {
        if (isOutbound(context)) {
            return true;
        }
        exception = null;
        try {
            if (!expectEmptyResponse) {
                // make sure a message can be created
                SOAPBody body = context.getMessage().getSOAPBody();
                int dummyVarForDebugger = 0;
            } else {
                // make sure a soap message cannot be created
                try {
                    SOAPBody body = context.getMessage().getSOAPBody();
                    exception = new Exception(
                        "did not receive error while creating soap message");
                } catch (SOAPException se) {
                    // good
                    exception = null;
                }
            }
        } catch (Exception e) {
            exception = e;
        }
        return true;
    }
    
    private boolean isOutbound(MessageContext context) {
        return context.get(
            context.MESSAGE_OUTBOUND_PROPERTY).equals(Boolean.TRUE);
    }
    
    public boolean handleFault(SOAPMessageContext context) {
        return true;
    }
    
    public void close(MessageContext context) {}
    
}
