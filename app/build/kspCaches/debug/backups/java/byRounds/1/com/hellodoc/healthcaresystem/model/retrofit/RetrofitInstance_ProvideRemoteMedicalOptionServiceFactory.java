package com.hellodoc.healthcaresystem.model.retrofit;

import com.hellodoc.healthcaresystem.api.RemoteMedicalOptionService;
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
public final class RetrofitInstance_ProvideRemoteMedicalOptionServiceFactory implements Factory<RemoteMedicalOptionService> {
  private final Provider<Retrofit> retrofitProvider;

  private RetrofitInstance_ProvideRemoteMedicalOptionServiceFactory(
      Provider<Retrofit> retrofitProvider) {
    this.retrofitProvider = retrofitProvider;
  }

  @Override
  public RemoteMedicalOptionService get() {
    return provideRemoteMedicalOptionService(retrofitProvider.get());
  }

  public static RetrofitInstance_ProvideRemoteMedicalOptionServiceFactory create(
      Provider<Retrofit> retrofitProvider) {
    return new RetrofitInstance_ProvideRemoteMedicalOptionServiceFactory(retrofitProvider);
  }

  public static RemoteMedicalOptionService provideRemoteMedicalOptionService(Retrofit retrofit) {
    return Preconditions.checkNotNullFromProvides(RetrofitInstance.INSTANCE.provideRemoteMedicalOptionService(retrofit));
  }
}
