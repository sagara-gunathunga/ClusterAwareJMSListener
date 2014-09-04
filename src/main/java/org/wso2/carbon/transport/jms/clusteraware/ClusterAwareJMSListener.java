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

import org.apache.axis2.AxisFault;
import org.apache.axis2.transport.jms.AxisJMSException;
import org.apache.axis2.transport.jms.JMSEndpoint;
import org.apache.axis2.transport.jms.JMSListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.core.clustering.api.CoordinatedActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * In a clustered Carbon environment this allows only cluster-coordinator to make a connection
 * with JMS broker. When coordinator node get change this class can detect the change and allows
 * new coordinator to make JMS connection. If the current node is not cluster-coordinator this
 * keeps JMSEndpoint in a list to be used in future in case if this node become the cluster-coordinator.
 * This is useful in situation where all JMS subscribers try to subscribe with same DurableSubscriberClientID.
 */
public class ClusterAwareJMSListener extends JMSListener implements CoordinatedActivity {

    private static final Log log = LogFactory.getLog(ClusterAwareJMSListener.class);

    private List<JMSEndpoint> endpointList = new ArrayList<JMSEndpoint>();

    //By default all nodes are not coordinators till someone get elected, hence set to false.
    private boolean currentCoordinator = false;

    @Override
    protected void startEndpoint(JMSEndpoint endpoint) throws AxisFault {
        boolean debug = log.isDebugEnabled();
        if (currentCoordinator) {
            if (debug) {
                log.debug(endpoint.getServiceName() + "start now ! ");
            }
            super.startEndpoint(endpoint);

        } else {
            if (debug) {
                log.debug(endpoint.getServiceName() + " will add to endpointList, now start now ");
            }
            endpointList.add(endpoint);
        }
    }

    public void startEndpoints() {
        log.debug(" Start adding endpoints in endpointList ");
        for (JMSEndpoint ep : endpointList) {
            try {
                super.startEndpoint(ep);
                log.info(ep.getServiceName() + " started ! ");
            } catch (AxisFault axisFault) {
                log.error(axisFault);
                //AxisJMSException is not public hence directly returns RuntimeException.
               throw  new RuntimeException(axisFault);
            }
        }
        endpointList.clear();
    }


    // If this is the cluster-coordinator allows to establish JMS connections.
    public void execute() {
        currentCoordinator = true;
        startEndpoints();
    }
}