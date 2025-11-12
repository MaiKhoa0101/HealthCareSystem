package com.hellodoc.healthcaresystem.model.repository;

import android.content.SharedPreferences;
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

  private final Provider<SharedPreferences> sharedPreferencesProvider;

  private GeminiRepository_Factory(Provider<GeminiService> geminiServiceProvider,
      Provider<SharedPreferences> sharedPreferencesProvider) {
    this.geminiServiceProvider = geminiServiceProvider;
    this.sharedPreferencesProvider = sharedPreferencesProvider;
  }

  @Override
  public GeminiRepository get() {
    return newInstance(geminiServiceProvider.get(), sharedPreferencesProvider.get());
  }

  public static GeminiRepository_Factory create(Provider<GeminiService> geminiServiceProvider,
      Provider<SharedPreferences> sharedPreferencesProvider) {
    return new GeminiRepository_Factory(geminiServiceProvider, sharedPreferencesProvider);
  }

  public static GeminiRepository newInstance(GeminiService geminiService,
      SharedPreferences sharedPreferences) {
    return new GeminiRepository(geminiService, sharedPreferences);
  }
}
