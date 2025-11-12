package com.hellodoc.healthcaresystem.viewmodel;

import com.hellodoc.healthcaresystem.model.repository.GeminiRepository;
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
public final class PostViewModel_Factory implements Factory<PostViewModel> {
  private final Provider<PostRepository> postRepositoryProvider;

  private final Provider<GeminiRepository> geminiRepositoryProvider;

  private PostViewModel_Factory(Provider<PostRepository> postRepositoryProvider,
      Provider<GeminiRepository> geminiRepositoryProvider) {
    this.postRepositoryProvider = postRepositoryProvider;
    this.geminiRepositoryProvider = geminiRepositoryProvider;
  }

  @Override
  public PostViewModel get() {
    return newInstance(postRepositoryProvider.get(), geminiRepositoryProvider.get());
  }

  public static PostViewModel_Factory create(Provider<PostRepository> postRepositoryProvider,
      Provider<GeminiRepository> geminiRepositoryProvider) {
    return new PostViewModel_Factory(postRepositoryProvider, geminiRepositoryProvider);
  }

  public static PostViewModel newInstance(PostRepository postRepository,
      GeminiRepository geminiRepository) {
    return new PostViewModel(postRepository, geminiRepository);
  }
}
