package com.hellodoc.healthcaresystem.model.repository;

import com.hellodoc.healthcaresystem.api.SpecialtyService;
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
public final class SpecialtyRepository_Factory implements Factory<SpecialtyRepository> {
  private final Provider<SpecialtyService> specialtyServiceProvider;

  private SpecialtyRepository_Factory(Provider<SpecialtyService> specialtyServiceProvider) {
    this.specialtyServiceProvider = specialtyServiceProvider;
  }

  @Override
  public SpecialtyRepository get() {
    return newInstance(specialtyServiceProvider.get());
  }

  public static SpecialtyRepository_Factory create(
      Provider<SpecialtyService> specialtyServiceProvider) {
    return new SpecialtyRepository_Factory(specialtyServiceProvider);
  }

  public static SpecialtyRepository newInstance(SpecialtyService specialtyService) {
    return new SpecialtyRepository(specialtyService);
  }
}
