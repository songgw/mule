/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.oauth2.internal;

import org.mule.api.DefaultMuleException;
import org.mule.api.MuleContext;
import org.mule.api.MuleEvent;
import org.mule.api.MuleException;
import org.mule.api.context.MuleContextAware;
import org.mule.api.lifecycle.Initialisable;
import org.mule.api.lifecycle.InitialisationException;
import org.mule.api.processor.MessageProcessor;
import org.mule.config.i18n.CoreMessages;
import org.mule.module.oauth2.internal.state.UserOAuthState;
import org.mule.util.AttributeEvaluator;

/**
 * Stores oauth state for a particular oauth state id.
 */
public class StoreAuthenticationCodeStateMessageProcessor implements MessageProcessor, Initialisable, MuleContextAware
{

    private AuthorizationCodeGrantType config;
    private AttributeEvaluator oauthStateIdEvaluator;
    private MuleContext muleContext;
    private AttributeEvaluator accessTokenEvaluator;
    private AttributeEvaluator refreshTokenEvaluator;
    private AttributeEvaluator expiresInEvaluator;

    public void setConfig(AuthorizationCodeGrantType config)
    {
        this.config = config;
    }

    /**
     * @param oauthStateId static value or expression that resolved an oauth state id
     */
    public void setOauthStateId(String oauthStateId)
    {
        this.oauthStateIdEvaluator = new AttributeEvaluator(oauthStateId);
    }

    /**
     * @param accessToken a valid access token to use for authentication
     */
    public void setAccessToken(String accessToken)
    {
        this.accessTokenEvaluator = new AttributeEvaluator(accessToken);
    }

    /**
     * @param refreshToken a valid refresh token to use for retrieving a new access token
     */
    public void setRefreshToken(String refreshToken)
    {
        this.refreshTokenEvaluator = new AttributeEvaluator(refreshToken);
    }

    /**
     * @param expiresIn time expiration for the access token
     */
    public void setExpiresIn(String expiresIn)
    {
        this.expiresInEvaluator = new AttributeEvaluator(expiresIn);
    }

    @Override
    public MuleEvent process(MuleEvent event) throws MuleException
    {
        final String oauthStateId = oauthStateIdEvaluator.resolveStringValue(event);
        if (oauthStateId == null)
        {
            throw new DefaultMuleException(CoreMessages.createStaticMessage(String.format("oauthStateId cannot be null. Following expression return null %s", oauthStateIdEvaluator.getRawValue())));
        }
        final String accessToken = accessTokenEvaluator.resolveStringValue(event);
        if (accessToken == null)
        {
            throw new DefaultMuleException(CoreMessages.createStaticMessage(String.format("accessToken cannot be null. Following expression return null %s", accessTokenEvaluator.getRawValue())));
        }
        final UserOAuthState userState = config.getOAuthState().getStateForUser(oauthStateId);
        userState.setAccessToken(accessToken);
        if (refreshTokenEvaluator != null)
        {
            userState.setRefreshToken(refreshTokenEvaluator.resolveStringValue(event));
        }
        if (expiresInEvaluator != null)
        {
            userState.setExpiresIn(expiresInEvaluator.resolveStringValue(event));
        }
        return event;
    }

    @Override
    public void initialise() throws InitialisationException
    {
        if (oauthStateIdEvaluator == null)
        {
            oauthStateIdEvaluator = new AttributeEvaluator(UserOAuthState.DEFAULT_USER_ID);
        }
        if (accessTokenEvaluator == null)
        {
            throw new InitialisationException(CoreMessages.createStaticMessage("accessToken must be configured"), this);
        }
        initialiseAttributeEvaluator(oauthStateIdEvaluator);
        initialiseAttributeEvaluator(accessTokenEvaluator);
        initialiseAttributeEvaluator(refreshTokenEvaluator);
        initialiseAttributeEvaluator(expiresInEvaluator);
    }

    private void initialiseAttributeEvaluator(AttributeEvaluator attributeEvaluator)
    {
        if (attributeEvaluator != null)
        {
            attributeEvaluator.initialize(muleContext.getExpressionManager());
        }
    }

    @Override
    public void setMuleContext(MuleContext context)
    {
        this.muleContext = context;
    }
}
