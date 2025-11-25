package com.miskidev.miskiparqueo.feature.map.domain.usecases

import com.miskidev.miskiparqueo.feature.map.domain.repository.ILocationRepository

class GetCurrentLocationUseCase(
    private val repository: ILocationRepository
) {
    suspend operator fun invoke() = repository.getCurrentLocation()
}