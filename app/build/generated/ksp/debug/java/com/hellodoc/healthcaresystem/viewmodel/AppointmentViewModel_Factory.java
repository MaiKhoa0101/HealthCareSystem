package com.hellodoc.healthcaresystem.viewmodel;

import com.hellodoc.healthcaresystem.model.repository.AppointmentRepository;
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
public final class AppointmentViewModel_Factory implements Factory<AppointmentViewModel> {
  private final Provider<AppointmentRepository> appointmentRepositoryProvider;

  private AppointmentViewModel_Factory(
      Provider<AppointmentRepository> appointmentRepositoryProvider) {
    this.appointmentRepositoryProvider = appointmentRepositoryProvider;
  }

  @Override
  public AppointmentViewModel get() {
    return newInstance(appointmentRepositoryProvider.get());
  }

  public static AppointmentViewModel_Factory create(
      Provider<AppointmentRepository> appointmentRepositoryProvider) {
    return new AppointmentViewModel_Factory(appointmentRepositoryProvider);
  }

  public static AppointmentViewModel newInstance(AppointmentRepository appointmentRepository) {
    return new AppointmentViewModel(appointmentRepository);
  }
}
