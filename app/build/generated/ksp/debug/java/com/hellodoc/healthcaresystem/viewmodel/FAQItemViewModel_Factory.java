package com.hellodoc.healthcaresystem.viewmodel;

import com.hellodoc.healthcaresystem.model.repository.FAQItemRepository;
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
public final class FAQItemViewModel_Factory implements Factory<FAQItemViewModel> {
  private final Provider<FAQItemRepository> faqItemRepositoryProvider;

  private FAQItemViewModel_Factory(Provider<FAQItemRepository> faqItemRepositoryProvider) {
    this.faqItemRepositoryProvider = faqItemRepositoryProvider;
  }

  @Override
  public FAQItemViewModel get() {
    return newInstance(faqItemRepositoryProvider.get());
  }

  public static FAQItemViewModel_Factory create(
      Provider<FAQItemRepository> faqItemRepositoryProvider) {
    return new FAQItemViewModel_Factory(faqItemRepositoryProvider);
  }

  public static FAQItemViewModel newInstance(FAQItemRepository faqItemRepository) {
    return new FAQItemViewModel(faqItemRepository);
  }
}
