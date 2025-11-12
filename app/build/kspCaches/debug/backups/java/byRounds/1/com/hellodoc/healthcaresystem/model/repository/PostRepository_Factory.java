package com.hellodoc.healthcaresystem.model.repository;

import com.hellodoc.healthcaresystem.api.PostService;
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
public final class PostRepository_Factory implements Factory<PostRepository> {
  private final Provider<PostService> postServiceProvider;

  private PostRepository_Factory(Provider<PostService> postServiceProvider) {
    this.postServiceProvider = postServiceProvider;
  }

  @Override
  public PostRepository get() {
    return newInstance(postServiceProvider.get());
  }

  public static PostRepository_Factory create(Provider<PostService> postServiceProvider) {
    return new PostRepository_Factory(postServiceProvider);
  }

  public static PostRepository newInstance(PostService postService) {
    return new PostRepository(postService);
  }
}
