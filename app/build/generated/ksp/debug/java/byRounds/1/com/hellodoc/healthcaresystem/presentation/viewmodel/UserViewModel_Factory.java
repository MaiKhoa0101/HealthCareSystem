package com.hellodoc.healthcaresystem.presentation.viewmodel;

import android.content.SharedPreferences;
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
  private final Provider<SharedPreferences> sharedPreferencesProvider;

  private UserViewModel_Factory(Provider<SharedPreferences> sharedPreferencesProvider) {
    this.sharedPreferencesProvider = sharedPreferencesProvider;
  }

  @Override
  public UserViewModel get() {
    return newInstance(sharedPreferencesProvider.get());
  }

  public static UserViewModel_Factory create(
      Provider<SharedPreferences> sharedPreferencesProvider) {
    return new UserViewModel_Factory(sharedPreferencesProvider);
  }

  public static UserViewModel newInstance(SharedPreferences sharedPreferences) {
    return new UserViewModel(sharedPreferences);
  }
}
