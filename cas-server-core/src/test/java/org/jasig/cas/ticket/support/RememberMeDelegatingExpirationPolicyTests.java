/*
 * Licensed to Apereo under one or more contributor license
 * agreements. See the NOTICE file distributed with this work
 * for additional information regarding copyright ownership.
 * Apereo licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License.  You may obtain a
 * copy of the License at the following location:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jasig.cas.ticket.support;

import org.jasig.cas.TestUtils;
import org.jasig.cas.authentication.Authentication;
import org.jasig.cas.authentication.RememberMeCredential;
import org.jasig.cas.authentication.principal.DefaultPrincipalFactory;
import org.jasig.cas.authentication.principal.PrincipalFactory;
import org.jasig.cas.ticket.TicketGrantingTicketImpl;
import org.junit.Before;
import org.junit.Test;

import javax.validation.constraints.NotNull;
import java.util.Collections;

import static org.junit.Assert.*;

/**
 * Tests for RememberMeDelegatingExpirationPolicy.
 *
 * @author Scott Battaglia
 * @since 3.2.1
 *
 */
public final class RememberMeDelegatingExpirationPolicyTests {

    /** Factory to create the principal type. **/
    @NotNull
    protected final PrincipalFactory principalFactory = new DefaultPrincipalFactory();


    private RememberMeDelegatingExpirationPolicy p;

    @Before
    public void setUp() throws Exception {
        this.p = new RememberMeDelegatingExpirationPolicy();
        this.p.setRememberMeExpirationPolicy(new MultiTimeUseOrTimeoutExpirationPolicy(1, 20000));
        this.p.setSessionExpirationPolicy(new MultiTimeUseOrTimeoutExpirationPolicy(5, 20000));
    }

    @Test
    public void verifyTicketExpirationWithRememberMe() {
        final Authentication authentication = TestUtils.getAuthentication(
                this.principalFactory.createPrincipal("test"),
                Collections.<String, Object>singletonMap(
                        RememberMeCredential.AUTHENTICATION_ATTRIBUTE_REMEMBER_ME, true));
        final TicketGrantingTicketImpl t = new TicketGrantingTicketImpl("111", authentication, this.p);
        assertFalse(t.isExpired());
        t.grantServiceTicket("55", TestUtils.getService(), this.p, false, true);
        assertTrue(t.isExpired());

    }

    @Test
    public void verifyTicketExpirationWithoutRememberMe() {
        final Authentication authentication = TestUtils.getAuthentication();
        final TicketGrantingTicketImpl t = new TicketGrantingTicketImpl("111", authentication, this.p);
        assertFalse(t.isExpired());
        t.grantServiceTicket("55", TestUtils.getService(), this.p, false, true);
        assertFalse(t.isExpired());

    }
}
