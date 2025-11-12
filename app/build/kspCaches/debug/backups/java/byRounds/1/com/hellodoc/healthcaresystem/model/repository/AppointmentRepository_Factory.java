package com.hellodoc.healthcaresystem.model.repository;

import com.hellodoc.healthcaresystem.api.AppointmentService;
import com.hellodoc.healthcaresystem.roomDb.data.dao.AppointmentDao;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Provider;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

@ScopeMetadata
@QualifierMetadata
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava",
    "cast",
    "deprecation",
    "nullness:initialization.field.uninitialized"
})
public final class AppointmentRepository_Factory implements Factory<AppointmentRepository> {
  private final Provider<AppointmentDao> appointmentDaoProvider;

  private final Provider<AppointmentService> appointmentServiceProvider;

  private AppointmentRepository_Factory(Provider<AppointmentDao> appointmentDaoProvider,
      Provider<AppointmentService> appointmentServiceProvider) {
    this.appointmentDaoProvider = appointmentDaoProvider;
    this.appointmentServiceProvider = appointmentServiceProvider;
  }

  @Override
  public AppointmentRepository get() {
    return newInstance(appointmentDaoProvider.get(), appointmentServiceProvider.get());
  }

  public static AppointmentRepository_Factory create(
      Provider<AppointmentDao> appointmentDaoProvider,
      Provider<AppointmentService> appointmentServiceProvider) {
    return new AppointmentRepository_Factory(appointmentDaoProvider, appointmentServiceProvider);
  }

  public static AppointmentRepository newInstance(AppointmentDao appointmentDao,
      AppointmentService appointmentService) {
    return new AppointmentRepository(appointmentDao, appointmentService);
  }
}
