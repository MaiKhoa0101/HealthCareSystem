package com.hellodoc.healthcaresystem.viewmodel;

import android.content.SharedPreferences;
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

  private final Provider<SharedPreferences> sharedPreferencesProvider;

  private MedicalOptionViewModel_Factory(
      Provider<MedicalOptionRepository> medicalOptionRepositoryProvider,
      Provider<SharedPreferences> sharedPreferencesProvider) {
    this.medicalOptionRepositoryProvider = medicalOptionRepositoryProvider;
    this.sharedPreferencesProvider = sharedPreferencesProvider;
  }

  @Override
  public MedicalOptionViewModel get() {
    return newInstance(medicalOptionRepositoryProvider.get(), sharedPreferencesProvider.get());
  }

  public static MedicalOptionViewModel_Factory create(
      Provider<MedicalOptionRepository> medicalOptionRepositoryProvider,
      Provider<SharedPreferences> sharedPreferencesProvider) {
    return new MedicalOptionViewModel_Factory(medicalOptionRepositoryProvider, sharedPreferencesProvider);
  }

  public static MedicalOptionViewModel newInstance(MedicalOptionRepository medicalOptionRepository,
      SharedPreferences sharedPreferences) {
    return new MedicalOptionViewModel(medicalOptionRepository, sharedPreferences);
  }
}
