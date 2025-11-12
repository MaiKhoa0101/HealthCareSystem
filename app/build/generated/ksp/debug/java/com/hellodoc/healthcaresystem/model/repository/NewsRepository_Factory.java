package com.hellodoc.healthcaresystem.model.repository;

import com.hellodoc.healthcaresystem.api.NewsService;
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
public final class NewsRepository_Factory implements Factory<NewsRepository> {
  private final Provider<NewsService> newsServiceProvider;

  private NewsRepository_Factory(Provider<NewsService> newsServiceProvider) {
    this.newsServiceProvider = newsServiceProvider;
  }

  @Override
  public NewsRepository get() {
    return newInstance(newsServiceProvider.get());
  }

  public static NewsRepository_Factory create(Provider<NewsService> newsServiceProvider) {
    return new NewsRepository_Factory(newsServiceProvider);
  }

  public static NewsRepository newInstance(NewsService newsService) {
    return new NewsRepository(newsService);
  }
}
