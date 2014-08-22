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

3. Restart the server.


See Also
=========
[Axis2 JMS Transport](https://axis.apache.org/axis2/java/transports/jms.html)

[WSO2 ESB JMS](https://docs.wso2.com/display/ESB470/JMS+Usecases)


License
========
Released under version 2.0 of the Apache License.



Copyright (c) 2005-2011, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.

WSO2 Inc. licenses this file to you under the Apache License,
Version 2.0 (the "License"); you may not use this file except
in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing,
software distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
KIND, either express or implied.  See the License for the
specific language governing permissions and limitations
under the License.

