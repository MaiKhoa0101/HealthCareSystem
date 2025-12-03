package com.hellodoc.healthcaresystem.model.dataclass.responsemodel.modeluser

import androidx.annotation.DrawableRes

data class ProfileUser(
    val name: String,
    val title: String,
    @DrawableRes val image: Int,
    val butProf: String,
    val butSchedule: String,
    val nExper: Int,
    val exper: String,
    val nPatient: Int,
    val patient: String,
    val nRate: Int,
    val rate: String,
    val role: String
)