package com.hellodoc.healthcaresystem.viewmodel;

import android.content.SharedPreferences;
import com.hellodoc.healthcaresystem.model.repository.AppointmentRepository;
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
public final class AppointmentViewModel_Factory implements Factory<AppointmentViewModel> {
  private final Provider<AppointmentRepository> appointmentRepositoryProvider;

  private final Provider<SharedPreferences> sharedPreferencesProvider;

  private final Provider<AppointmentDao> appointmentDaoProvider;

  private AppointmentViewModel_Factory(
      Provider<AppointmentRepository> appointmentRepositoryProvider,
      Provider<SharedPreferences> sharedPreferencesProvider,
      Provider<AppointmentDao> appointmentDaoProvider) {
    this.appointmentRepositoryProvider = appointmentRepositoryProvider;
    this.sharedPreferencesProvider = sharedPreferencesProvider;
    this.appointmentDaoProvider = appointmentDaoProvider;
  }

  @Override
  public AppointmentViewModel get() {
    return newInstance(appointmentRepositoryProvider.get(), sharedPreferencesProvider.get(), appointmentDaoProvider.get());
  }

  public static AppointmentViewModel_Factory create(
      Provider<AppointmentRepository> appointmentRepositoryProvider,
      Provider<SharedPreferences> sharedPreferencesProvider,
      Provider<AppointmentDao> appointmentDaoProvider) {
    return new AppointmentViewModel_Factory(appointmentRepositoryProvider, sharedPreferencesProvider, appointmentDaoProvider);
  }

  public static AppointmentViewModel newInstance(AppointmentRepository appointmentRepository,
      SharedPreferences sharedPreferences, AppointmentDao appointmentDao) {
    return new AppointmentViewModel(appointmentRepository, sharedPreferences, appointmentDao);
  }
}
