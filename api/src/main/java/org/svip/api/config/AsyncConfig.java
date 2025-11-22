/**
 * Copyright 2021 Rochester Institute of Technology (RIT). Developed with
 * government support under contract 70RCSA22C00000008 awarded by the United
 * States Department of Homeland Security for Cybersecurity and Infrastructure Security Agency.
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package org.svip.api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * File: AsyncConfig.java
 * Configuration for asynchronous task execution, specifically for parallel vulnerability scanning
 *
 * @author Ibrahim Matar
 */
@Configuration
@EnableAsync
public class AsyncConfig {
    
    /**
     * Executor service for parallel vulnerability scanning
     * Uses virtual threads (Java 21+) if available, otherwise fixed thread pool
     *
     * @return Executor for vulnerability scanning tasks
     */
    @Bean(name = "vulnerabilityScanExecutor")
    public Executor vulnerabilityScanExecutor() {
        // Use virtual threads if available (Java 21+), otherwise use fixed thread pool
        try {
            // Try to use virtual threads (more efficient for I/O-bound tasks like scanning)
            return Executors.newVirtualThreadPerTaskExecutor();
        } catch (UnsupportedOperationException e) {
            // Fall back to fixed thread pool for Java 17/19
            return Executors.newFixedThreadPool(4);
        }
    }
}



