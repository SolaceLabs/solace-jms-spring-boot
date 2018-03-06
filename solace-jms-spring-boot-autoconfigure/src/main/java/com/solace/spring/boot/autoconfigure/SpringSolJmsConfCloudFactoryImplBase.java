/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
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
package com.solace.spring.boot.autoconfigure;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.Cloud;
import org.springframework.cloud.CloudFactory;
import org.springframework.cloud.service.ServiceInfo;
import org.springframework.context.annotation.Bean;
import com.solace.spring.cloud.core.SolaceMessagingInfo;

abstract class SpringSolJmsConfCloudFactoryImplBase extends SpringSolJmsConfImplBase {

	private static final Logger logger = LoggerFactory.getLogger(SpringSolJmsConfCloudFactoryImplBase.class);

	private CloudFactory cloudFactory = new CloudFactory();

	SpringSolJmsConfCloudFactoryImplBase(SolaceJmsProperties properties) {
		super(properties);
	}

	@Bean
	public List<SolaceMessagingInfo> getSolaceMessagingInfos() {
		List<SolaceMessagingInfo> solaceMessagingInfoList = new ArrayList<>();
		Cloud cloud = cloudFactory.getCloud();

		List<ServiceInfo> serviceInfos = cloud.getServiceInfos();
		for (ServiceInfo serviceInfo : serviceInfos) {
			if (serviceInfo instanceof SolaceMessagingInfo) {
				solaceMessagingInfoList.add((SolaceMessagingInfo) serviceInfo);
			}
		}
		return solaceMessagingInfoList;
	}

	@Bean
	public SolaceMessagingInfo findFirstSolaceMessagingInfo() {
		SolaceMessagingInfo solacemessaging = null;
		Cloud cloud = cloudFactory.getCloud();
		List<ServiceInfo> serviceInfos = cloud.getServiceInfos();
		for (ServiceInfo serviceInfo : serviceInfos) {
			// Stop when we find the first one...
			// TODO: Consider annotation driven selection, or sorted plan based
			// selection
			if (serviceInfo instanceof SolaceMessagingInfo) {
				solacemessaging = (SolaceMessagingInfo) serviceInfo;
				logger.info("Found Cloud Solace Messaging Service Instance Id: " + solacemessaging.getId());
				break;
			}
		}

		if (solacemessaging == null) {
			// The CloudCondition should shield from this happening, should not
			// arrive to this state.
			logger.error("Cloud Solace Messaging Info was not found, cannot auto-configure");
			throw new IllegalStateException(
					"Unable to create SolConnectionFactory did not find SolaceMessagingInfo in the current cloud environment");
		}

		return solacemessaging;
	}
}