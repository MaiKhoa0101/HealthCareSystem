package com.hellodoc.healthcaresystem.model.repository;

import com.hellodoc.healthcaresystem.api.NotificationService;
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
public final class NotificationRepository_Factory implements Factory<NotificationRepository> {
  private final Provider<NotificationService> notificationServiceProvider;

  private NotificationRepository_Factory(
      Provider<NotificationService> notificationServiceProvider) {
    this.notificationServiceProvider = notificationServiceProvider;
  }

  @Override
  public NotificationRepository get() {
    return newInstance(notificationServiceProvider.get());
  }

  public static NotificationRepository_Factory create(
      Provider<NotificationService> notificationServiceProvider) {
    return new NotificationRepository_Factory(notificationServiceProvider);
  }

  public static NotificationRepository newInstance(NotificationService notificationService) {
    return new NotificationRepository(notificationService);
  }
}
