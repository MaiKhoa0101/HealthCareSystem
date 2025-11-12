package com.hellodoc.healthcaresystem.model.repository;

import com.hellodoc.healthcaresystem.api.AdminService;
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
public final class AdminRepository_Factory implements Factory<AdminRepository> {
  private final Provider<AdminService> adminServiceProvider;

  private AdminRepository_Factory(Provider<AdminService> adminServiceProvider) {
    this.adminServiceProvider = adminServiceProvider;
  }

  @Override
  public AdminRepository get() {
    return newInstance(adminServiceProvider.get());
  }

  public static AdminRepository_Factory create(Provider<AdminService> adminServiceProvider) {
    return new AdminRepository_Factory(adminServiceProvider);
  }

  public static AdminRepository newInstance(AdminService adminService) {
    return new AdminRepository(adminService);
  }
}
