package com.hellodoc.healthcaresystem.viewmodel;

import android.content.SharedPreferences;
import com.hellodoc.healthcaresystem.model.repository.DoctorRepository;
import com.hellodoc.healthcaresystem.model.repository.PostRepository;
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
public final class GeminiViewModel_Factory implements Factory<GeminiViewModel> {
  private final Provider<SharedPreferences> sharedPreferencesProvider;

  private final Provider<PostRepository> postRepositoryProvider;

  private final Provider<DoctorRepository> doctorRepositoryProvider;

  private GeminiViewModel_Factory(Provider<SharedPreferences> sharedPreferencesProvider,
      Provider<PostRepository> postRepositoryProvider,
      Provider<DoctorRepository> doctorRepositoryProvider) {
    this.sharedPreferencesProvider = sharedPreferencesProvider;
    this.postRepositoryProvider = postRepositoryProvider;
    this.doctorRepositoryProvider = doctorRepositoryProvider;
  }

  @Override
  public GeminiViewModel get() {
    return newInstance(sharedPreferencesProvider.get(), postRepositoryProvider.get(), doctorRepositoryProvider.get());
  }

  public static GeminiViewModel_Factory create(
      Provider<SharedPreferences> sharedPreferencesProvider,
      Provider<PostRepository> postRepositoryProvider,
      Provider<DoctorRepository> doctorRepositoryProvider) {
    return new GeminiViewModel_Factory(sharedPreferencesProvider, postRepositoryProvider, doctorRepositoryProvider);
  }

  public static GeminiViewModel newInstance(SharedPreferences sharedPreferences,
      PostRepository postRepository, DoctorRepository doctorRepository) {
    return new GeminiViewModel(sharedPreferences, postRepository, doctorRepository);
  }
}
