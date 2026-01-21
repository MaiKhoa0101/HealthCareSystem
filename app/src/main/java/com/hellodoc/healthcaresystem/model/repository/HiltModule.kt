package com.hellodoc.healthcaresystem.model.repository

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    abstract fun bindAdminRepository(
        impl: AdminRepositoryImpl
    ): AdminRepository

    @Binds
    abstract fun bindPostRepository(
        impl: PostRepositoryImpl
    ): PostRepository


    @Binds
    abstract fun bindDoctorRepository(
        impl: DoctorRepositoryImpl
    ): DoctorRepository


    @Binds
    abstract fun bindMedicalOptionRepository(
        impl: MedicalOptionRepositoryImpl
    ): MedicalOptionRepository

    @Binds
    abstract fun bindSpecialtyRepository(
        impl: SpecialtyRepositoryImpl
    ): SpecialtyRepository


    @Binds
    abstract fun bindAppointmentRepository(
        impl: AppointmentRepositoryImpl
    ): AppointmentRepository

    @Binds
    abstract fun bindNotificationRepository(
        impl: NotificationRepositoryImpl
    ): NotificationRepository

    @Binds
    abstract fun bindReportRepository(
        impl: ReportRepositoryImpl
    ): ReportRepository

    @Binds
    abstract fun bindReviewRepository(
        impl: ReviewRepositoryImpl
    ): ReviewRepository

    @Binds
    abstract fun bindGeminiRepository(
        impl: GeminiRepositoryImpl
    ): GeminiRepository



    @Binds
    abstract fun bindSubtitleRepository(
        impl: SubtitleRepositoryImpl
    ): SubtitleRepository

    @Binds
    abstract fun bindFastTalkRepository(
        impl: FastTalkRepositoryImpl
    ): FastTalkRepository


    @Binds
    abstract fun bindUserRepository(
        impl: UserRepositoryImpl
    ): UserRepository

    @Binds
    abstract fun bindNewsRepository(
        impl: NewsRepositoryImpl
    ): NewsRepository

    @Binds
    abstract fun bindSettingsRepository(
        impl: SettingsRepositoryImpl
    ): SettingsRepository



}
