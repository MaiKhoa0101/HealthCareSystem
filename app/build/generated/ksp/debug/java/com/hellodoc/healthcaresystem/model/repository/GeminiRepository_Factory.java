package com.hellodoc.healthcaresystem.model.repository;

import com.hellodoc.healthcaresystem.api.GeminiService;
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
public final class GeminiRepository_Factory implements Factory<GeminiRepository> {
  private final Provider<GeminiService> geminiServiceProvider;

  private GeminiRepository_Factory(Provider<GeminiService> geminiServiceProvider) {
    this.geminiServiceProvider = geminiServiceProvider;
  }

  @Override
  public GeminiRepository get() {
    return newInstance(geminiServiceProvider.get());
  }

  public static GeminiRepository_Factory create(Provider<GeminiService> geminiServiceProvider) {
    return new GeminiRepository_Factory(geminiServiceProvider);
  }

  public static GeminiRepository newInstance(GeminiService geminiService) {
    return new GeminiRepository(geminiService);
  }
}
