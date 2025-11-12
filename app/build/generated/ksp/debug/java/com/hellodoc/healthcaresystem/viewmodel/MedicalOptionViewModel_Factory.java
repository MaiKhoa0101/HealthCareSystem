package com.hellodoc.healthcaresystem.viewmodel;

import com.hellodoc.healthcaresystem.model.repository.MedicalOptionRepository;
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
public final class MedicalOptionViewModel_Factory implements Factory<MedicalOptionViewModel> {
  private final Provider<MedicalOptionRepository> medicalOptionRepositoryProvider;

  private MedicalOptionViewModel_Factory(
      Provider<MedicalOptionRepository> medicalOptionRepositoryProvider) {
    this.medicalOptionRepositoryProvider = medicalOptionRepositoryProvider;
  }

  @Override
  public MedicalOptionViewModel get() {
    return newInstance(medicalOptionRepositoryProvider.get());
  }

  public static MedicalOptionViewModel_Factory create(
      Provider<MedicalOptionRepository> medicalOptionRepositoryProvider) {
    return new MedicalOptionViewModel_Factory(medicalOptionRepositoryProvider);
  }

  public static MedicalOptionViewModel newInstance(
      MedicalOptionRepository medicalOptionRepository) {
    return new MedicalOptionViewModel(medicalOptionRepository);
  }
}
