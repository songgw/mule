/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.expression;

import org.mule.DefaultMuleMessage;
import org.mule.api.MuleContext;
import org.mule.api.MuleMessage;
import org.mule.api.expression.RequiredValueException;
import org.mule.tck.junit4.AbstractMuleTestCase;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;

public class MapPayloadExpressionEvaluatorTestCase extends AbstractMuleTestCase
{

    private Map<String, String> props = new HashMap<String, String>(3);
    private MuleContext muleContext = mock(MuleContext.class);

    @Before
    public void setUpProperties()
    {
        props.clear();
        props.put("foo", "moo");
        props.put("bar", "mar");
        props.put("ba?z", "maz");
    }

    @Test
    public void testExpressions() throws Exception
    {
        MapPayloadExpressionEvaluator eval = new MapPayloadExpressionEvaluator();
        MuleMessage message = new DefaultMuleMessage(props, (Map) null, muleContext);

        // direct match
        Object result = eval.evaluate("foo", message);
        assertEquals("moo", result);

        // direct match, optional
        result = eval.evaluate("bar?", message);
        assertEquals("mar", result);

        // direct match with * inline
        result = eval.evaluate("ba?z", message);
        assertEquals("maz", result);

        // no match, optional
        result = eval.evaluate("fool?", message);
        assertNull(result);

        try
        {
            // no match, required
            eval.evaluate("fool", message);
            fail("Should've failed with an exception.");
        }
        catch (RequiredValueException rex)
        {
            // expected
            assertTrue(rex.getMessage().contains("fool"));
        }
    }
    
    @Test
    public void testMultipleExpressions() throws Exception
    {
        MapPayloadExpressionEvaluator eval = new MapPayloadExpressionEvaluator();
        MuleMessage message = new DefaultMuleMessage(props, (Map) null, muleContext);

        // direct match
        Object result = eval.evaluate("foo,bar?,ba?z,fool?", message);        
        assertNotNull(result);
        assertTrue(result instanceof Map);
        assertEquals(3, ((Map)result).size());

        assertEquals("moo", ((Map)result).get("foo"));
        assertEquals("mar", ((Map)result).get("bar"));
        assertEquals("maz", ((Map)result).get("ba?z"));
        assertNull(((Map)result).get("fool?"));        
    }    

}
