package com.hellodoc.healthcaresystem.model.repository;

import com.hellodoc.healthcaresystem.api.AdminService;
import com.hellodoc.healthcaresystem.api.AuthService;
import com.hellodoc.healthcaresystem.api.UserService;
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
public final class UserRepository_Factory implements Factory<UserRepository> {
  private final Provider<UserService> userServiceProvider;

  private final Provider<AdminService> adminServiceProvider;

  private final Provider<AuthService> authenServiceProvider;

  private UserRepository_Factory(Provider<UserService> userServiceProvider,
      Provider<AdminService> adminServiceProvider, Provider<AuthService> authenServiceProvider) {
    this.userServiceProvider = userServiceProvider;
    this.adminServiceProvider = adminServiceProvider;
    this.authenServiceProvider = authenServiceProvider;
  }

  @Override
  public UserRepository get() {
    return newInstance(userServiceProvider.get(), adminServiceProvider.get(), authenServiceProvider.get());
  }

  public static UserRepository_Factory create(Provider<UserService> userServiceProvider,
      Provider<AdminService> adminServiceProvider, Provider<AuthService> authenServiceProvider) {
    return new UserRepository_Factory(userServiceProvider, adminServiceProvider, authenServiceProvider);
  }

  public static UserRepository newInstance(UserService userService, AdminService adminService,
      AuthService authenService) {
    return new UserRepository(userService, adminService, authenService);
  }
}
