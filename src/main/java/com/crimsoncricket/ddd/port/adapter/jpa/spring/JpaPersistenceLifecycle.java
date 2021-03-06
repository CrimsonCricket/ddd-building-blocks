/*
 * Copyright 2016 Martijn van der Woud - The Crimson Cricket Internet Services
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.crimsoncricket.ddd.port.adapter.jpa.spring;

import com.crimsoncricket.ddd.application.PersistenceLifeCycle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.orm.jpa.EntityManagerHolder;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import static com.crimsoncricket.asserts.Assert.assertArgumentNotNull;

public class JpaPersistenceLifecycle implements PersistenceLifeCycle {

	private static final Logger logger = LoggerFactory.getLogger(JpaPersistenceLifecycle.class);

	private final PlatformTransactionManager transactionManager;

	private final ThreadLocal<TransactionStatus> transactionStatusHolder = new ThreadLocal<TransactionStatus>();

	private final EntityManagerFactory entityManagerFactory;

	public JpaPersistenceLifecycle(
			PlatformTransactionManager transactionManager,
			EntityManagerFactory entityManagerFactory
	) {
		assertArgumentNotNull(transactionManager, "The transaction manager may not be null.");
		assertArgumentNotNull(entityManagerFactory, "The entity manager factory may not be null");
		this.transactionManager = transactionManager;
		this.entityManagerFactory = entityManagerFactory;
	}

	@Override
	public void begin() {
		logger.debug("Starting JPA transaction");
		try {
			DefaultTransactionDefinition definition = new DefaultTransactionDefinition();
			definition.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
			definition.setIsolationLevel(TransactionDefinition.ISOLATION_REPEATABLE_READ);
			TransactionStatus transactionStatus = transactionManager.getTransaction(definition);
			transactionStatusHolder.set(transactionStatus);
			logger.debug("Started JPA transaction");
		} catch (Exception e) {
			logger.error("Error starting JPA transaction", e);
			throw new RuntimeException(e);
		}
	}

	@Override
	public void commit() {
		transactionManager.commit(transactionStatusHolder.get());
		logger.debug("Committed JPA transaction");
	}

	@Override
	public void rollback() {
		TransactionStatus transactionStatus = transactionStatusHolder.get();
		if (transactionStatus.isCompleted()) {
			logger.debug("Skipping rollback of transaction. Transaction is already completed. " +
					"Presumably, a rollback already occurred" +
					"as a result of a runtime exception during transaction commit");
			return;
		}
		transactionManager.rollback(transactionStatus);
		logger.debug("Rolled back transaction");
	}

	@Override
	public void flush() {
		entityManager().flush();
	}

	private EntityManager entityManager() {
		EntityManagerHolder entityManagerHolder =
				(EntityManagerHolder) TransactionSynchronizationManager.getResource(entityManagerFactory);
		assert entityManagerHolder != null;
		return entityManagerHolder.getEntityManager();
	}

}
