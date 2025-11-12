package com.hellodoc.healthcaresystem.model.repository;

import com.hellodoc.healthcaresystem.api.ReviewService;
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
public final class ReviewRepository_Factory implements Factory<ReviewRepository> {
  private final Provider<ReviewService> reviewServiceProvider;

  private ReviewRepository_Factory(Provider<ReviewService> reviewServiceProvider) {
    this.reviewServiceProvider = reviewServiceProvider;
  }

  @Override
  public ReviewRepository get() {
    return newInstance(reviewServiceProvider.get());
  }

  public static ReviewRepository_Factory create(Provider<ReviewService> reviewServiceProvider) {
    return new ReviewRepository_Factory(reviewServiceProvider);
  }

  public static ReviewRepository newInstance(ReviewService reviewService) {
    return new ReviewRepository(reviewService);
  }
}
