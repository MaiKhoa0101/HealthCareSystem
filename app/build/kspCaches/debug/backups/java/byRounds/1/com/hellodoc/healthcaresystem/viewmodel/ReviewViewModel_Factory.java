package com.hellodoc.healthcaresystem.viewmodel;

import com.hellodoc.healthcaresystem.model.repository.ReviewRepository;
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
public final class ReviewViewModel_Factory implements Factory<ReviewViewModel> {
  private final Provider<ReviewRepository> reviewRepositoryProvider;

  private ReviewViewModel_Factory(Provider<ReviewRepository> reviewRepositoryProvider) {
    this.reviewRepositoryProvider = reviewRepositoryProvider;
  }

  @Override
  public ReviewViewModel get() {
    return newInstance(reviewRepositoryProvider.get());
  }

  public static ReviewViewModel_Factory create(
      Provider<ReviewRepository> reviewRepositoryProvider) {
    return new ReviewViewModel_Factory(reviewRepositoryProvider);
  }

  public static ReviewViewModel newInstance(ReviewRepository reviewRepository) {
    return new ReviewViewModel(reviewRepository);
  }
}
