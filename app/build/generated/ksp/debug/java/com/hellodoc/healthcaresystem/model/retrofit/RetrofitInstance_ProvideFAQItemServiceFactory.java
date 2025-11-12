package com.hellodoc.healthcaresystem.model.retrofit;

import com.hellodoc.healthcaresystem.api.FAQItemService;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.Provider;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import retrofit2.Retrofit;

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
public final class RetrofitInstance_ProvideFAQItemServiceFactory implements Factory<FAQItemService> {
  private final Provider<Retrofit> retrofitProvider;

  private RetrofitInstance_ProvideFAQItemServiceFactory(Provider<Retrofit> retrofitProvider) {
    this.retrofitProvider = retrofitProvider;
  }

  @Override
  public FAQItemService get() {
    return provideFAQItemService(retrofitProvider.get());
  }

  public static RetrofitInstance_ProvideFAQItemServiceFactory create(
      Provider<Retrofit> retrofitProvider) {
    return new RetrofitInstance_ProvideFAQItemServiceFactory(retrofitProvider);
  }

  public static FAQItemService provideFAQItemService(Retrofit retrofit) {
    return Preconditions.checkNotNullFromProvides(RetrofitInstance.INSTANCE.provideFAQItemService(retrofit));
  }
}
