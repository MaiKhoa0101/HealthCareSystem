package com.hellodoc.healthcaresystem.model.repository;

import com.hellodoc.healthcaresystem.api.ReportService;
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
public final class ReportRepository_Factory implements Factory<ReportRepository> {
  private final Provider<ReportService> reportServiceProvider;

  private ReportRepository_Factory(Provider<ReportService> reportServiceProvider) {
    this.reportServiceProvider = reportServiceProvider;
  }

  @Override
  public ReportRepository get() {
    return newInstance(reportServiceProvider.get());
  }

  public static ReportRepository_Factory create(Provider<ReportService> reportServiceProvider) {
    return new ReportRepository_Factory(reportServiceProvider);
  }

  public static ReportRepository newInstance(ReportService reportService) {
    return new ReportRepository(reportService);
  }
}
