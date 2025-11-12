package com.hellodoc.healthcaresystem.model.repository;

import com.hellodoc.healthcaresystem.api.FAQItemService;
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
public final class FAQItemRepository_Factory implements Factory<FAQItemRepository> {
  private final Provider<FAQItemService> faqServiceProvider;

  private FAQItemRepository_Factory(Provider<FAQItemService> faqServiceProvider) {
    this.faqServiceProvider = faqServiceProvider;
  }

  @Override
  public FAQItemRepository get() {
    return newInstance(faqServiceProvider.get());
  }

  public static FAQItemRepository_Factory create(Provider<FAQItemService> faqServiceProvider) {
    return new FAQItemRepository_Factory(faqServiceProvider);
  }

  public static FAQItemRepository newInstance(FAQItemService faqService) {
    return new FAQItemRepository(faqService);
  }
}
