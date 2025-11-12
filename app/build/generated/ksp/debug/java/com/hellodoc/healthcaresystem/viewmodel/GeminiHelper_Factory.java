package com.hellodoc.healthcaresystem.viewmodel;

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
public final class GeminiHelper_Factory implements Factory<GeminiHelper> {
  private final Provider<PostRepository> postRepositoryProvider;

  private GeminiHelper_Factory(Provider<PostRepository> postRepositoryProvider) {
    this.postRepositoryProvider = postRepositoryProvider;
  }

  @Override
  public GeminiHelper get() {
    return newInstance(postRepositoryProvider.get());
  }

  public static GeminiHelper_Factory create(Provider<PostRepository> postRepositoryProvider) {
    return new GeminiHelper_Factory(postRepositoryProvider);
  }

  public static GeminiHelper newInstance(PostRepository postRepository) {
    return new GeminiHelper(postRepository);
  }
}
