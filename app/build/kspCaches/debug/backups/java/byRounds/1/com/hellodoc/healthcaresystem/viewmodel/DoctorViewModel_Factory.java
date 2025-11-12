package com.hellodoc.healthcaresystem.viewmodel;

import android.content.SharedPreferences;
import com.hellodoc.healthcaresystem.model.repository.AppointmentRepository;
import com.hellodoc.healthcaresystem.model.repository.DoctorRepository;
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
public final class DoctorViewModel_Factory implements Factory<DoctorViewModel> {
  private final Provider<DoctorRepository> doctorRepositoryProvider;

  private final Provider<AppointmentRepository> appointmentRepositoryProvider;

  private final Provider<SharedPreferences> sharedPreferencesProvider;

  private DoctorViewModel_Factory(Provider<DoctorRepository> doctorRepositoryProvider,
      Provider<AppointmentRepository> appointmentRepositoryProvider,
      Provider<SharedPreferences> sharedPreferencesProvider) {
    this.doctorRepositoryProvider = doctorRepositoryProvider;
    this.appointmentRepositoryProvider = appointmentRepositoryProvider;
    this.sharedPreferencesProvider = sharedPreferencesProvider;
  }

  @Override
  public DoctorViewModel get() {
    return newInstance(doctorRepositoryProvider.get(), appointmentRepositoryProvider.get(), sharedPreferencesProvider.get());
  }

  public static DoctorViewModel_Factory create(Provider<DoctorRepository> doctorRepositoryProvider,
      Provider<AppointmentRepository> appointmentRepositoryProvider,
      Provider<SharedPreferences> sharedPreferencesProvider) {
    return new DoctorViewModel_Factory(doctorRepositoryProvider, appointmentRepositoryProvider, sharedPreferencesProvider);
  }

  public static DoctorViewModel newInstance(DoctorRepository doctorRepository,
      AppointmentRepository appointmentRepository, SharedPreferences sharedPreferences) {
    return new DoctorViewModel(doctorRepository, appointmentRepository, sharedPreferences);
  }
}
