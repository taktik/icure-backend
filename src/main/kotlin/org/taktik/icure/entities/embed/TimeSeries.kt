package org.taktik.icure.entities.embed

data class TimeSeries(
	val fields: List<String> = emptyList(),
	val samples: List<List<Double>> = emptyList(),
	val min: List<Double> = emptyList(),
	val max: List<Double> = emptyList(),
	val mean: List<Double> = emptyList(),
	val median: List<Double> = emptyList(),
	val variance: List<Double> = emptyList(),
)
