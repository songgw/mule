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
 *
 */
package org.mule.providers.jms;

import org.mule.umo.MessagingException;

import javax.jms.Message;
import javax.jms.Session;

/**
 * @author <a href="mailto:ross.mason@cubis.co.uk">Ross Mason</a>
 * @version $Revision$
 */
public class MessageRedeliveredException extends MessagingException
{
    private Session session;

    public MessageRedeliveredException(Message jmsMessage, Session session)
    {
        super(new org.mule.config.i18n.Message("jms", 7), jmsMessage);
        this.session = session;
    }

    public Session getSession()
    {
        return session;
    }
}