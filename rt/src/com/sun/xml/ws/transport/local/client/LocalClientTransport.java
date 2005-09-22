/*
 * $Id: LocalClientTransport.java,v 1.10 2005-09-22 20:02:55 spericas Exp $
 */

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

package com.sun.xml.ws.transport.local.client;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import com.sun.xml.ws.client.ClientTransportException;
import com.sun.xml.ws.handler.MessageContextImpl;
import com.sun.xml.ws.server.RuntimeEndpointInfo;
import com.sun.xml.ws.server.Tie;
import com.sun.xml.ws.spi.runtime.WSConnection;
import com.sun.xml.ws.spi.runtime.WebServiceContext;
import com.sun.xml.ws.transport.WSConnectionImpl;
import com.sun.xml.ws.util.exception.LocalizableExceptionAdapter;
import com.sun.xml.ws.util.localization.Localizable;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;
import com.sun.xml.ws.transport.local.server.LocalConnectionImpl;
import com.sun.xml.ws.transport.local.LocalMessage;

import static com.sun.xml.ws.developer.JAXWSProperties.CONTENT_NEGOTIATION_PROPERTY;

/**
 * @author WS Development Team
 */
public class LocalClientTransport extends WSConnectionImpl {

    private RuntimeEndpointInfo endpointInfo;
    private Tie tie = new Tie();
    LocalMessage lm = new LocalMessage();

    //this class is used primarily for debugging purposes
    public LocalClientTransport(RuntimeEndpointInfo endpointInfo) {
        this(endpointInfo, null);
    }

    public LocalClientTransport(RuntimeEndpointInfo endpointInfo,
        OutputStream logStream) {
        this.endpointInfo = endpointInfo;
        debugStream = logStream;
    }

    
    @Override
    public OutputStream getOutput() {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            lm.setOutput(baos);
            return lm.getOutput();
        }
        catch (Exception ex) {
            if (ex instanceof Localizable) {
                throw new ClientTransportException("local.client.failed",
                        (Localizable) ex);
            } else {
                throw new ClientTransportException("local.client.failed",
                        new LocalizableExceptionAdapter(ex));
            }
        }
    }

    private static void checkMessageContentType(WSConnection con, boolean response) {
        String negotiation = System.getProperty(CONTENT_NEGOTIATION_PROPERTY, "none").intern();
        String contentType = con.getHeaders().get("Content-Type").get(0);
        
        // Use indexOf() to handle Multipart/related types
        if (negotiation == "none") {
            // OK only if XML
            if (contentType.indexOf("text/xml") < 0 &&
                   contentType.indexOf("application/soap+xml") < 0 &&
                   contentType.indexOf("application/xop+xml") < 0) 
            {
                throw new RuntimeException("Invalid content type '" + contentType 
                    + "' with content negotiation set to '" + negotiation + "'.");
            }
        }
        else if (negotiation == "optimistic") {
            // OK only if FI
            if (contentType.indexOf("application/fastinfoset") < 0 && 
                   contentType.indexOf("application/soap+fastinfoset") < 0) 
            {
                throw new RuntimeException("Invalid content type '" + contentType 
                    + "' with content negotiation set to '" + negotiation + "'.");
            }
        }
        else if (negotiation == "pessimistic") {
            // OK if FI request is anything and response is FI
            if (response && 
                    contentType.indexOf("application/fastinfoset") < 0 && 
                    contentType.indexOf("application/soap+fastinfoset") < 0)
            {
                throw new RuntimeException("Invalid content type '" + contentType 
                    + "' with content negotiation set to '" + negotiation + "'.");                
            }
        }
    }
    
    @Override
    public void closeOutput() {
        super.closeOutput();
        WSConnection con = new LocalConnectionImpl(lm);
        
        // Copy headers for content negotiation
        con.setHeaders(getHeaders());
        
        // Check request content type based on negotiation property
        checkMessageContentType(this, false);
     
        try {
            // Set a MessageContext per invocation
            WebServiceContext wsContext = endpointInfo.getWebServiceContext();
            wsContext.setMessageContext(new MessageContextImpl());
            tie.handle(con, endpointInfo);
            
            checkMessageContentType(con, true);
        } 
        catch (Exception ex) {
            if (ex instanceof Localizable) {
                throw new ClientTransportException("local.client.failed",
                        (Localizable) ex);
            } else {
                throw new ClientTransportException("local.client.failed",
                        new LocalizableExceptionAdapter(ex));
            }
        }
    }
    
    @Override
    public InputStream getInput() {
        try {
            ByteArrayInputStream bis = new ByteArrayInputStream(lm.getOutput().toByteArray());
            return bis;
        } 
        catch (Exception ex) {
            if (ex instanceof Localizable) {
                throw new ClientTransportException("local.client.failed",
                        (Localizable) ex);
            } else {
                throw new ClientTransportException("local.client.failed",
                        new LocalizableExceptionAdapter(ex));
            }
        }
    }
    
    @Override
    public void setHeaders(Map<String, List<String>> headers) {
        lm.setHeaders(headers);
    }
    
    @Override
    public Map<String, List<String>> getHeaders() {
        return lm.getHeaders();
    }
    
}
