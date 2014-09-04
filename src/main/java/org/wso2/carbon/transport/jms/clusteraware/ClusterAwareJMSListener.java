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

import com.hazelcast.core.MembershipEvent;
import com.hazelcast.core.MembershipListener;
import org.apache.axis2.AxisFault;
import org.apache.axis2.clustering.ClusteringAgent;
import org.apache.axis2.transport.jms.JMSEndpoint;
import org.apache.axis2.transport.jms.JMSListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

public class ClusterAwareJMSListener extends JMSListener implements MembershipListener {

    private static final Log log = LogFactory.getLog(ClusterAwareJMSListener.class);

    private List<JMSEndpoint> endpointList = new ArrayList<JMSEndpoint>();
    private static final int DEFAULT_COORDINATOR_ELECTION_DELAY = 60000;
    private static final String COORDINATOR_ELECTION_DELAY_PROPERTY_NAME = "transport.jms.coordinatorElectionDelay";
    private boolean currentCoordinator = false;
    private int coordinatorElectionDelay = -1;


    @Override
    protected void startEndpoint(JMSEndpoint endpoint) throws AxisFault {
        setCoordinatorElectionDelay(endpoint);
        if (currentCoordinator) {
            if (log.isDebugEnabled()) {
                log.debug(endpoint.getServiceName() + "start now ! ");
            }
            super.startEndpoint(endpoint);
        } else {
            if (log.isDebugEnabled()) {
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


    public void memberRemoved(MembershipEvent membershipEvent) {
        // Set delay due to it take some times to zookeeper to sync data such as global queue when member left cluster
        try {
            Thread.sleep(coordinatorElectionDelay);
        } catch (InterruptedException ignore) {

        }
        ClusteringAgent agent = ClusterAwareJMSDataHolder.getInstance().getClusteringAgent();
        if (agent.isCoordinator()) {
            startEndpoints();
            setCurrentCoordinator(true);
        }
    }

    public void setCurrentCoordinator(boolean currentCoordinator) {
        this.currentCoordinator = currentCoordinator;
    }

    public void setCoordinatorElectionDelay(JMSEndpoint ep) {
        if (-1 == coordinatorElectionDelay) {
            Hashtable<String, String> properties = ep.getServiceTaskManager().getJmsProperties();
            String delayStr = properties.get(COORDINATOR_ELECTION_DELAY_PROPERTY_NAME);
            if (delayStr != null && !"".equals(delayStr)) {
                this.coordinatorElectionDelay = Integer.valueOf(delayStr.trim());
            } else {
                this.coordinatorElectionDelay = DEFAULT_COORDINATOR_ELECTION_DELAY;
            }
            log.info("coordinatorElectionDelay value set to " + coordinatorElectionDelay);
        }
    }
}