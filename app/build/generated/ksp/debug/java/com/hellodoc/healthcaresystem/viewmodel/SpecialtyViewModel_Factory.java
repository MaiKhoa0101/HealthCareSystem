package com.hellodoc.healthcaresystem.viewmodel;

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

  private SpecialtyViewModel_Factory(Provider<SpecialtyRepository> specialtyRepositoryProvider) {
    this.specialtyRepositoryProvider = specialtyRepositoryProvider;
  }

  @Override
  public SpecialtyViewModel get() {
    return newInstance(specialtyRepositoryProvider.get());
  }

  public static SpecialtyViewModel_Factory create(
      Provider<SpecialtyRepository> specialtyRepositoryProvider) {
    return new SpecialtyViewModel_Factory(specialtyRepositoryProvider);
  }

  public static SpecialtyViewModel newInstance(SpecialtyRepository specialtyRepository) {
    return new SpecialtyViewModel(specialtyRepository);
  }
}
