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
package org.mule.routing.inbound;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mule.management.stats.RouterStatistics;
import org.mule.umo.UMOEvent;
import org.mule.umo.UMOFilter;
import org.mule.umo.MessagingException;
import org.mule.umo.routing.RoutingException;
import org.mule.umo.routing.UMOInboundRouter;
import org.mule.umo.transformer.TransformerException;
import org.mule.config.i18n.Message;
import org.mule.config.i18n.Messages;

/**
 * <code>SelectiveConsumer</code> is an inbound router used to filter out unwanted events.
 * The filtering is performed by a <code>UMOFilter</code> that can be set on the router. If the event
 * does not match the filter a <code>UMOROutnerCatchAllStrategy</code> can be set on this router
 * to route unwanted events.  If a catch strategy is not set the router just returns null.
 *
 * @author <a href="mailto:ross.mason@cubis.co.uk">Ross Mason</a>
 * @version $Revision$
 */

public class SelectiveConsumer implements UMOInboundRouter
{
    /**
     * logger used by this class
     */
    protected transient Log logger = LogFactory.getLog(getClass());

    private UMOFilter filter;
    private boolean transformFirst = true;

    private RouterStatistics routerStatistics;

    public boolean isMatch(UMOEvent event) throws MessagingException
    {
        if(filter==null) return true;
        Object payload = null;
        if(transformFirst) {
            try
            {
                payload = event.getTransformedMessage();
            } catch (TransformerException e)
            {
                throw new RoutingException(new Message(Messages.TRANSFORM_FAILED_BEFORE_FILTER), event.getMessage(), event.getEndpoint(), e);
            }
        } else {
            payload = event.getMessage().getPayload();
        }
        return filter.accept(payload);
    }

    public UMOEvent[] process(UMOEvent event) throws MessagingException
    {
        if(isMatch(event)) {
            return new UMOEvent[]{event};
        } else {
            return null;
        }
    }

    public UMOFilter getFilter()
    {
        return filter;
    }

    public void setFilter(UMOFilter filter)
    {
        this.filter = filter;
    }

    public boolean isTransformFirst()
    {
        return transformFirst;
    }

    public void setTransformFirst(boolean transformFirst)
    {
        this.transformFirst = transformFirst;
    }

    public void setRouterStatistics(RouterStatistics stats)
    {
        this.routerStatistics = stats;
    }

    public RouterStatistics getRouterStatistics()
    {
        return routerStatistics;
    }
}
