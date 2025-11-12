package com.hellodoc.healthcaresystem.model.retrofit;

import com.hellodoc.healthcaresystem.api.MedicalOptionService;
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
public final class RetrofitInstance_ProvideMedicalOptionServiceFactory implements Factory<MedicalOptionService> {
  private final Provider<Retrofit> retrofitProvider;

  private RetrofitInstance_ProvideMedicalOptionServiceFactory(Provider<Retrofit> retrofitProvider) {
    this.retrofitProvider = retrofitProvider;
  }

  @Override
  public MedicalOptionService get() {
    return provideMedicalOptionService(retrofitProvider.get());
  }

  public static RetrofitInstance_ProvideMedicalOptionServiceFactory create(
      Provider<Retrofit> retrofitProvider) {
    return new RetrofitInstance_ProvideMedicalOptionServiceFactory(retrofitProvider);
  }

  public static MedicalOptionService provideMedicalOptionService(Retrofit retrofit) {
    return Preconditions.checkNotNullFromProvides(RetrofitInstance.INSTANCE.provideMedicalOptionService(retrofit));
  }
}
