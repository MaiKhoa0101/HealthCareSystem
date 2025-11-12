package com.hellodoc.healthcaresystem.model.retrofit;

import com.hellodoc.healthcaresystem.api.GeminiService;
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
public final class RetrofitInstance_ProvideGeminiServiceFactory implements Factory<GeminiService> {
  private final Provider<Retrofit> retrofitProvider;

  private RetrofitInstance_ProvideGeminiServiceFactory(Provider<Retrofit> retrofitProvider) {
    this.retrofitProvider = retrofitProvider;
  }

  @Override
  public GeminiService get() {
    return provideGeminiService(retrofitProvider.get());
  }

  public static RetrofitInstance_ProvideGeminiServiceFactory create(
      Provider<Retrofit> retrofitProvider) {
    return new RetrofitInstance_ProvideGeminiServiceFactory(retrofitProvider);
  }

  public static GeminiService provideGeminiService(Retrofit retrofit) {
    return Preconditions.checkNotNullFromProvides(RetrofitInstance.INSTANCE.provideGeminiService(retrofit));
  }
}
