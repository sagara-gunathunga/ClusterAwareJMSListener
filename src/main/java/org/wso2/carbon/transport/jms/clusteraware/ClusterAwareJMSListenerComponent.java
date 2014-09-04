package org.wso2.carbon.transport.jms.clusteraware;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.MembershipListener;
import org.apache.axis2.Constants;
import org.apache.axis2.clustering.ClusteringAgent;
import org.apache.axis2.context.ConfigurationContext;
import org.apache.axis2.description.TransportInDescription;
import org.apache.axis2.engine.ListenerManager;
import org.apache.axis2.transport.TransportListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osgi.service.component.ComponentContext;
import org.wso2.carbon.utils.ConfigurationContextService;

/**
 * @scr.component  name="org.wso2.carbon.transport.jms.clusteraware.ClusterAwareJMSListenerComponent"
 *                              immediate="true"
 * @scr.reference  name="hazelcast.instance.service"
 *                              interface="com.hazelcast.core.HazelcastInstance"
 *                              cardinality="1..1"
 *                              policy="dynamic"
 *                              bind="setHazelcastInstance"
 *                              unbind="unsetHazelcastInstance"
 * @scr.reference  name="config.context.service"
 *                              interface="org.wso2.carbon.utils.ConfigurationContextService"
 *                              cardinality="1..1" policy="dynamic"
 *                              bind="setConfigurationContextService"
 *                              unbind="unsetConfigurationContextService"
 * @scr.reference  name="ListenerManager.component"
 *                              interface="org.apache.axis2.engine.ListenerManager"
 *                              cardinality="1..1"
 *                              policy="dynamic"
 *                              bind="setListenerManager"
 *                              unbind="unSetListenerManager"
 */
public class ClusterAwareJMSListenerComponent {

    private static final Log log = LogFactory.getLog(ClusterAwareJMSListenerComponent.class);

    private HazelcastInstance hazelcastInstance;
    private ConfigurationContext configurationContext;
    private ClusteringAgent agent;

    protected void activate(ComponentContext ctx) {

        log.debug("in activate method");
        if (configurationContext != null) {
            TransportInDescription jmsIn = configurationContext.getAxisConfiguration().getTransportIn(Constants.TRANSPORT_JMS);
            if (jmsIn != null) {
                TransportListener listener = jmsIn.getReceiver();
                if (listener != null && listener instanceof ClusterAwareJMSListener) {
                    if (isCurrentCoordinator()) {
                        log.debug("This member is the current coordinator");
                        ClusterAwareJMSListener clusterAwareJMSListener = (ClusterAwareJMSListener) listener;
                        clusterAwareJMSListener.setCurrentCoordinator(true);
                        clusterAwareJMSListener.startEndpoints();
                    } else {
                        log.debug("This member is not current coordinator");
                        hazelcastInstance.getCluster().addMembershipListener((MembershipListener) listener);
                    }
                }
            }
        }
    }

    protected void deactivate(ComponentContext ctx) {
    }

    public boolean isCurrentCoordinator() {
        return agent.isCoordinator();
    }

    protected void setHazelcastInstance(HazelcastInstance hazelcastInstance) {
        this.hazelcastInstance = hazelcastInstance;
    }

    protected void unsetHazelcastInstance(HazelcastInstance hazelcastInstance) {
    }

    protected void setConfigurationContextService(ConfigurationContextService configurationContextService) {
        this.configurationContext = configurationContextService.getServerConfigContext();
        agent = configurationContextService.getServerConfigContext().getAxisConfiguration().getClusteringAgent();
        ClusterAwareJMSDataHolder.getInstance().setClusteringAgent(agent);
    }

    protected void unsetConfigurationContextService(ConfigurationContextService configurationContextService) {
        ClusterAwareJMSDataHolder.getInstance().setClusteringAgent(null);

    }

    protected void setListenerManager(ListenerManager listenerManager) {
    }

    protected void unSetListenerManager(ListenerManager listenerManager) {
    }
}