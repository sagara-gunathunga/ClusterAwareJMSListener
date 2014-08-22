ClusterAwareJMSListener
=======================



Configuration
=======================

1. Copy ClusterAwareJMSListener-X.jar file into "CARBON-HOME/repository/components/dropins" directory. 


2. Modify "CARBON-HOME/repository/conf/axis2/axis2.xml" file. 

```xml
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
```

Note : only addional paramters have listed above. 

3. Restart the server 
