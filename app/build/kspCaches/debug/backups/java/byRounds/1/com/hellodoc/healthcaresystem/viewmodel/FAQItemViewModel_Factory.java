package com.hellodoc.healthcaresystem.viewmodel;

import android.content.SharedPreferences;
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

  private final Provider<SharedPreferences> sharedPreferencesProvider;

  private FAQItemViewModel_Factory(Provider<FAQItemRepository> faqItemRepositoryProvider,
      Provider<SharedPreferences> sharedPreferencesProvider) {
    this.faqItemRepositoryProvider = faqItemRepositoryProvider;
    this.sharedPreferencesProvider = sharedPreferencesProvider;
  }

  @Override
  public FAQItemViewModel get() {
    return newInstance(faqItemRepositoryProvider.get(), sharedPreferencesProvider.get());
  }

  public static FAQItemViewModel_Factory create(
      Provider<FAQItemRepository> faqItemRepositoryProvider,
      Provider<SharedPreferences> sharedPreferencesProvider) {
    return new FAQItemViewModel_Factory(faqItemRepositoryProvider, sharedPreferencesProvider);
  }

  public static FAQItemViewModel newInstance(FAQItemRepository faqItemRepository,
      SharedPreferences sharedPreferences) {
    return new FAQItemViewModel(faqItemRepository, sharedPreferences);
  }
}
