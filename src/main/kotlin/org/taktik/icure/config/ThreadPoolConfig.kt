/*
 *  iCure Data Stack. Copyright (c) 2020 Taktik SA
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Affero General Public License as
 *     published by the Free Software Foundation, either version 3 of the
 *     License, or (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful, but
 *     WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *     Affero General Public License for more details.
 *
 *     You should have received a copy of the GNU Affero General Public
 *     License along with this program.  If not, see
 *     <https://www.gnu.org/licenses/>.
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
		corePoolSize = 8
		maxPoolSize = 24
		setThreadNamePrefix("default_task_executor_thread_pool")
		initialize()
	}

	@Bean
	fun threadPoolTaskScheduler(): TaskScheduler = ThreadPoolTaskScheduler().apply {
		setThreadNamePrefix("default_task_scheduler_thread_pool")
		initialize()
	}
}
