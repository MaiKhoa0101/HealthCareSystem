package com.hellodoc.healthcaresystem.viewmodel;

import android.content.SharedPreferences;
import com.hellodoc.healthcaresystem.model.repository.ReportRepository;
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
public final class ReportViewModel_Factory implements Factory<ReportViewModel> {
  private final Provider<ReportRepository> repositoryProvider;

  private final Provider<SharedPreferences> sharedPreferencesProvider;

  private ReportViewModel_Factory(Provider<ReportRepository> repositoryProvider,
      Provider<SharedPreferences> sharedPreferencesProvider) {
    this.repositoryProvider = repositoryProvider;
    this.sharedPreferencesProvider = sharedPreferencesProvider;
  }

  @Override
  public ReportViewModel get() {
    return newInstance(repositoryProvider.get(), sharedPreferencesProvider.get());
  }

  public static ReportViewModel_Factory create(Provider<ReportRepository> repositoryProvider,
      Provider<SharedPreferences> sharedPreferencesProvider) {
    return new ReportViewModel_Factory(repositoryProvider, sharedPreferencesProvider);
  }

  public static ReportViewModel newInstance(ReportRepository repository,
      SharedPreferences sharedPreferences) {
    return new ReportViewModel(repository, sharedPreferences);
  }
}
