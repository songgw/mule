/*
 * $Header$
 * $Revision$
 * $Date$
 * ------------------------------------------------------------------------------------------------------
 *
 * Copyright (c) Cubis Limited. All rights reserved.
 * http://www.cubis.co.uk
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */

package org.mule.umo.routing;

import org.mule.umo.MessagingException;
import org.mule.umo.UMOMessage;
import org.mule.umo.endpoint.UMOEndpoint;
import org.mule.config.i18n.Message;
import org.mule.config.i18n.Messages;

/**
 * <code>RoutingException</code> is a base class for al routing exceptions.  Routing
 * exceptions are only thrown for InboundMessageRouter and OutboundMessageRouter and
 * deriving types.  Mule itself does not throw routing exceptions when routing internal
 * events.
 *
 * @author <a href="mailto:ross.mason@cubis.co.uk">Ross Mason</a>
 * @version $Revision$
 */
public class RoutingException extends MessagingException
{
    protected transient UMOEndpoint endpoint;

    public RoutingException(UMOMessage message, UMOEndpoint endpoint)
    {
        super(generateMessage(null, endpoint), message);
        this.endpoint = endpoint;
    }

    public RoutingException(UMOMessage umoMessage, UMOEndpoint endpoint, Throwable cause)
    {
        super(generateMessage(null, endpoint), umoMessage, cause);
        this.endpoint = endpoint;
    }

    public RoutingException(Message message, UMOMessage umoMessage, UMOEndpoint endpoint)
    {
        super(generateMessage(message, endpoint), umoMessage);
        this.endpoint = endpoint;
    }

    public RoutingException(Message message, UMOMessage umoMessage, UMOEndpoint endpoint, Throwable cause)
    {
        super(generateMessage(message, endpoint), umoMessage, cause);
        this.endpoint = endpoint;
    }

    public UMOEndpoint getEndpoint()
    {
        return endpoint;
    }

     private static Message generateMessage(Message message, UMOEndpoint endpoint) {
         Message m = new Message(Messages.FAILED_TO_ROUTER_VIA_ENDPOINT, endpoint);
         if(message!=null) {
             message.setNextMessage(m);
             return message;
         } else {
             return m;
         }
    }
}
