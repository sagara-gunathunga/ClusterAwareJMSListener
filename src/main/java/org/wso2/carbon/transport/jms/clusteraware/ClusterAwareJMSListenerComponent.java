/*
*  Copyright (c) 2005-2014, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
*  WSO2 Inc. licenses this file to you under the Apache License,
*  Version 2.0 (the "License"); you may not use this file except
*  in compliance with the License.
*  You may obtain a copy of the License at
*
*    http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied.  See the License for the
* specific language governing permissions and limitations
* under the License.
*/

package org.wso2.carbon.transport.jms.clusteraware;

import org.apache.axis2.Constants;
import org.apache.axis2.context.ConfigurationContext;
import org.apache.axis2.description.TransportInDescription;
import org.apache.axis2.transport.TransportListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osgi.service.component.ComponentContext;
import org.wso2.carbon.core.clustering.api.CoordinatedActivity;
import org.wso2.carbon.utils.ConfigurationContextService;

/**
 * @scr.component  name="org.wso2.carbon.transport.jms.clusteraware.ClusterAwareJMSListenerComponent"
 *                              immediate="true"
 * @scr.reference  name="config.context.service"
 *                              interface="org.wso2.carbon.utils.ConfigurationContextService"
 *                              cardinality="1..1" policy="dynamic"
 *                              bind="setConfigurationContextService"
 *                              unbind="unsetConfigurationContextService"
 */
public class ClusterAwareJMSListenerComponent {

    private static final Log log = LogFactory.getLog(ClusterAwareJMSListenerComponent.class);

    private ConfigurationContext configurationContext;

    protected void activate(ComponentContext ctx) {
        log.debug("in activate method");
        if (configurationContext != null) {
            TransportInDescription jmsIn = configurationContext.getAxisConfiguration().getTransportIn(Constants.TRANSPORT_JMS);
            if (jmsIn != null) {
                TransportListener listener = jmsIn.getReceiver();
                if (listener != null && listener instanceof CoordinatedActivity) {
                    ctx.getBundleContext().registerService(CoordinatedActivity.class.getName(), listener, null);
                }
            }
        }

    }

    protected void deactivate(ComponentContext ctx) {
    }

    protected void setConfigurationContextService(ConfigurationContextService configurationContextService) {
        this.configurationContext = configurationContextService.getServerConfigContext();
    }

    protected void unsetConfigurationContextService(ConfigurationContextService configurationContextService) {
    }

}