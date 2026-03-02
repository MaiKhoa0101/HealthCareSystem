package com.hellodoc.healthcaresystem.model.hilt

import com.hellodoc.healthcaresystem.model.repository.AdminRepository
import com.hellodoc.healthcaresystem.model.repository.AdminRepositoryImpl
import com.hellodoc.healthcaresystem.model.repository.AppointmentRepository
import com.hellodoc.healthcaresystem.model.repository.AppointmentRepositoryImpl
import com.hellodoc.healthcaresystem.model.repository.DoctorRepository
import com.hellodoc.healthcaresystem.model.repository.DoctorRepositoryImpl
import com.hellodoc.healthcaresystem.model.repository.FastTalkRepository
import com.hellodoc.healthcaresystem.model.repository.FastTalkRepositoryImpl
import com.hellodoc.healthcaresystem.model.repository.GeminiRepository
import com.hellodoc.healthcaresystem.model.repository.GeminiRepositoryImpl
import com.hellodoc.healthcaresystem.model.repository.MedicalOptionRepository
import com.hellodoc.healthcaresystem.model.repository.MedicalOptionRepositoryImpl
import com.hellodoc.healthcaresystem.model.repository.NewsRepository
import com.hellodoc.healthcaresystem.model.repository.NewsRepositoryImpl
import com.hellodoc.healthcaresystem.model.repository.NotificationRepository
import com.hellodoc.healthcaresystem.model.repository.NotificationRepositoryImpl
import com.hellodoc.healthcaresystem.model.repository.PostRepository
import com.hellodoc.healthcaresystem.model.repository.PostRepositoryImpl
import com.hellodoc.healthcaresystem.model.repository.ReportRepository
import com.hellodoc.healthcaresystem.model.repository.ReportRepositoryImpl
import com.hellodoc.healthcaresystem.model.repository.ReviewRepository
import com.hellodoc.healthcaresystem.model.repository.ReviewRepositoryImpl
import com.hellodoc.healthcaresystem.model.repository.SettingsRepository
import com.hellodoc.healthcaresystem.model.repository.SettingsRepositoryImpl
import com.hellodoc.healthcaresystem.model.repository.SpecialtyRepository
import com.hellodoc.healthcaresystem.model.repository.SpecialtyRepositoryImpl
import com.hellodoc.healthcaresystem.model.repository.SubtitleRepository
import com.hellodoc.healthcaresystem.model.repository.SubtitleRepositoryImpl
import com.hellodoc.healthcaresystem.model.repository.UserRepository
import com.hellodoc.healthcaresystem.model.repository.UserRepositoryImpl
import com.hellodoc.healthcaresystem.model.repository.VSLRepository
import com.hellodoc.healthcaresystem.model.repository.VSLRepositoryImpl
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


    @Binds
    abstract fun bindVSLRepository(
        impl: VSLRepositoryImpl
    ): VSLRepository


}
