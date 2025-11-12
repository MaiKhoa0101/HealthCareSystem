package com.hellodoc.healthcaresystem.viewmodel;

import android.content.SharedPreferences;
import com.hellodoc.healthcaresystem.model.repository.NewsRepository;
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
public final class NewsViewModel_Factory implements Factory<NewsViewModel> {
  private final Provider<NewsRepository> newsRepositoryProvider;

  private final Provider<SharedPreferences> sharedPreferencesProvider;

  private NewsViewModel_Factory(Provider<NewsRepository> newsRepositoryProvider,
      Provider<SharedPreferences> sharedPreferencesProvider) {
    this.newsRepositoryProvider = newsRepositoryProvider;
    this.sharedPreferencesProvider = sharedPreferencesProvider;
  }

  @Override
  public NewsViewModel get() {
    return newInstance(newsRepositoryProvider.get(), sharedPreferencesProvider.get());
  }

  public static NewsViewModel_Factory create(Provider<NewsRepository> newsRepositoryProvider,
      Provider<SharedPreferences> sharedPreferencesProvider) {
    return new NewsViewModel_Factory(newsRepositoryProvider, sharedPreferencesProvider);
  }

  public static NewsViewModel newInstance(NewsRepository newsRepository,
      SharedPreferences sharedPreferences) {
    return new NewsViewModel(newsRepository, sharedPreferences);
  }
}
