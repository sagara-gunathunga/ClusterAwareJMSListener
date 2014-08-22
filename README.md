ClusterAwareJMSListener
=======================



Configuration
=======================

 <transportReceiver name="jms" class="org.wso2.carbon.transport.jms.clusteraware.ClusterAwareJMSListener">
       <parameter name="myTopicConnectionFactory" locked="false">
           <parameter name="transport.jms.coordinatorElectionDelay">3000</parameter>

       </parameter>
  
       <parameter name="myQueueConnectionFactory" locked="false">
           <parameter name="transport.jms.coordinatorElectionDelay">3000</parameter>

       </parameter>
  
       <parameter name="default" locked="false">
           <parameter name="transport.jms.coordinatorElectionDelay">3000</parameter>
       </parameter>
   </transportReceiver>
