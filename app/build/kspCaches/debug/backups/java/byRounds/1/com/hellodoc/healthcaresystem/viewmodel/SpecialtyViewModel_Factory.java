package com.hellodoc.healthcaresystem.viewmodel;

import android.content.SharedPreferences;
import com.hellodoc.healthcaresystem.model.repository.SpecialtyRepository;
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
public final class SpecialtyViewModel_Factory implements Factory<SpecialtyViewModel> {
  private final Provider<SpecialtyRepository> specialtyRepositoryProvider;

  private final Provider<SharedPreferences> sharedPreferencesProvider;

  private SpecialtyViewModel_Factory(Provider<SpecialtyRepository> specialtyRepositoryProvider,
      Provider<SharedPreferences> sharedPreferencesProvider) {
    this.specialtyRepositoryProvider = specialtyRepositoryProvider;
    this.sharedPreferencesProvider = sharedPreferencesProvider;
  }

  @Override
  public SpecialtyViewModel get() {
    return newInstance(specialtyRepositoryProvider.get(), sharedPreferencesProvider.get());
  }

  public static SpecialtyViewModel_Factory create(
      Provider<SpecialtyRepository> specialtyRepositoryProvider,
      Provider<SharedPreferences> sharedPreferencesProvider) {
    return new SpecialtyViewModel_Factory(specialtyRepositoryProvider, sharedPreferencesProvider);
  }

  public static SpecialtyViewModel newInstance(SpecialtyRepository specialtyRepository,
      SharedPreferences sharedPreferences) {
    return new SpecialtyViewModel(specialtyRepository, sharedPreferences);
  }
}
