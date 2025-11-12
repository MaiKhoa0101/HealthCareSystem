package com.hellodoc.healthcaresystem.model.retrofit;

import com.hellodoc.healthcaresystem.api.SpecialtyService;
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
public final class RetrofitInstance_ProvideSpecialtyServiceFactory implements Factory<SpecialtyService> {
  private final Provider<Retrofit> retrofitProvider;

  private RetrofitInstance_ProvideSpecialtyServiceFactory(Provider<Retrofit> retrofitProvider) {
    this.retrofitProvider = retrofitProvider;
  }

  @Override
  public SpecialtyService get() {
    return provideSpecialtyService(retrofitProvider.get());
  }

  public static RetrofitInstance_ProvideSpecialtyServiceFactory create(
      Provider<Retrofit> retrofitProvider) {
    return new RetrofitInstance_ProvideSpecialtyServiceFactory(retrofitProvider);
  }

  public static SpecialtyService provideSpecialtyService(Retrofit retrofit) {
    return Preconditions.checkNotNullFromProvides(RetrofitInstance.INSTANCE.provideSpecialtyService(retrofit));
  }
}
