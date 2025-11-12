package com.hellodoc.healthcaresystem.viewmodel;

import android.content.SharedPreferences;
import com.hellodoc.healthcaresystem.model.repository.NotificationRepository;
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
public final class NotificationViewModel_Factory implements Factory<NotificationViewModel> {
  private final Provider<SharedPreferences> sharedPreferencesProvider;

  private final Provider<NotificationRepository> notificationRepositoryProvider;

  private NotificationViewModel_Factory(Provider<SharedPreferences> sharedPreferencesProvider,
      Provider<NotificationRepository> notificationRepositoryProvider) {
    this.sharedPreferencesProvider = sharedPreferencesProvider;
    this.notificationRepositoryProvider = notificationRepositoryProvider;
  }

  @Override
  public NotificationViewModel get() {
    return newInstance(sharedPreferencesProvider.get(), notificationRepositoryProvider.get());
  }

  public static NotificationViewModel_Factory create(
      Provider<SharedPreferences> sharedPreferencesProvider,
      Provider<NotificationRepository> notificationRepositoryProvider) {
    return new NotificationViewModel_Factory(sharedPreferencesProvider, notificationRepositoryProvider);
  }

  public static NotificationViewModel newInstance(SharedPreferences sharedPreferences,
      NotificationRepository notificationRepository) {
    return new NotificationViewModel(sharedPreferences, notificationRepository);
  }
}
