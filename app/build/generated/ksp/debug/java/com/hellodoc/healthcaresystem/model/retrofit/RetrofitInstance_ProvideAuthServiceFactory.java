package com.hellodoc.healthcaresystem.model.retrofit;

import com.hellodoc.healthcaresystem.api.AuthService;
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
public final class RetrofitInstance_ProvideAuthServiceFactory implements Factory<AuthService> {
  private final Provider<Retrofit> retrofitProvider;

  private RetrofitInstance_ProvideAuthServiceFactory(Provider<Retrofit> retrofitProvider) {
    this.retrofitProvider = retrofitProvider;
  }

  @Override
  public AuthService get() {
    return provideAuthService(retrofitProvider.get());
  }

  public static RetrofitInstance_ProvideAuthServiceFactory create(
      Provider<Retrofit> retrofitProvider) {
    return new RetrofitInstance_ProvideAuthServiceFactory(retrofitProvider);
  }

  public static AuthService provideAuthService(Retrofit retrofit) {
    return Preconditions.checkNotNullFromProvides(RetrofitInstance.INSTANCE.provideAuthService(retrofit));
  }
}
