/*
 * Copyright (c) 2020. Taktik SA, All rights reserved.
 */

package org.taktik.icure.services.external.rest.v1.mapper.utils;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import java.time.Instant;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public abstract class InstantMapper {
    Long map(Instant instant) { return instant.toEpochMilli(); }
    Instant map(Long aLong) { return Instant.ofEpochMilli(aLong); }
}
