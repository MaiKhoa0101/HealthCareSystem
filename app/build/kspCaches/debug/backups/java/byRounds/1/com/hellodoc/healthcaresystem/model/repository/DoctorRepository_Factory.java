package com.hellodoc.healthcaresystem.model.repository;

import com.hellodoc.healthcaresystem.api.DoctorService;
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
public final class DoctorRepository_Factory implements Factory<DoctorRepository> {
  private final Provider<DoctorService> doctorServiceProvider;

  private DoctorRepository_Factory(Provider<DoctorService> doctorServiceProvider) {
    this.doctorServiceProvider = doctorServiceProvider;
  }

  @Override
  public DoctorRepository get() {
    return newInstance(doctorServiceProvider.get());
  }

  public static DoctorRepository_Factory create(Provider<DoctorService> doctorServiceProvider) {
    return new DoctorRepository_Factory(doctorServiceProvider);
  }

  public static DoctorRepository newInstance(DoctorService doctorService) {
    return new DoctorRepository(doctorService);
  }
}
