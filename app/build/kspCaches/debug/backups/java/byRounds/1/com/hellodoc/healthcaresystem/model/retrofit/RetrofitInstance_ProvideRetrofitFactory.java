package com.hellodoc.healthcaresystem.model.retrofit;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.Provider;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;

@ScopeMetadata("jakarta.inject.Singleton")
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
public final class RetrofitInstance_ProvideRetrofitFactory implements Factory<Retrofit> {
  private final Provider<OkHttpClient> clientProvider;

  private RetrofitInstance_ProvideRetrofitFactory(Provider<OkHttpClient> clientProvider) {
    this.clientProvider = clientProvider;
  }

  @Override
  public Retrofit get() {
    return provideRetrofit(clientProvider.get());
  }

  public static RetrofitInstance_ProvideRetrofitFactory create(
      Provider<OkHttpClient> clientProvider) {
    return new RetrofitInstance_ProvideRetrofitFactory(clientProvider);
  }

  public static Retrofit provideRetrofit(OkHttpClient client) {
    return Preconditions.checkNotNullFromProvides(RetrofitInstance.INSTANCE.provideRetrofit(client));
  }
}
