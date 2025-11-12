package com.hellodoc.healthcaresystem.viewmodel;

import android.content.SharedPreferences;
import com.hellodoc.healthcaresystem.model.repository.UserRepository;
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
public final class UserViewModel_Factory implements Factory<UserViewModel> {
  private final Provider<UserRepository> repositoryProvider;

  private final Provider<SharedPreferences> sharedPreferencesProvider;

  private UserViewModel_Factory(Provider<UserRepository> repositoryProvider,
      Provider<SharedPreferences> sharedPreferencesProvider) {
    this.repositoryProvider = repositoryProvider;
    this.sharedPreferencesProvider = sharedPreferencesProvider;
  }

  @Override
  public UserViewModel get() {
    return newInstance(repositoryProvider.get(), sharedPreferencesProvider.get());
  }

  public static UserViewModel_Factory create(Provider<UserRepository> repositoryProvider,
      Provider<SharedPreferences> sharedPreferencesProvider) {
    return new UserViewModel_Factory(repositoryProvider, sharedPreferencesProvider);
  }

  public static UserViewModel newInstance(UserRepository repository,
      SharedPreferences sharedPreferences) {
    return new UserViewModel(repository, sharedPreferences);
  }
}
