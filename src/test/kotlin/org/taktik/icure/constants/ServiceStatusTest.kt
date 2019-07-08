package org.taktik.icure.constants

import org.junit.Assert.*
import org.junit.Test

class ServiceStatusTest {

    @Test
    fun isActive() {
        assertTrue(ServiceStatus.isActive(null, 0))
        assertTrue(ServiceStatus.isActive(0))
        assertFalse(ServiceStatus.isActive(1))
        assertTrue(ServiceStatus.isActive(2))
        assertFalse(ServiceStatus.isActive(3))
        assertTrue(ServiceStatus.isActive(4))
        assertFalse(ServiceStatus.isActive(5))
        assertTrue(ServiceStatus.isActive(6))
        assertFalse(ServiceStatus.isActive(7))
        assertFalse(ServiceStatus.isActive(null, 7))
    }

    @Test
    fun isInactive() {
        assertFalse(ServiceStatus.isInactive(null, 0))
        assertFalse(ServiceStatus.isInactive(0))
        assertTrue(ServiceStatus.isInactive(1))
        assertFalse(ServiceStatus.isInactive(2))
        assertTrue(ServiceStatus.isInactive(3))
        assertFalse(ServiceStatus.isInactive(4))
        assertTrue(ServiceStatus.isInactive(5))
        assertFalse(ServiceStatus.isInactive(6))
        assertTrue(ServiceStatus.isInactive(7))
        assertTrue(ServiceStatus.isInactive(null, 7))
    }

    @Test
    fun isRelevant() {
        assertTrue(ServiceStatus.isRelevant(null, 0))
        assertTrue(ServiceStatus.isRelevant(0))
        assertTrue(ServiceStatus.isRelevant(1))
        assertFalse(ServiceStatus.isRelevant(2))
        assertFalse(ServiceStatus.isRelevant(3))
        assertTrue(ServiceStatus.isRelevant(4))
        assertTrue(ServiceStatus.isRelevant(5))
        assertFalse(ServiceStatus.isRelevant(6))
        assertFalse(ServiceStatus.isRelevant(7))
        assertFalse(ServiceStatus.isRelevant(null, 7))
    }

    @Test
    fun isIrrelevant() {
        assertFalse(ServiceStatus.isIrrelevant(null, 0))
        assertFalse(ServiceStatus.isIrrelevant(0))
        assertFalse(ServiceStatus.isIrrelevant(1))
        assertTrue(ServiceStatus.isIrrelevant(2))
        assertTrue(ServiceStatus.isIrrelevant(3))
        assertFalse(ServiceStatus.isIrrelevant(4))
        assertFalse(ServiceStatus.isIrrelevant(5))
        assertTrue(ServiceStatus.isIrrelevant(6))
        assertTrue(ServiceStatus.isIrrelevant(7))
        assertTrue(ServiceStatus.isIrrelevant(null, 7))
    }

    @Test
    fun isPresent() {
        assertTrue(ServiceStatus.isPresent(null, 0))
        assertTrue(ServiceStatus.isPresent(0))
        assertTrue(ServiceStatus.isPresent(1))
        assertTrue(ServiceStatus.isPresent(2))
        assertTrue(ServiceStatus.isPresent(3))
        assertFalse(ServiceStatus.isPresent(4))
        assertFalse(ServiceStatus.isPresent(5))
        assertFalse(ServiceStatus.isPresent(6))
        assertFalse(ServiceStatus.isPresent(7))
        assertFalse(ServiceStatus.isPresent(null, 7))
    }

    @Test
    fun isAbsent() {
        assertFalse(ServiceStatus.isAbsent(null, 0))
        assertFalse(ServiceStatus.isAbsent(0))
        assertFalse(ServiceStatus.isAbsent(1))
        assertFalse(ServiceStatus.isAbsent(2))
        assertFalse(ServiceStatus.isAbsent(3))
        assertTrue(ServiceStatus.isAbsent(4))
        assertTrue(ServiceStatus.isAbsent(5))
        assertTrue(ServiceStatus.isAbsent(6))
        assertTrue(ServiceStatus.isAbsent(7))
        assertTrue(ServiceStatus.isAbsent(null, 7))
    }
    
}