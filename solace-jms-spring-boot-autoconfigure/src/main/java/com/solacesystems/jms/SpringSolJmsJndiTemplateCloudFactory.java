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

package com.solacesystems.jms;

import java.util.List;

import com.solace.services.loader.model.SolaceServiceCredentials;
import org.springframework.jndi.JndiTemplate;

/**
 * A Factory for {@link JndiTemplate} to Support Cloud Environments having
 * multiple solace-messaging services.
 */
public interface SpringSolJmsJndiTemplateCloudFactory<T extends SolaceServiceCredentials> {

	/**
	 * Gets the first detected {@link SolaceServiceCredentials}.
	 *
	 * @return A Solace Messaging service
	 */
	T findFirstSolaceServiceCredentials();

	/**
	 * Lists All Cloud Environment detected Solace Messaging services.
	 *
	 * @return List of all Cloud Environment detected Solace Messaging services
	 */
	List<T> getSolaceServiceCredentials();

	/**
	 * Returns a {@link JndiTemplate} based on the first detected {@link SolaceServiceCredentials}.
	 *
	 * @return {@link JndiTemplate} based on the first detected {@link SolaceServiceCredentials}
	 */
	JndiTemplate getJndiTemplate();

	/**
	 * Returns a {@link JndiTemplate} based on the {@link SolaceServiceCredentials}
	 * identified by the given ID.
	 *
	 * @param id The Solace Messaging service's ID
	 * @return {@link JndiTemplate} based on the specified Solace Messaging service
	 */
	JndiTemplate getJndiTemplate(String id);

	/**
	 * Returns a {@link JndiTemplate} based on the given {@link SolaceServiceCredentials}.
	 *
	 * @param solaceServiceCredentials The credentials to an existing Solace Messaging service
	 * @return {@link JndiTemplate} based on the given {@link SolaceServiceCredentials}
	 */
	JndiTemplate getJndiTemplate(SolaceServiceCredentials solaceServiceCredentials);

}
