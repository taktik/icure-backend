/*
 * Copyright (C) 2018 Taktik SA
 *
 * This file is part of iCureBackend.
 *
 * Foobar is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 *
 * Foobar is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with iCureBackend.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.taktik.icure.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.task.TaskExecutor
import org.springframework.scheduling.TaskScheduler
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler


@Configuration
class ThreadPoolConfig {
	@Bean
	fun threadPoolTaskExecutor(): TaskExecutor = ThreadPoolTaskExecutor().apply {
		corePoolSize = 4
		maxPoolSize = 4
		threadNamePrefix = "default_task_executor_thread_pool"
		initialize()
	}

	@Bean
	fun threadPoolTaskScheduler(): TaskScheduler  = ThreadPoolTaskScheduler(). apply {
		threadNamePrefix = "default_task_scheduler_thread_pool"
		initialize()
	}
}