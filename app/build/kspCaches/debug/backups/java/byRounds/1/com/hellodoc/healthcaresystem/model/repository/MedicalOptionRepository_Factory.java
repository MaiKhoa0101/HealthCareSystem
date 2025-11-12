package com.hellodoc.healthcaresystem.model.repository;

import com.hellodoc.healthcaresystem.api.MedicalOptionService;
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
public final class MedicalOptionRepository_Factory implements Factory<MedicalOptionRepository> {
  private final Provider<MedicalOptionService> medicalOptionServiceProvider;

  private MedicalOptionRepository_Factory(
      Provider<MedicalOptionService> medicalOptionServiceProvider) {
    this.medicalOptionServiceProvider = medicalOptionServiceProvider;
  }

  @Override
  public MedicalOptionRepository get() {
    return newInstance(medicalOptionServiceProvider.get());
  }

  public static MedicalOptionRepository_Factory create(
      Provider<MedicalOptionService> medicalOptionServiceProvider) {
    return new MedicalOptionRepository_Factory(medicalOptionServiceProvider);
  }

  public static MedicalOptionRepository newInstance(MedicalOptionService medicalOptionService) {
    return new MedicalOptionRepository(medicalOptionService);
  }
}
